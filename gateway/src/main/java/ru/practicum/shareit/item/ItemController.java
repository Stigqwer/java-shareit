package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {

    private final ItemClient itemClient;

    @GetMapping
    public ResponseEntity<Object> getItems(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
            @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Get items with userId={}, from={}, size={}", userId, from, size);
        return itemClient.getItems(userId, from, size);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> findItemById(@RequestHeader("X-Sharer-User-Id") long userId,
                                               @PathVariable long itemId) {
        log.info("Get item {}, userId={}", itemId, userId);
        return itemClient.getItem(userId, itemId);
    }

    @PostMapping
    public ResponseEntity<Object> createItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                             @RequestBody @Valid ItemDto itemDto) {
        log.info("Creating item {}, userId={}", itemDto, userId);
        return itemClient.createItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> patchItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                            @PathVariable long itemId,
                                            @RequestBody ItemDto itemDto) {
        log.info("Patch item {}, userId={}, itemId={}", itemDto, userId, itemId);
        return itemClient.patchItem(userId, itemId, itemDto);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItem(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestParam String text,
            @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
            @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Search items with userId={}, text={}, from={}, size={}", userId, text, from, size);
        return itemClient.searchItem(userId, text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@RequestHeader("X-Sharer-User-Id") long userId,
                                                @PathVariable long itemId, @RequestBody @Valid CommentDto commentDto) {
        log.info("Creating comment {}, userId={}, itemId={}", commentDto, userId, itemId);
        return itemClient.createComment(userId, itemId, commentDto);
    }
}
