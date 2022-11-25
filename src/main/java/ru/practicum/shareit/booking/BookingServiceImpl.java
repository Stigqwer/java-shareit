package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class BookingServiceImpl implements BookingService {
    private final UserService userService;
    private final ItemService itemService;
    private final BookingRepository bookingRepository;

    @Override
    public BookingDto createBooking(long userId, Booking booking) {
        ItemDto itemDto = itemService.findItemById(userId, booking.getItemId());
        boolean isUserItem = itemService.findAllItem(userId, null, null).stream()
                .anyMatch(item -> Objects.equals(booking.getItemId(), item.getId()));
        if (isUserItem) {
            throw new BookingNotFoundException("Вы являетесь владельцем данной вещи");
        }
        if (!itemDto.getAvailable()) {
            throw new BookingException(String.format("Вещ с id %d недоступна для бронирования", booking.getItemId()));
        } else if (booking.getEnd().isBefore(booking.getStart())) {
            throw new BookingException("Время старта бронирования позже времени окончания бронирования");
        }
        booking.setStatus(Status.WAITING);
        booking.setBookerId(userId);
        return BookingMapper.toBookingDto(bookingRepository.save(booking), userService.findUserById(userId), itemDto);
    }

    @Override
    public BookingDto patchBooking(long userId, long bookingId, boolean approved) {
        Booking booking = findById(bookingId);
        if (booking.getStatus().equals(Status.APPROVED) && approved) {
            throw new BookingException("Статус бронирования уже подтвержден");
        }
        boolean isUserItem = itemService.findAllItem(userId, null, null).stream()
                .anyMatch(item -> Objects.equals(booking.getItemId(), item.getId()));
        if (isUserItem) {
            if (approved) {
                booking.setStatus(Status.APPROVED);
            } else {
                booking.setStatus(Status.REJECTED);
            }
            return BookingMapper.toBookingDto(bookingRepository.save(booking),
                    userService.findUserById(booking.getBookerId()),
                    itemService.findItemById(userId, booking.getItemId()));
        } else {
            throw new BookingNotFoundException(String.format("У пользователя с id %d нет вещи с id %d",
                    userId, booking.getItemId()));
        }
    }

    @Override
    public BookingDto findBookingById(long userId, long bookingId) {
        Booking booking = findById(bookingId);
        boolean isUserItem = itemService.findAllItem(userId, null, null).stream()
                .anyMatch(item -> Objects.equals(booking.getItemId(), item.getId()));
        if (isUserItem || Objects.equals(userId, booking.getBookerId())) {
            return BookingMapper.toBookingDto(booking,
                    userService.findUserById(booking.getBookerId()),
                    itemService.findItemById(userId, booking.getItemId()));
        } else {
            throw new BookingNotFoundException(String.format(
                    "Пользователь с id %d не имеет отношения к этому бронированию", userId));
        }
    }

    @Override
    public List<BookingDto> findAllBookingByUser(long bookerId, String state, Integer from, Integer size) {
        userService.findUserById(bookerId);
        List<Booking> bookingList;
        if (from == null || size == null) {
            bookingList = bookingRepository.findAllByBookerIdOrderByStartDesc(bookerId);
        } else {
            if (size <= 0) {
                throw new BookingException(String.format("Размер страницы %s", size));
            } else if (from < 0) {
                throw new BookingException("Индекс первого эллемента меньше нуля");
            } else {
                Pageable pageable = PageRequest.of(((from) / size), size);
                bookingList = bookingRepository.findAllByBookerIdOrderByStartDesc(bookerId, pageable);
            }
        }
        bookingList = getBookingByState(state, bookingList);
        return bookingList.stream().map(booking -> BookingMapper.toBookingDto(booking,
                userService.findUserById(bookerId),
                itemService.findItemById(bookerId, booking.getItemId()))).collect(Collectors.toList());
    }

    private List<Booking> getBookingByState(String state, List<Booking> bookingList) {
        switch (state) {
            case "ALL":
                break;
            case "WAITING":
                bookingList = bookingList.stream()
                        .filter(booking -> booking.getStatus() == Status.WAITING).collect(Collectors.toList());
                break;
            case "REJECTED":
                bookingList = bookingList.stream()
                        .filter(booking -> booking.getStatus() == Status.REJECTED).collect(Collectors.toList());
                break;
            case "PAST":
                bookingList = bookingList.stream()
                        .filter(booking -> booking.getEnd().isBefore(LocalDateTime.now())).collect(Collectors.toList());
                break;
            case "FUTURE":
                bookingList = bookingList.stream()
                        .filter(booking -> booking.getStart().isAfter(LocalDateTime.now()))
                        .collect(Collectors.toList());
                break;
            case "CURRENT":
                bookingList = bookingList.stream()
                        .filter(booking -> booking.getStart().isBefore(LocalDateTime.now())
                                && booking.getEnd().isAfter(LocalDateTime.now())).collect(Collectors.toList());
                break;
            default:
                throw new BookingException(String.format("Unknown state: %s", state));
        }
        return bookingList;
    }

    @Override
    public List<BookingDto> findAllBookingByOwner(long ownerId, String state, Integer from, Integer size) {
        userService.findUserById(ownerId);
        List<Long> itemId = itemService.findAllItem(ownerId, null, null)
                .stream().map(ItemDto::getId).collect(Collectors.toList());
        List<Booking> bookingList = new ArrayList<>();
        for (Long id : itemId) {
            if (from == null || size == null) {
                bookingList.addAll(bookingRepository.findAllByItemId(id));
            } else {
                if (size <= 0) {
                    throw new BookingException(String.format("Размер страницы %s", size));
                } else if (from < 0) {
                    throw new BookingException("Индекс первого эллемента меньше нуля");
                } else {
                    Pageable pageable = PageRequest.of(((from) / size), size);
                    bookingList.addAll(bookingRepository.findAllByItemIdOrderByStartDesc(id, pageable));
                }
            }
        }
        bookingList = bookingList.stream().sorted((booking1, booking2)
                -> {
            if (booking1.getStart().isBefore(booking2.getStart())) {
                return 1;
            } else if (booking1.getStart().equals(booking2.getStart())) {
                return 0;
            } else {
                return -1;
            }
        }).collect(Collectors.toList());

        bookingList = getBookingByState(state, bookingList);
        return bookingList.stream().map(booking -> BookingMapper.toBookingDto(booking,
                userService.findUserById(booking.getBookerId()),
                itemService.findItemById(booking.getBookerId(), booking.getItemId()))).collect(Collectors.toList());
    }

    private Booking findById(long bookingId) {
        Optional<Booking> booking = bookingRepository.findById(bookingId);
        if (booking.isPresent()) {
            return booking.get();
        } else {
            throw new BookingNotFoundException(String.format("Бронирование с id %d не найдено", bookingId));
        }
    }
}
