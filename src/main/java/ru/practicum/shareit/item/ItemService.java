package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {

    List<ItemDto> findAllItem(long userId);

    ItemDto findItemById(long userId, long itemId);

    ItemDto createItem(long userId, ItemDto itemDto);

    ItemDto patchItem(long userId, long itemId, ItemDto itemDto);

    List<ItemDto> searchItem(long userId, String text);
}
