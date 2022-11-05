package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

public class BookingMapper {
    public static BookingDto toBookingDto(Booking booking, UserDto userDto, ItemDto itemDto){
        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(booking.getId());
        bookingDto.setStart(booking.getStart());
        bookingDto.setEnd(booking.getEnd());
        bookingDto.setStatus(booking.getStatus());
        bookingDto.setBooker(userDto);
        bookingDto.setItem(itemDto);
        return bookingDto;
    }
}
