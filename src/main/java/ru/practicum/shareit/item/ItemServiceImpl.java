package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final UserService userService;
    private final ItemRepository itemRepository;

    @Override
    public List<ItemDto> findAllItem(long userId) {
        userService.findUserById(userId);
        return itemRepository.findAllByOwnerId(userId).stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
    }

    @Override
    public ItemDto findItemById(long userId, long itemId) {
        userService.findUserById(userId);
        Optional<Item> item = itemRepository.findById(itemId);
        if(item.isPresent()){
            return ItemMapper.toItemDto(item.get());
        } else {
            throw new ItemNotFoundException(String.format("Вещи с id %d не существует", itemId));
        }
    }

    @Override
    public ItemDto createItem(long userId, ItemDto itemDto) {
        userService.findUserById(userId);
        return ItemMapper.toItemDto(itemRepository.save(ItemMapper.toItem(userId, itemDto)));
    }

    @Override
    public ItemDto patchItem(long userId, long itemId, ItemDto itemDto) {
        userService.findUserById(userId);
        Optional<Item> itemOptional = itemRepository.findById(itemId);
        if(itemOptional.isEmpty()){
            throw new ItemNotFoundException(String.format("Вещи с id %d не существует", itemId));
        }
        Item item = itemOptional.get();
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
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public List<ItemDto> searchItem(long userId, String text) {
        userService.findUserById(userId);
        if(text.isEmpty()){
            return Collections.emptyList();
        } else {
            return itemRepository.search(text).stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
        }
    }
}
