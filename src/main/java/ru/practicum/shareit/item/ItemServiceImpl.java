package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.validation.PaginationValidation;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final ItemRepository itemRepository;

    private final UserRepository userRepository;

    @Override
    public List<ItemDto> findAllItem(long userId, Integer from, Integer size) {
        userService.findUserById(userId);
        PaginationValidation.doValidation(from, size);
        Pageable pageable = PageRequest.of(((from) / size), size);
        List<Item> items = itemRepository.findAllByOwnerId(userId, pageable);
        List<ItemDto> itemDtos = new ArrayList<>();
        Map<Long, List<Comment>> comments =
                commentRepository.findAll().stream().collect(Collectors.groupingBy(Comment::getItemId));
        Map<Long, List<Booking>> bookings = bookingRepository.findAllByStatusOrderByStartDesc(Status.APPROVED).stream()
                .collect(Collectors.groupingBy(Booking::getItemId));
        List<UserDto> userList =
                userRepository.findAll().stream().map(UserMapper::toUserDto).collect(Collectors.toList());
        Map<Long, UserDto> users = new HashMap<>();
        for (UserDto userDto : userList) {
            users.put(userDto.getId(), userDto);
        }
        for (Item item : items) {
            ItemDto itemDto = ItemMapper.toItemDto(item);
            if (bookings.containsKey(item.getId())) {
                addBooking(itemDto, bookings.get(item.getId()));
            }
            if (comments.containsKey(item.getId())) {
                List<CommentDto> commentDtoList = comments.get(item.getId()).stream()
                        .map(comment -> CommentMapper.toCommentDto(comment,
                                users.get(comment.getAuthorId()))).collect(Collectors.toList());
                itemDto.setComments(commentDtoList);
            }
            itemDtos.add(itemDto);
        }
        return itemDtos;
    }

    private void addBooking(ItemDto itemDto, List<Booking> bookings) {
        List<Booking> lastBookings = bookings.stream()
                .filter(booking -> booking.getEnd().isBefore(LocalDateTime.now()))
                .collect(Collectors.toList());
        if (lastBookings.size() != 0) {
            itemDto.setLastBooking(lastBookings.get(0));
        }
        List<Booking> nextBookings = bookings.stream()
                .filter(booking -> booking.getStart().isAfter(LocalDateTime.now()))
                .collect(Collectors.toList());
        if (nextBookings.size() != 0) {
            itemDto.setNextBooking(nextBookings.get(nextBookings.size() - 1));
        }
    }

    @Override
    public ItemDto findItemById(long userId, long itemId) {
        userService.findUserById(userId);
        Optional<Item> optionalItem = itemRepository.findById(itemId);
        if (optionalItem.isPresent()) {
            Item item = optionalItem.get();
            ItemDto itemDto = ItemMapper.toItemDto(item);
            if (item.getOwnerId().equals(userId)) {
                List<Booking> bookings =
                        bookingRepository.findAllByItemIdOrderByStartDesc(itemId);
                if (bookings.size() != 0) {
                    addBooking(itemDto, bookings);
                }
            }
            itemDto.setComments(commentRepository.findAllByItemId(itemId).stream()
                    .map(comment -> CommentMapper.toCommentDto(comment,
                            userService.findUserById(comment.getAuthorId()))).collect(Collectors.toList()));
            return itemDto;
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
        if (itemOptional.isEmpty()) {
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
    public List<ItemDto> searchItem(long userId, String text, Integer from, Integer size) {
        userService.findUserById(userId);
        if (text.isEmpty()) {
            return Collections.emptyList();
        } else {
            PaginationValidation.doValidation(from, size);
            Pageable pageable = PageRequest.of(((from) / size), size);
            return itemRepository.search(text, pageable).stream().map(ItemMapper::toItemDto)
                    .collect(Collectors.toList());
        }
    }

    @Override
    public CommentDto createComment(long userId, long itemId, CommentDto commentDto) {
        List<Booking> bookings = bookingRepository.findAllByItemId(itemId)
                .stream().filter(booking -> booking.getBookerId().equals(userId))
                .filter(booking -> booking.getEnd().isBefore(LocalDateTime.now()))
                .collect(Collectors.toList());
        if (bookings.isEmpty()) {
            throw new CommentException(String.format("Пользователь с id %d не бронировал вещ с id %d",
                    userId, itemId));
        }
        Comment comment = CommentMapper.toComment(commentDto, itemId, userId);
        return CommentMapper.toCommentDto(commentRepository.save(comment), userService.findUserById(userId));
    }

    public List<ItemDto> findAllByRequestId(Long requestId) {
        return itemRepository.findAllByRequestId(requestId).stream()
                .map(ItemMapper::toItemDto).collect(Collectors.toList());
    }
}
