package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.UserService;

import java.util.Objects;
import java.util.Optional;

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

    private Booking findById(long bookingId) {
        Optional<Booking> booking = bookingRepository.findById(bookingId);
        if (booking.isPresent()) {
            return booking.get();
        } else {
            throw new BookingNotFoundException(String.format("Бронирование с id %d не найдено", bookingId));
        }
    }
}
