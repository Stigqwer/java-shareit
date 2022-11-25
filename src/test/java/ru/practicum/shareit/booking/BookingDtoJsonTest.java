package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class BookingDtoJsonTest {
    @Autowired
    private JacksonTester<BookingDto> json;

    @Test
    void testBookingDto() throws Exception {
        UserDto userDto = new UserDto(1L,"update", "update@user.com");
        ItemDto itemDto = new ItemDto(1L,"Дрель+","Аккумуляторная дрель", false,
                null,null, null, null);
       BookingDto bookingDto = new BookingDto(1L,
               LocalDateTime.of(2023, 11, 12, 10, 25, 1),
               LocalDateTime.of(2024, 11, 12, 10, 25, 1),
               Status.WAITING,
               userDto,
               itemDto);

        JsonContent<BookingDto> result = json.write(bookingDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start")
                .isEqualTo((LocalDateTime.of(2023, 11, 12, 10, 25, 1)).toString());
        assertThat(result).extractingJsonPathStringValue("$.end")
                .isEqualTo(LocalDateTime.of(2024, 11, 12, 10, 25, 1).toString());
        assertThat(result).extractingJsonPathNumberValue("$.booker.id").isEqualTo(userDto.getId().intValue());
        assertThat(result).extractingJsonPathNumberValue("$.item.id").isEqualTo(itemDto.getId().intValue());

    }
}
