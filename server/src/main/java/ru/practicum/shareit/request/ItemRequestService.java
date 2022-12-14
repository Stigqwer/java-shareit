package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto createItemRequest(ItemRequestDto itemRequestDto, Long requestorId);

    List<ItemRequestDto> findAllItemRequestByOwner(Long ownerId, Integer from, Integer size);

    List<ItemRequestDto> findAllItemRequest(Long userId, Integer from, Integer size);

    ItemRequestDto findItemRequestById(Long userId, Long requestId);
}
