package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemStorage {
    List<Item> findAllItem(long userId);

    Item findItemById(long itemId);

    Item createItem(Item item);

    Item patchItem(Item item);

    List<Item> searchItem(String text);
}
