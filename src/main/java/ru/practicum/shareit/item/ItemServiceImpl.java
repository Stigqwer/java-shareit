package ru.practicum.shareit.item;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;

import java.util.List;

@Service
public class ItemServiceImpl implements ItemService {
    private final UserService userService;
    private final ItemStorage itemStorage;

    private static long id = 0;

    public ItemServiceImpl(UserService userService, ItemStorage itemStorage) {
        this.userService = userService;
        this.itemStorage = itemStorage;
    }

    @Override
    public List<Item> findAllItem(long userId) {
        userService.findUserById(userId);
        return itemStorage.findAllItem(userId);
    }

    @Override
    public Item findItemById(long userId, long itemId) {
        userService.findUserById(userId);
        return itemStorage.findItemById(itemId);
    }

    @Override
    public Item createItem(long userId, ItemDto itemDto) {
        userService.findUserById(userId);
        return itemStorage.createItem(ItemMapper.toItem(userId, ++id, itemDto));
    }

    @Override
    public Item patchItem(long userId, long itemId, ItemDto itemDto) {
        userService.findUserById(userId);
        Item item = itemStorage.findItemById(itemId);
        if (item.getOwnerId() != userId) {
            throw new ItemNotFoundException(String.format("У пользователя с id %d нет вещи с id %d", userId, itemId));
        }
        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
        return itemStorage.patchItem(item);
    }

    @Override
    public List<Item> searchItem(long userId, String text) {
        userService.findUserById(userId);
        return itemStorage.searchItem(text);
    }
}
