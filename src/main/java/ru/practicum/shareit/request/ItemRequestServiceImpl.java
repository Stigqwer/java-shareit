package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.UserService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService{
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
        return itemRequestRepository.findAllByRequestorIdOrderByCreatedDesc(ownerId).stream()
                .map(ItemRequestMapper::toItemRequestDto).collect(Collectors.toList());
    }
}
