package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

public class ItemMapper {
    public static ItemDto toItemDto(Item item) {
        return new ItemDto(
                item.getName(),
                item.getDescription(),
                item.isAvailable()
        );
    }

    public static Item toItem(long userId, long itemId, ItemDto itemDto) {
        return new Item(itemId, itemDto.getName(), itemDto.getDescription(), itemDto.getAvailable(), userId);
    }
}
