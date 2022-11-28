package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */
@RestController
@RequestMapping(path = "/requests")
@AllArgsConstructor
public class ItemRequestController {


    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDto createItemRequest(@RequestHeader("X-Sharer-User-Id") @NotEmpty long userId,
                                            @RequestBody @Valid ItemRequestDto itemRequestDto) {
        return itemRequestService.createItemRequest(itemRequestDto, userId);
    }

    @GetMapping
    public List<ItemRequestDto> findAllItemRequestByOwner(@RequestHeader("X-Sharer-User-Id") @NotEmpty long userId,
                                                          @RequestParam(defaultValue = "0") Integer from,
                                                          @RequestParam(defaultValue = "10") Integer size) {
        return itemRequestService.findAllItemRequestByOwner(userId, from, size);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> findAllItemRequest(@RequestHeader("X-Sharer-User-Id") @NotEmpty long userId,
                                                   @RequestParam(defaultValue = "0") Integer from,
                                                   @RequestParam(defaultValue = "10") Integer size) {
        return itemRequestService.findAllItemRequest(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto findItemRequestById(@RequestHeader("X-Sharer-User-Id") @NotEmpty long userId,
                                              @PathVariable Long requestId) {
        return itemRequestService.findItemRequestById(userId, requestId);
    }
}
