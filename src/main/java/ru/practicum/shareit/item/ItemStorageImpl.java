package ru.practicum.shareit.item;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ItemStorageImpl implements ItemStorage {
    private final HashMap<Long, Item> items = new HashMap<>();

    @Override
    public List<Item> findAllItem(long userId) {
        return items.values().stream().filter(x -> x.getOwnerId() == userId).collect(Collectors.toList());
    }

    @Override
    public Item findItemById(long itemId) {
        if (items.containsKey(itemId)) {
            return items.get(itemId);
        } else {
            throw new ItemNotFoundException(String.format("Вещи с id %d не существует", itemId));
        }
    }

    @Override
    public Item createItem(Item item) {
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item patchItem(Item item) {
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public List<Item> searchItem(String text) {
        return items.values().stream()
                .filter(x -> x.isAvailable() &&
                        (x.getName().toLowerCase().contains(text.toLowerCase()) ||
                                x.getDescription().toLowerCase().contains(text.toLowerCase())) && !text.isEmpty())
                .collect(Collectors.toList());
    }
}
