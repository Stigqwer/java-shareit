package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {

    List<Item> findAllItem(long userId);

    Item findItemById(long userId, long itemId);

    Item createItem(long userId, ItemDto itemDto);

    Item patchItem(long userId, long itemId, ItemDto itemDto);

    List<Item> searchItem(long userId, String text);
}
