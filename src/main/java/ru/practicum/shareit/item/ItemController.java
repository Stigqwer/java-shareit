package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

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
    public List<ItemDto> findAllItem(@RequestHeader("X-Sharer-User-Id") @NotEmpty long userId,
                                     @RequestParam(defaultValue = "0") Integer from,
                                     @RequestParam(defaultValue = "10") Integer size) {
        return itemService.findAllItem(userId, from, size);
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
                                    @RequestParam String text,
                                    @RequestParam(defaultValue = "0") Integer from,
                                    @RequestParam(defaultValue = "10") Integer size) {
        return itemService.searchItem(userId, text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@RequestHeader("X-Sharer-User-Id") @NotEmpty long userId,
                                    @PathVariable long itemId, @RequestBody @Valid CommentDto commentDto) {
        return itemService.createComment(userId, itemId, commentDto);
    }
}
