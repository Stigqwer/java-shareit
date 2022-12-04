package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@AllArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingDto createBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                    @RequestBody Booking booking) {
        return bookingService.createBooking(userId, booking);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto patchBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                   @PathVariable long bookingId, @RequestParam boolean approved) {
        return bookingService.patchBooking(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto findBookingById(@RequestHeader("X-Sharer-User-Id") long userId,
                                      @PathVariable long bookingId) {
        return bookingService.findBookingById(userId, bookingId);
    }

    @GetMapping
    public List<BookingDto> findAllBookingByUser(@RequestHeader("X-Sharer-User-Id") long userId,
                                                 @RequestParam(defaultValue = "ALL") String state,
                                                 @RequestParam(defaultValue = "0") Integer from,
                                                 @RequestParam(defaultValue = "10") Integer size) {
        return bookingService.findAllBookingByUser(userId, state, from, size);
    }

    @GetMapping("/owner")
    public List<BookingDto> findAllBookingByOwner(@RequestHeader("X-Sharer-User-Id") long userId,
                                                  @RequestParam(defaultValue = "ALL") String state,
                                                  @RequestParam(defaultValue = "0") Integer from,
                                                  @RequestParam(defaultValue = "10") Integer size) {
        return bookingService.findAllBookingByOwner(userId, state, from, size);
    }
}
