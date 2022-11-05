package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
@AllArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @GetMapping
    public List<ItemDto> findAllItem(@RequestHeader("X-Sharer-User-Id") @NotEmpty long userId) {
        return itemService.findAllItem(userId);
    }

    @GetMapping("/{itemId}")
    public ItemDto findItemById(@RequestHeader("X-Sharer-User-Id") @NotEmpty long userId,
                             @PathVariable long itemId) {
        return itemService.findItemById(userId, itemId);
    }

    @PostMapping
    public ItemDto createItem(@RequestHeader("X-Sharer-User-Id") @NotEmpty long userId,
                           @RequestBody @Valid ItemDto itemDto) {
        return itemService.createItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto patchItem(@RequestHeader("X-Sharer-User-Id") @NotEmpty long userId,
                          @PathVariable long itemId,
                          @RequestBody ItemDto itemDto) {
        return itemService.patchItem(userId, itemId, itemDto);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItem(@RequestHeader("X-Sharer-User-Id") @NotEmpty long userId,
                                 @RequestParam String text) {
        return itemService.searchItem(userId, text);
    }
}
