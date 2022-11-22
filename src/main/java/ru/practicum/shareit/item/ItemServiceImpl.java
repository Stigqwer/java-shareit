package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final ItemRepository itemRepository;

    @Override
    public List<ItemDto> findAllItem(long userId, Integer from, Integer size) {
        userService.findUserById(userId);
        List<Item> items;
        if(from == null || size == null){
            items = itemRepository.findAllByOwnerId(userId);
        } else {
          if(size <= 0) {
              throw new ItemException(String.format("Размер страницы %s", size));
          }  else if (from < 0) {
              throw new ItemException("Индекс первого эллемента меньше нуля");
          } else {
              Pageable pageable = PageRequest.of(((from) / size), size);
              items = itemRepository.findAllByOwnerId(userId, pageable);
          }
        }
        List<ItemDto> itemDtos = new ArrayList<>();
        for (Item item : items) {
            ItemDto itemDto = ItemMapper.toItemDto(item);
            List<Booking> bookings = bookingRepository.findAllByItemIdOrderByStartDesc(item.getId());
            if (bookings.size() != 0) {
                addBooking(itemDto, bookings);
            }
            itemDto.setComments(commentRepository.findAllByItemId(item.getId()).stream()
                    .map(comment -> CommentMapper.toCommentDto(comment,
                            userService.findUserById(comment.getAuthorId()))).collect(Collectors.toList()));
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
                List<Booking> bookings = bookingRepository.findAllByItemIdOrderByStartDesc(itemId);
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
            if(from == null || size == null) {
                return itemRepository.search(text).stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
            } else{
                if(size <= 0) {
                    throw new ItemException(String.format("Размер страницы %s", size));
                }  else if (from < 0) {
                    throw new ItemException("Индекс первого эллемента меньше нуля");
                } else {
                    Pageable pageable = PageRequest.of(((from) / size), size);
                     return itemRepository.search(text, pageable).stream().map(ItemMapper::toItemDto)
                             .collect(Collectors.toList());
                }
            }
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

    public List<ItemDto> findAllByRequestId(Long requestId){
        return itemRepository.findAllByRequestId(requestId).stream()
                .map(ItemMapper::toItemDto).collect(Collectors.toList());
    }
}
