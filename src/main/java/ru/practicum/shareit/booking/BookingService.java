package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

public interface BookingService {
    BookingDto createBooking(long userId, Booking booking);

    BookingDto patchBooking(long userId, long bookingId, boolean approved);

    BookingDto findBookingById(long userId, long bookingId);

    List<BookingDto> findAllBookingByUser(long bookerId, String state, Integer from, Integer size);

    List<BookingDto> findAllBookingByOwner(long ownerId, String state, Integer from, Integer size);
}
