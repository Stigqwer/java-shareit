package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.UserService;

@AllArgsConstructor
@Service
public class BookingServiceImpl implements BookingService{
    private final UserService userService;
    private final ItemService itemService;
    private final BookingRepository bookingRepository;

    @Override
    public BookingDto createBooking(long userId, Booking booking) {
        ItemDto itemDto = itemService.findItemById(userId, booking.getItemId());
        if(!itemDto.getAvailable()){
            throw new BookingException(String.format("Вещ с id %d недоступна для бронирования", booking.getItemId()));
        } else if(booking.getEnd().isBefore(booking.getStart())){
            throw new BookingException("Время старта бронирования позже времени окончания бронирования");
        }
        booking.setStatus(Status.WAITING);
        booking.setBookerId(userId);
        return BookingMapper.toBookingDto(bookingRepository.save(booking), userService.findUserById(userId), itemDto);
    }
}
