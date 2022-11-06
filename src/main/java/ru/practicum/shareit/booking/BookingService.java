package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;

public interface BookingService {
    BookingDto createBooking(long userId, Booking booking);
    BookingDto patchBooking(long userId, long bookingId, boolean approved);
    BookingDto findBookingById(long userId, long bookingId);
}
