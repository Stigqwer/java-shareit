package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.validation.PaginationValidation;

import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final UserService userService;

    private final ItemService itemService;
    private final ItemRequestRepository itemRequestRepository;
    private final ItemRepository itemRepository;

    @Override
    public ItemRequestDto createItemRequest(ItemRequestDto itemRequestDto, Long requestorId) {
        userService.findUserById(requestorId);
        ItemRequest itemRequest = itemRequestRepository
                .save(ItemRequestMapper.toItemRequest(itemRequestDto, requestorId));
        return ItemRequestMapper.toItemRequestDto(itemRequest);
    }

    @Override
    public List<ItemRequestDto> findAllItemRequestByOwner(Long ownerId, Integer from, Integer size) {
        PaginationValidation.doValidation(from, size);
        userService.findUserById(ownerId);
        Map<Long, List<ItemDto>> items = itemRepository.findAll().stream().map(ItemMapper::toItemDto)
                .collect(Collectors.groupingBy(itemDto -> itemDto.getRequestId() == null ? 0 : itemDto.getRequestId()));
        List<ItemRequestDto> itemRequestDtos = itemRequestRepository
                .findAllByRequestorIdOrderByCreatedDesc(ownerId, PageRequest.of(((from) / size), size)).stream()
                .map(ItemRequestMapper::toItemRequestDto).collect(Collectors.toList());
        itemRequestDtos.forEach(itemRequestDto -> itemRequestDto
                .setItems(items.getOrDefault(itemRequestDto.getId(), Collections.emptyList())));
        return itemRequestDtos;
    }

    @Override
    public List<ItemRequestDto> findAllItemRequest(Long userId, Integer from, Integer size) {
        userService.findUserById(userId);
        PaginationValidation.doValidation(from, size);
        Map<Long, List<ItemDto>> items = itemRepository.findAll().stream().map(ItemMapper::toItemDto)
                .collect(Collectors.groupingBy(itemDto -> itemDto.getRequestId() == null ? 0 : itemDto.getRequestId()));
        Pageable pageable = PageRequest.of(((from) / size), size,
                Sort.by("created").descending());
        List<ItemRequestDto> itemRequestDtos = itemRequestRepository.findAll(pageable).stream()
                .filter(itemRequest -> !Objects.equals(itemRequest.getRequestorId(), userId))
                .map(ItemRequestMapper::toItemRequestDto).collect(Collectors.toList());
        itemRequestDtos.forEach(itemRequestDto -> itemRequestDto
                .setItems(items.getOrDefault(itemRequestDto.getId(), Collections.emptyList())));
        return itemRequestDtos;
    }

    @Override
    public ItemRequestDto findItemRequestById(Long userId, Long requestId) {
        userService.findUserById(userId);
        Optional<ItemRequest> itemRequestOptional = itemRequestRepository.findById(requestId);
        if (itemRequestOptional.isEmpty()) {
            throw new ItemRequestNotFoundException(String.format("Запроса с id %d не существует", requestId));
        } else {
            ItemRequestDto itemRequestDto = ItemRequestMapper.toItemRequestDto(itemRequestOptional.get());
            itemRequestDto.setItems(itemService.findAllByRequestId(itemRequestDto.getId()));
            return itemRequestDto;
        }
    }
}
