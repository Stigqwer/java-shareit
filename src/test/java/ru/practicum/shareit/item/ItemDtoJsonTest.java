package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class ItemDtoJsonTest {
    @Autowired
    private JacksonTester<ItemDto> json;

    @Test
    void testItemDto() throws IOException {
        Item item1 = new Item(1L, "Дрель", "Простая дрель", true, 1L, 1L);
        Booking booking1 = new Booking(1L, LocalDateTime.of(2023, 11, 12, 10, 25),
                LocalDateTime.of(2024, 11, 12, 10, 25),
                Status.WAITING, 2L, 1L);
        Booking booking2 = new Booking(2L, LocalDateTime.of(2017, 11, 12, 10, 25),
                LocalDateTime.of(2018, 11, 12, 10, 25),
                Status.WAITING, 2L, 1L);
        CommentDto commentDto = new CommentDto(null,"text",  null,
                LocalDateTime.of(2016, 11, 12, 10, 25));
        ItemDto itemDto = ItemMapper.toItemDto(item1);
        itemDto.setComments(List.of(commentDto));
        itemDto.setLastBooking(booking2);
        itemDto.setNextBooking(booking1);

        JsonContent<ItemDto> result = json.write(itemDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.description")
                .isEqualTo("Простая дрель");
        assertThat(result).extractingJsonPathNumberValue("$.lastBooking.id").isEqualTo(2);
        assertThat(result).extractingJsonPathNumberValue("$.nextBooking.id").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.requestId").isEqualTo(1);
    }
}
