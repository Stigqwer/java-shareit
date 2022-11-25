package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = BookingController.class)
public class BookingControllerTest {
    @MockBean
    private BookingService bookingService;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    private MockMvc mvc;

    @Test
    void createBooking() throws Exception {
        Booking booking1 = new Booking(1L, LocalDateTime.of(2023, 11, 12, 10, 25),
                LocalDateTime.of(2024, 11, 12, 10, 25),
                Status.WAITING, 1L, 1L);
        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(booking1.getId());
        bookingDto.setStart(booking1.getStart());
        bookingDto.setEnd(booking1.getEnd());
        bookingDto.setStatus(booking1.getStatus());
        when(bookingService.createBooking(anyLong(), any(Booking.class)))
                .thenReturn(bookingDto);

        mvc.perform(post("/bookings")
                        .content(objectMapper.writeValueAsString(booking1))
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.start", notNullValue()))
                .andExpect(jsonPath("$.end", notNullValue()))
                .andExpect(jsonPath("$.status", is(bookingDto.getStatus().toString())));
    }

    @Test
    void patchBooking() throws Exception {
        Booking booking1 = new Booking(1L, LocalDateTime.of(2023, 11, 12, 10, 25),
                LocalDateTime.of(2024, 11, 12, 10, 25),
                Status.APPROVED, 1L, 1L);
        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(booking1.getId());
        bookingDto.setStart(booking1.getStart());
        bookingDto.setEnd(booking1.getEnd());
        bookingDto.setStatus(booking1.getStatus());
        when(bookingService.patchBooking(anyLong(), anyLong(), anyBoolean()))
                .thenReturn(bookingDto);

        mvc.perform(patch("/bookings/{bookingId}", 1)
                        .param("approved", "true")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.start", notNullValue()))
                .andExpect(jsonPath("$.end", notNullValue()))
                .andExpect(jsonPath("$.status", is(bookingDto.getStatus().toString())));
    }

    @Test
    void findBookingById() throws Exception {
        Booking booking1 = new Booking(1L, LocalDateTime.of(2023, 11, 12, 10, 25),
                LocalDateTime.of(2024, 11, 12, 10, 25),
                Status.APPROVED, 1L, 1L);
        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(booking1.getId());
        bookingDto.setStart(booking1.getStart());
        bookingDto.setEnd(booking1.getEnd());
        bookingDto.setStatus(booking1.getStatus());
        when(bookingService.findBookingById(anyLong(), anyLong()))
                .thenReturn(bookingDto);

        mvc.perform(get("/bookings/{bookingId}", 1)
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.start", notNullValue()))
                .andExpect(jsonPath("$.end", notNullValue()))
                .andExpect(jsonPath("$.status", is(bookingDto.getStatus().toString())));
    }

    @Test
    void bookingException() throws Exception {
        when(bookingService.findBookingById(anyLong(), anyLong()))
                .thenThrow(BookingException.class);

        mvc.perform(get("/bookings/{bookingId}", 1)
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is(400));
    }

    @Test
    void bookingNotFoundException() throws Exception {
        when(bookingService.findBookingById(anyLong(), anyLong()))
                .thenThrow(BookingNotFoundException.class);

        mvc.perform(get("/bookings/{bookingId}", 1)
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is(404));
    }

    @Test
    void findAllBookingByUser() throws Exception {
        Booking booking1 = new Booking(1L, LocalDateTime.of(2023, 11, 12, 10, 25),
                LocalDateTime.of(2024, 11, 12, 10, 25),
                Status.APPROVED, 1L, 1L);
        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(booking1.getId());
        bookingDto.setStart(booking1.getStart());
        bookingDto.setEnd(booking1.getEnd());
        bookingDto.setStatus(booking1.getStatus());
        List<BookingDto> bookingDtoList = List.of(bookingDto);
        when(bookingService.findAllBookingByUser(anyLong(), anyString(), any(),any()))
                .thenReturn(bookingDtoList);

        mvc.perform(get("/bookings")
                        .param("state", "ALL")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].start", notNullValue()))
                .andExpect(jsonPath("$[0].end", notNullValue()))
                .andExpect(jsonPath("$[0].status", is(bookingDto.getStatus().toString())));
    }

    @Test
    void findAllBookingByOwner() throws Exception {
        Booking booking1 = new Booking(1L, LocalDateTime.of(2023, 11, 12, 10, 25),
                LocalDateTime.of(2024, 11, 12, 10, 25),
                Status.APPROVED, 1L, 1L);
        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(booking1.getId());
        bookingDto.setStart(booking1.getStart());
        bookingDto.setEnd(booking1.getEnd());
        bookingDto.setStatus(booking1.getStatus());
        List<BookingDto> bookingDtoList = List.of(bookingDto);
        when(bookingService.findAllBookingByOwner(anyLong(), anyString(), any(),any()))
                .thenReturn(bookingDtoList);

        mvc.perform(get("/bookings/owner")
                        .param("state", "ALL")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].start", notNullValue()))
                .andExpect(jsonPath("$[0].end", notNullValue()))
                .andExpect(jsonPath("$[0].status", is(bookingDto.getStatus().toString())));
    }
}
