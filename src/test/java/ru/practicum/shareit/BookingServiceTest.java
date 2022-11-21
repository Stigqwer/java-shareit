package ru.practicum.shareit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import ru.practicum.shareit.booking.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class BookingServiceTest {
    @Mock
    UserService mockUserService;

    @Mock
    ItemService mockItemService;

    @Mock
    BookingRepository mockBookingRepository;

    BookingService bookingService;

    @BeforeEach
    void beforeEach(){
        bookingService = new BookingServiceImpl(mockUserService,mockItemService,mockBookingRepository);
        Mockito.when(mockUserService.findUserById(Mockito.anyLong()))
                .thenReturn(new UserDto(1L, "update", "update@user.com"));
        Mockito.when(mockItemService.findItemById(Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(new ItemDto(1L, "Дрель", "Простая дрель",
                        true, null, null, Collections.emptyList(),null));
    }

    @Test
    void testOkCreateBooking(){
        Booking booking1 = new Booking(1L, LocalDateTime.of(2023, 11, 12, 10, 25),
                LocalDateTime.of(2024, 11, 12, 10, 25),
                Status.WAITING, 1L, 1L);
        Mockito.when(mockBookingRepository.save(Mockito.any(Booking.class)))
                .thenReturn(booking1);

        BookingDto bookingDto = bookingService.createBooking(1L, new Booking(null,
                LocalDateTime.of(2023, 11, 12, 10, 25),
                LocalDateTime.of(2024, 11, 12, 10, 25),
                null, null, 1L));

        Assertions.assertEquals(BookingMapper.toBookingDto(booking1, mockUserService.findUserById(1L),
                mockItemService.findItemById(1L,1L)),bookingDto);
    }

    @Test
    void testCreateBookingByOwner(){
        Booking booking1 = new Booking(null,
                LocalDateTime.of(2023, 11, 12, 10, 25),
                LocalDateTime.of(2024, 11, 12, 10, 25),
                null, null, 1L);
        Mockito.when(mockItemService.findAllItem(Mockito.anyLong(),Mockito.any(), Mockito.any()))
                .thenReturn(List.of(new ItemDto(1L, "Дрель", "Простая дрель",
                        true, null, null, Collections.emptyList(),null)));

        BookingNotFoundException bookingNotFoundException = Assertions.assertThrows(BookingNotFoundException.class,
                () -> bookingService.createBooking(1L, booking1));

        Assertions.assertEquals("Вы являетесь владельцем данной вещи", bookingNotFoundException.getMessage());
    }

    @Test
    void testNotAvailableItemOnCreateBooking(){
        Booking booking1 = new Booking(null,
                LocalDateTime.of(2023, 11, 12, 10, 25),
                LocalDateTime.of(2024, 11, 12, 10, 25),
                null, null, 1L);
        Mockito.when(mockItemService.findItemById(Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(new ItemDto(1L, "Дрель", "Простая дрель",
                        false, null, null, Collections.emptyList(),null));

        BookingException bookingException = Assertions.assertThrows(BookingException.class,
                () -> bookingService.createBooking(1L, booking1));

        Assertions.assertEquals("Вещ с id 1 недоступна для бронирования", bookingException.getMessage());
    }

    @Test
    void testErrorTimeOnCreateBooking(){
        Booking booking1 = new Booking(null,
                LocalDateTime.of(2025, 11, 12, 10, 25),
                LocalDateTime.of(2024, 11, 12, 10, 25),
                null, null, 1L);

        BookingException bookingException = Assertions.assertThrows(BookingException.class,
                () -> bookingService.createBooking(1L, booking1));

        Assertions.assertEquals("Время старта бронирования позже времени окончания бронирования",
                bookingException.getMessage());
    }

    @Test
    void testOkPatchBookingWithApproved(){
        Booking booking1 = new Booking(1L,
                LocalDateTime.of(2023, 11, 12, 10, 25),
                LocalDateTime.of(2024, 11, 12, 10, 25),
                Status.WAITING, 1L, 1L);
        Mockito.when(mockBookingRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(booking1));
        Mockito.when(mockItemService.findAllItem(Mockito.anyLong(),Mockito.any(), Mockito.any()))
                .thenReturn(List.of(new ItemDto(1L, "Дрель", "Простая дрель",
                        true, null, null, Collections.emptyList(),null)));
        Mockito.when(mockBookingRepository.save(Mockito.any(Booking.class)))
                .thenReturn(booking1);

        BookingDto bookingDto = bookingService.patchBooking(1L,1L, true);
        booking1.setStatus(Status.APPROVED);

        Assertions.assertEquals(BookingMapper.toBookingDto(booking1,mockUserService.findUserById(1L),
                mockItemService.findItemById(1L,1L)),bookingDto);
    }

    @Test
    void testOkPatchBookingWithoutApproved(){
        Booking booking1 = new Booking(1L,
                LocalDateTime.of(2023, 11, 12, 10, 25),
                LocalDateTime.of(2024, 11, 12, 10, 25),
                Status.WAITING, 1L, 1L);
        Mockito.when(mockBookingRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(booking1));
        Mockito.when(mockItemService.findAllItem(Mockito.anyLong(),Mockito.any(), Mockito.any()))
                .thenReturn(List.of(new ItemDto(1L, "Дрель", "Простая дрель",
                        true, null, null, Collections.emptyList(),null)));
        Mockito.when(mockBookingRepository.save(Mockito.any(Booking.class)))
                .thenReturn(booking1);

        BookingDto bookingDto = bookingService.patchBooking(1L,1L, false);
        booking1.setStatus(Status.REJECTED);

        Assertions.assertEquals(BookingMapper.toBookingDto(booking1,mockUserService.findUserById(1L),
                mockItemService.findItemById(1L,1L)),bookingDto);
    }

    @Test
    void testStatusApprovedYetOnPatchBooking(){
        Booking booking1 = new Booking(1L,
                LocalDateTime.of(2023, 11, 12, 10, 25),
                LocalDateTime.of(2024, 11, 12, 10, 25),
                Status.APPROVED, 1L, 1L);
        Mockito.when(mockBookingRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(booking1));

        BookingException bookingException = Assertions.assertThrows(BookingException.class,
                () -> bookingService.patchBooking(1L,1L, true));

        Assertions.assertEquals("Статус бронирования уже подтвержден", bookingException.getMessage());
    }

    @Test
    void testOwnerOnPatchBooking(){
        Booking booking1 = new Booking(1L,
                LocalDateTime.of(2023, 11, 12, 10, 25),
                LocalDateTime.of(2024, 11, 12, 10, 25),
                Status.WAITING, 1L, 1L);
        Mockito.when(mockBookingRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(booking1));

        BookingNotFoundException bookingNotFoundException = Assertions.assertThrows(BookingNotFoundException.class,
                () -> bookingService.patchBooking(1L,1L,true));

        Assertions.assertEquals("У пользователя с id 1 нет вещи с id 1", bookingNotFoundException.getMessage());
    }

    @Test
    void testOkFindBookingById(){

    }
}
