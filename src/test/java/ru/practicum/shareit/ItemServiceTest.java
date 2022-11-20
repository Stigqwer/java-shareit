package ru.practicum.shareit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.item.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class ItemServiceTest {

    @Mock
    CommentRepository mockCommentRepository;

    @Mock
    BookingRepository mockBookingRepository;

    @Mock
    UserService mockUserService;

    @Mock
    ItemRepository mockItemRepository;

    ItemService itemService;

    @BeforeEach
    void beforeEach() {
        itemService = new ItemServiceImpl(mockCommentRepository
                , mockBookingRepository, mockUserService, mockItemRepository);
        Mockito.when(mockUserService.findUserById(Mockito.anyLong()))
                .thenReturn(new UserDto(1L, "update", "update@user.com"));
    }

    @Test
    void testOkFindAllItemWithoutPageable() {
        Item item1 = new Item(1L, "Дрель", "Простая дрель", true, 1L, null);
        Item item2 = new Item(2L, "Отвертка", "Аккумуляторная отвертка",
                true, 1L, null);
        Item item3 = new Item(3L, "Клей момент",
                "Тюбик суперклея марки Момент", true, 1L, null);
        Mockito.when(mockItemRepository.findAllByOwnerId(Mockito.anyLong()))
                .thenReturn(List.of(item1, item2, item3));
        List<ItemDto> itemDtoList1 = List.of(ItemMapper.toItemDto(item1),
                ItemMapper.toItemDto(item2), ItemMapper.toItemDto(item3));
        itemDtoList1.forEach(itemDto -> itemDto.setComments(Collections.emptyList()));

        List<ItemDto> itemDtoList = itemService.findAllItem(1L, null, null);

        Assertions.assertEquals(itemDtoList1, itemDtoList);
    }

    @Test
    void testOkFindAllItemWithPageable() {
        Item item1 = new Item(1L, "Дрель", "Простая дрель", true, 1L, null);
        Mockito.when(mockItemRepository.findAllByOwnerId(Mockito.anyLong(), Mockito.any(Pageable.class)))
                .thenReturn(List.of(item1));
        List<ItemDto> itemDtoList1 = List.of(ItemMapper.toItemDto(item1));
        itemDtoList1.forEach(itemDto -> itemDto.setComments(Collections.emptyList()));

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
        Mockito.when(mockBookingRepository.findAllByItemIdOrderByStartDesc(Mockito.anyLong()))
                .thenReturn(List.of(booking3, booking2, booking1));
        List<ItemDto> itemDtoList1 = List.of(ItemMapper.toItemDto(item1));
        itemDtoList1.forEach(itemDto -> itemDto.setComments(Collections.emptyList()));
        itemDtoList1.forEach(itemDto -> itemDto.setLastBooking(booking2));
        itemDtoList1.forEach(itemDto -> itemDto.setNextBooking(booking1));

        List<ItemDto> itemDtoList = itemService.findAllItem(1L, 1, 1);

        Assertions.assertEquals(itemDtoList1, itemDtoList);
    }

    @Test
    void testSizeErrorFindAllItem() {
        ItemException itemException = Assertions.assertThrows(ItemException.class,
                () -> itemService.findAllItem(1L, 1, -1));

        Assertions.assertEquals("Размер страницы -1", itemException.getMessage());
    }

    @Test
    void testIndexErrorFindAllItem() {
        ItemException itemException = Assertions.assertThrows(ItemException.class,
                () -> itemService.findAllItem(1L, -1, 1));

        Assertions.assertEquals("Индекс первого эллемента меньше нуля", itemException.getMessage());
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
        Mockito.when(mockBookingRepository.findAllByItemIdOrderByStartDesc(Mockito.anyLong()))
                .thenReturn(List.of(booking3, booking2, booking1));

        ItemDto itemDto1 = itemService.findItemById(1L, 1L);

        Assertions.assertEquals(itemDto, itemDto1);
    }

    @Test
    void testOkCreateItem(){
        Item item1 = new Item(1L, "Дрель", "Простая дрель", true, 1L, null);
        Mockito.when(mockItemRepository.save(Mockito.any(Item.class)))
                .thenReturn(item1);

        ItemDto itemDto = itemService.createItem(1L, new ItemDto(null,"Дрель",
                "Простая дрель", true, null, null, null, null));

        Assertions.assertEquals(new ItemDto(1L, "Дрель", "Простая дрель", true,
                null, null, null, null), itemDto);
    }

    @Test
    void testOkPatchItem(){
        Item item1 = new Item(1L, "Дрель",
                "Простая дрель", true, 1L, null);
        Mockito.when(mockItemRepository.save(Mockito.any(Item.class)))
                .thenReturn(item1);
        Mockito.when(mockItemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(item1));

        ItemDto itemDto = itemService.patchItem(1L, 1L,new ItemDto(1L,"Дрель+",
                "Аккумуляторная дрель", false, null, null, null, null));

        Assertions.assertEquals(new ItemDto(1L, "Дрель+", "Аккумуляторная дрель", false,
                null, null, null, null), itemDto);
    }

    @Test
    void testItemNotFoundInPatchItem(){
        Mockito.when(mockItemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());

        ItemNotFoundException itemNotFoundException = Assertions.assertThrows(ItemNotFoundException.class,
                () -> itemService.patchItem(1L, 1L, new ItemDto(1L,"Дрель+",
                        "Аккумуляторная дрель", false, null,
                        null, null, null)));

        Assertions.assertEquals("Вещи с id 1 не существует", itemNotFoundException.getMessage());
    }

    @Test
    void testItemNotOwnerInPatchItem(){
        Item item1 = new Item(1L, "Дрель",
                "Простая дрель", true, 2L, null);
        Mockito.when(mockItemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(item1));

        ItemNotFoundException itemNotFoundException = Assertions.assertThrows(ItemNotFoundException.class,
                () -> itemService.patchItem(1L,1L,
                new ItemDto(1L,"Дрель+","Аккумуляторная дрель", false, null,
                null, null, null)));

        Assertions.assertEquals("У пользователя с id 1 нет вещи с id 1", itemNotFoundException.getMessage());
    }

    @Test
    void testOkSearchItem(){}
}
