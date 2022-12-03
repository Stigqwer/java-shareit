package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.ItemClient;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemRequestController {
    private ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> createItemRequest(@RequestHeader("X-Sharer-User-Id") long userId,
                                                    @RequestBody @Valid ItemRequestDto itemRequestDto) {
        log.info("Creating itemRequest {}, userId={}", itemRequestDto, userId);
        return itemRequestClient.createItemRequest(userId, itemRequestDto);
    }

    @GetMapping
    public ResponseEntity<Object> findAllItemRequestByOwner(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
            @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Get owner itemRequest with userId={}, from={}, size={}", userId, from, size);
        return itemRequestClient.findAllItemRequestByOwner(userId, from, size);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> findAllItemRequest(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
            @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Get all itemRequest with userId={}, from={}, size={}", userId, from, size);
        return itemRequestClient.findAllItemRequest(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> findItemRequestById(@RequestHeader("X-Sharer-User-Id") long userId,
                                                      @PathVariable Long requestId) {
        log.info("Get itemRequest {}, userId={}", requestId, userId);
        return itemRequestClient.findItemRequestById(userId, requestId);
    }
}
