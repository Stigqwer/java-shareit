package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
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
        boolean isUserItem = itemService.findAllItem(userId).stream()
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
            throw new BookingException(String.format("У пользователя с id %d нет вещи с id %d",
                    userId, booking.getItemId()));
        }
    }

    @Override
    public BookingDto findBookingById(long userId, long bookingId) {
        Booking booking = findById(bookingId);
        boolean isUserItem = itemService.findAllItem(userId).stream()
                .anyMatch(item -> Objects.equals(booking.getItemId(), item.getId()));
        if (isUserItem || Objects.equals(userId, booking.getBookerId())) {
            return BookingMapper.toBookingDto(booking,
                    userService.findUserById(booking.getBookerId()),
                    itemService.findItemById(userId, booking.getItemId()));
        } else {
            throw new BookingException(String.format
                    ("Пользователь с id %d не имеет отношения к этому бронированию", userId));
        }
    }

    @Override
    public List<BookingDto> findAllBookingByUser(long bookerId, String state) {
        userService.findUserById(bookerId);
        List<Booking> bookingList;
        switch (state) {
            case "ALL":
                bookingList = bookingRepository.findAllByBookerIdOrderByStartDesc(bookerId);
                break;
            case "WAITING":
                bookingList = bookingRepository.findAllByBookerIdOrderByStartDesc(bookerId).stream().
                        filter(booking -> booking.getStatus() == Status.WAITING).collect(Collectors.toList());
                break;
            case "REJECTED":
                bookingList = bookingRepository.findAllByBookerIdOrderByStartDesc(bookerId).stream().
                        filter(booking -> booking.getStatus() == Status.REJECTED).collect(Collectors.toList());
                break;
            case "PAST":
                bookingList = bookingRepository.findAllByBookerIdOrderByStartDesc(bookerId).stream().
                        filter(booking -> booking.getEnd().isBefore(LocalDateTime.now())).collect(Collectors.toList());
                break;
            case "FUTURE":
                bookingList = bookingRepository.findAllByBookerIdOrderByStartDesc(bookerId).stream().
                        filter(booking -> booking.getStart().isAfter(LocalDateTime.now())).collect(Collectors.toList());
                break;
            case "CURRENT":
                bookingList = bookingRepository.findAllByBookerIdOrderByStartDesc(bookerId).stream().
                        filter(booking -> booking.getStart().isBefore(LocalDateTime.now())
                                && booking.getEnd().isAfter(LocalDateTime.now())).collect(Collectors.toList());
                break;
            default:
                throw new BookingException(String.format("Unknown state: %s", state));
        }
        return bookingList.stream().map(booking -> BookingMapper.toBookingDto(booking,
                userService.findUserById(bookerId),
                itemService.findItemById(bookerId, booking.getItemId()))).collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> findAllBookingByOwner(long ownerId, String state) {
        userService.findUserById(ownerId);
        return null;
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
