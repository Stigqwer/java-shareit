package ru.practicum.shareit.item;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class ItemServiceTest {

    @Mock
    CommentRepository mockCommentRepository;

    @Mock
    BookingRepository mockBookingRepository;

    @Mock
    UserService mockUserService;

    @Mock
    ItemRepository mockItemRepository;

    @Mock
    UserRepository userRepository;

    ItemService itemService;

    @BeforeEach
    void beforeEach() {
        itemService = new ItemServiceImpl(mockCommentRepository,
                mockBookingRepository, mockUserService, mockItemRepository, userRepository);
        Mockito.when(mockUserService.findUserById(Mockito.anyLong()))
                .thenReturn(new UserDto(1L, "update", "update@user.com"));
    }

    @Test
    void testOkFindAllItem() {
        Item item1 = new Item(1L, "Дрель", "Простая дрель", true, 1L, null);
        Mockito.when(mockItemRepository.findAllByOwnerId(Mockito.anyLong(), Mockito.any(Pageable.class)))
                .thenReturn(List.of(item1));
        List<ItemDto> itemDtoList1 = List.of(ItemMapper.toItemDto(item1));

        List<ItemDto> itemDtoList = itemService.findAllItem(1L, 1, 1);

        Assertions.assertEquals(itemDtoList1, itemDtoList);
    }

    @Test
    void testOkFindAllItemWithBooking() {
        Item item1 = new Item(1L, "Дрель", "Простая дрель", true, 1L, null);
        Booking booking1 = new Booking(1L, LocalDateTime.of(2023, 11, 12, 10, 25),
                LocalDateTime.of(2024, 11, 12, 10, 25),
                Status.WAITING, 2L, 1L);
        Booking booking2 = new Booking(2L, LocalDateTime.of(2017, 11, 12, 10, 25),
                LocalDateTime.of(2018, 11, 12, 10, 25),
                Status.WAITING, 2L, 1L);
        Booking booking3 = new Booking(3L, LocalDateTime.of(2015, 11, 12, 10, 25),
                LocalDateTime.of(2016, 11, 12, 10, 25),
                Status.WAITING, 2L, 1L);
        Mockito.when(mockItemRepository.findAllByOwnerId(Mockito.anyLong(), Mockito.any(Pageable.class)))
                .thenReturn(List.of(item1));
        Mockito.when(mockBookingRepository
                        .findAllByStatusOrderByStartDesc(Mockito.any()))
                .thenReturn(List.of(booking1, booking2, booking3));
        List<ItemDto> itemDtoList1 = List.of(ItemMapper.toItemDto(item1));
        itemDtoList1.forEach(itemDto -> itemDto.setLastBooking(booking2));
        itemDtoList1.forEach(itemDto -> itemDto.setNextBooking(booking1));

        List<ItemDto> itemDtoList = itemService.findAllItem(1L, 1, 1);

        Assertions.assertEquals(itemDtoList1, itemDtoList);
    }

    @Test
    void testOkFindItemById() {
        Item item1 = new Item(1L, "Дрель", "Простая дрель", true, 1L, null);
        ItemDto itemDto = ItemMapper.toItemDto(item1);
        itemDto.setComments(Collections.emptyList());
        Mockito.when(mockItemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(item1));

        ItemDto itemDto1 = itemService.findItemById(1L, 1L);

        Assertions.assertEquals(itemDto, itemDto1);
    }

    @Test
    void testItemNotFound() {
        Mockito.when(mockItemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());

        ItemNotFoundException itemException = Assertions.assertThrows(ItemNotFoundException.class,
                () -> itemService.findItemById(1L, 1L));

        Assertions.assertEquals("Вещи с id 1 не существует", itemException.getMessage());
    }

    @Test
    void testOkFindItemByIdWithBooking() {
        Item item1 = new Item(1L, "Дрель", "Простая дрель", true, 1L, null);
        Booking booking1 = new Booking(1L, LocalDateTime.of(2023, 11, 12, 10, 25),
                LocalDateTime.of(2024, 11, 12, 10, 25),
                Status.WAITING, 2L, 1L);
        Booking booking2 = new Booking(2L, LocalDateTime.of(2017, 11, 12, 10, 25),
                LocalDateTime.of(2018, 11, 12, 10, 25),
                Status.WAITING, 2L, 1L);
        Booking booking3 = new Booking(3L, LocalDateTime.of(2015, 11, 12, 10, 25),
                LocalDateTime.of(2016, 11, 12, 10, 25),
                Status.WAITING, 2L, 1L);
        ItemDto itemDto = ItemMapper.toItemDto(item1);
        itemDto.setComments(Collections.emptyList());
        itemDto.setLastBooking(booking2);
        itemDto.setNextBooking(booking1);
        Mockito.when(mockItemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(item1));
        Mockito.when(mockBookingRepository
                        .findAllByItemIdOrderByStartDesc(Mockito.anyLong()))
                .thenReturn(List.of(booking1, booking2, booking3));

        ItemDto itemDto1 = itemService.findItemById(1L, 1L);

        Assertions.assertEquals(itemDto, itemDto1);
    }

    @Test
    void testOkCreateItem() {
        Item item1 = new Item(1L, "Дрель", "Простая дрель", true, 1L, null);
        Mockito.when(mockItemRepository.save(Mockito.any(Item.class)))
                .thenReturn(item1);

        ItemDto itemDto = itemService.createItem(1L, new ItemDto(null, "Дрель",
                "Простая дрель", true, null, null, null, null));

        Assertions.assertEquals(new ItemDto(1L, "Дрель", "Простая дрель", true,
                null, null, null, null), itemDto);
    }

    @Test
    void testOkPatchItem() {
        Item item1 = new Item(1L, "Дрель",
                "Простая дрель", true, 1L, null);
        Mockito.when(mockItemRepository.save(Mockito.any(Item.class)))
                .thenReturn(item1);
        Mockito.when(mockItemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(item1));

        ItemDto itemDto = itemService.patchItem(1L, 1L, new ItemDto(1L, "Дрель+",
                "Аккумуляторная дрель", false, null, null, null, null));

        Assertions.assertEquals(new ItemDto(1L, "Дрель+", "Аккумуляторная дрель", false,
                null, null, null, null), itemDto);
    }

    @Test
    void testItemNotFoundInPatchItem() {
        Mockito.when(mockItemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());

        ItemNotFoundException itemNotFoundException = Assertions.assertThrows(ItemNotFoundException.class,
                () -> itemService.patchItem(1L, 1L, new ItemDto(1L, "Дрель+",
                        "Аккумуляторная дрель", false, null,
                        null, null, null)));

        Assertions.assertEquals("Вещи с id 1 не существует", itemNotFoundException.getMessage());
    }

    @Test
    void testItemNotOwnerInPatchItem() {
        Item item1 = new Item(1L, "Дрель",
                "Простая дрель", true, 2L, null);
        Mockito.when(mockItemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(item1));

        ItemNotFoundException itemNotFoundException = Assertions.assertThrows(ItemNotFoundException.class,
                () -> itemService.patchItem(1L, 1L,
                        new ItemDto(1L, "Дрель+", "Аккумуляторная дрель", false, null,
                                null, null, null)));

        Assertions.assertEquals("У пользователя с id 1 нет вещи с id 1", itemNotFoundException.getMessage());
    }

    @Test
    void testOkSearch() {
        Item item1 = new Item(1L, "Дрель",
                "Простая дрель", true, 2L, null);
        Mockito.when(mockItemRepository.search(Mockito.anyString(), Mockito.any(Pageable.class)))
                .thenReturn(List.of(item1));

        List<ItemDto> itemDtoList = itemService.searchItem(1L, "ДРЕЛЬ", 1, 1);

        Assertions.assertEquals(List.of(ItemMapper.toItemDto(item1)), itemDtoList);
    }

    @Test
    void testOkSearchWithPageable() {
        Item item1 = new Item(1L, "Дрель",
                "Простая дрель", true, 2L, null);
        Mockito.when(mockItemRepository.search(Mockito.anyString(), Mockito.any(Pageable.class)))
                .thenReturn(List.of(item1));

        List<ItemDto> itemDtoList = itemService.searchItem(1L, "ДРЕЛЬ", 1, 1);

        Assertions.assertEquals(List.of(ItemMapper.toItemDto(item1)), itemDtoList);
    }

    @Test
    void testEmptyListWithEmptyText() {
        List<ItemDto> itemDtoList = itemService.searchItem(1L, "", 1, 1);

        Assertions.assertEquals(Collections.emptyList(), itemDtoList);
    }

    @Test
    void testOkCreateComment() {
        Booking booking1 = new Booking(3L, LocalDateTime.of(2015, 11, 12, 10, 25),
                LocalDateTime.of(2016, 11, 12, 10, 25),
                Status.WAITING, 1L, 1L);
        Comment comment = new Comment(1L, "text", 1L, 1L,
                LocalDateTime.of(2016, 11, 12, 10, 25));
        Mockito.when(mockBookingRepository.findAllByItemId(Mockito.anyLong()))
                .thenReturn(List.of(booking1));
        Mockito.when(mockCommentRepository.save(Mockito.any(Comment.class)))
                .thenReturn(comment);


        CommentDto commentDto = itemService.createComment(1L, 1L,
                new CommentDto(null, "text", null,
                        LocalDateTime.of(2016, 11, 12, 10, 25)));

        Assertions.assertEquals(CommentMapper.toCommentDto(comment, mockUserService.findUserById(1L)),
                commentDto);
    }

    @Test
    void testCommentException() {
        Booking booking1 = new Booking(3L, LocalDateTime.of(2015, 11, 12, 10, 25),
                LocalDateTime.of(2016, 11, 12, 10, 25),
                Status.WAITING, 2L, 1L);
        Mockito.when(mockBookingRepository.findAllByItemId(Mockito.anyLong()))
                .thenReturn(List.of(booking1));

        CommentException commentException = Assertions.assertThrows(CommentException.class,
                () -> itemService.createComment(1L, 1L,
                        new CommentDto(null, "text", null,
                                LocalDateTime.of(2016, 11, 12, 10, 25))));

        Assertions.assertEquals("Пользователь с id 1 не бронировал вещ с id 1", commentException.getMessage());
    }

    @Test
    void testOkFindAllByRequestId() {
        Item item1 = new Item(1L, "Дрель",
                "Простая дрель", true, 2L, 1L);
        Mockito.when(mockItemRepository.findAllByRequestId(Mockito.anyLong()))
                .thenReturn(List.of(item1));

        List<ItemDto> itemDtoList = itemService.findAllByRequestId(1L);

        Assertions.assertEquals(List.of(ItemMapper.toItemDto(item1)), itemDtoList);
    }
}
