package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.UserService;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final UserService userService;

    private final ItemService itemService;
    private final ItemRequestRepository itemRequestRepository;

    @Override
    public ItemRequestDto createItemRequest(ItemRequestDto itemRequestDto, Long requestorId) {
        userService.findUserById(requestorId);
        ItemRequest itemRequest = itemRequestRepository
                .save(ItemRequestMapper.toItemRequest(itemRequestDto, requestorId));
        return ItemRequestMapper.toItemRequestDto(itemRequest);
    }

    @Override
    public List<ItemRequestDto> findAllItemRequestByOwner(Long ownerId) {
        userService.findUserById(ownerId);
        List<ItemRequestDto> itemRequestDtos = itemRequestRepository
                .findAllByRequestorIdOrderByCreatedDesc(ownerId).stream()
                .map(ItemRequestMapper::toItemRequestDto).collect(Collectors.toList());
        itemRequestDtos.forEach(itemRequestDto
                -> itemRequestDto.setItems(itemService.findAllByRequestId(itemRequestDto.getId())));
        return itemRequestDtos;
    }

    @Override
    public List<ItemRequestDto> findAllItemRequest(Long userId, Integer from, Integer size) {
        userService.findUserById(userId);
        if (from == null || size == null) {
            return itemRequestRepository.findAll(Sort.by("created").descending()).stream()
                    .map(ItemRequestMapper::toItemRequestDto).collect(Collectors.toList());
        } else {
            if (size <= 0) {
                throw new ItemRequestException(String.format("Размер страницы %s", size));
            } else if (from < 0) {
                throw new ItemRequestException("Индекс первого эллемента меньше нуля");
            } else {
                Pageable pageable = PageRequest.of(((from) / size), size,
                        Sort.by("created").descending());
                List<ItemRequestDto> itemRequestDtos = itemRequestRepository.findAll(pageable).stream()
                        .filter(itemRequest -> !Objects.equals(itemRequest.getRequestorId(), userId))
                        .map(ItemRequestMapper::toItemRequestDto).collect(Collectors.toList());
                itemRequestDtos.forEach(itemRequestDto
                        -> itemRequestDto.setItems(itemService.findAllByRequestId(itemRequestDto.getId())));
                return itemRequestDtos;
            }
        }
    }

    @Override
    public ItemRequestDto findItemRequestById(Long userId, Long requestId) {
        userService.findUserById(userId);
        Optional<ItemRequest> itemRequestOptional = itemRequestRepository.findById(requestId);
        if(itemRequestOptional.isEmpty()){
            throw new ItemRequestNotFoundException(String.format("Запроса с %d не существует", requestId));
        } else {
            ItemRequestDto itemRequestDto = ItemRequestMapper.toItemRequestDto(itemRequestOptional.get());
            itemRequestDto.setItems(itemService.findAllByRequestId(itemRequestDto.getId()));
            return itemRequestDto;
        }
    }
}
