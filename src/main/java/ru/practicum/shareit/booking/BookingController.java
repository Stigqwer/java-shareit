package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;

/**
 * TODO Sprint add-bookings.
 */
@RestController
@RequestMapping(path = "/bookings")
@AllArgsConstructor
public class BookingController {
   private final BookingService bookingService;

   @PostMapping
    BookingDto createBooking(@RequestHeader("X-Sharer-User-Id") @NotEmpty long userId,
                             @RequestBody @Valid Booking booking){
       return bookingService.createBooking(userId, booking);
   }
}
