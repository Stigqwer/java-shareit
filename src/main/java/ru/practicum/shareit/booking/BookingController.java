package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;

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
                             @RequestBody @Valid Booking booking) {
        return bookingService.createBooking(userId, booking);
    }

    @PatchMapping("/{bookingId}")
    BookingDto patchBooking(@RequestHeader("X-Sharer-User-Id") @NotEmpty long userId,
                            @PathVariable long bookingId, @RequestParam boolean approved) {
        return bookingService.patchBooking(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    BookingDto findBookingById(@RequestHeader("X-Sharer-User-Id") @NotEmpty long userId,
                               @PathVariable long bookingId) {
        return bookingService.findBookingById(userId, bookingId);
    }

    @GetMapping
    List<BookingDto> findAllBookingByUser(@RequestHeader("X-Sharer-User-Id") @NotEmpty long userId,
                                          @RequestParam(defaultValue = "ALL") String state) {
        return bookingService.findAllBookingByUser(userId, state);
    }

    @GetMapping("/owner")
    List<BookingDto> findAllBookingByOwner(@RequestHeader("X-Sharer-User-Id") @NotEmpty long userId,
                                           @RequestParam(defaultValue = "ALL") String state) {
        return bookingService.findAllBookingByOwner(userId, state);
    }
}
