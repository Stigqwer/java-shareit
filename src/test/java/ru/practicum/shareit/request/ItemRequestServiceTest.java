package ru.practicum.shareit.request;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class ItemRequestServiceTest {

    @Mock
    UserService mockUserService;

    @Mock
    ItemService mockItemService;

    @Mock
    ItemRequestRepository mockItemRequestRepository;

    ItemRequestService itemRequestService;

    @BeforeEach
    void beforeEach() {
        itemRequestService = new ItemRequestServiceImpl(mockUserService, mockItemService, mockItemRequestRepository);
        Mockito.when(mockUserService.findUserById(Mockito.anyLong()))
                .thenReturn(new UserDto(1L, "update", "update@user.com"));
    }

    @Test
    void testOkCreateItemRequest(){
        ItemRequest itemRequest = new ItemRequest(1L, "Хотел бы воспользоваться щёткой для обуви",
                1L, LocalDateTime.of(2024, 11, 12, 10, 25));
        Mockito.when(mockItemRequestRepository.save(Mockito.any(ItemRequest.class)))
                .thenReturn(itemRequest);
        ItemRequestDto itemRequestDto = ItemRequestMapper.toItemRequestDto(itemRequest);

        ItemRequestDto itemRequestDto1 = itemRequestService.createItemRequest(new ItemRequestDto(null,
                "Хотел бы воспользоваться щёткой для обуви", null,  null), 1L);

        Assertions.assertEquals(itemRequestDto, itemRequestDto1);
    }

    @Test
    void testOkFindAllItemRequestByOwner(){
        ItemRequest itemRequest = new ItemRequest(1L, "Хотел бы воспользоваться щёткой для обуви",
                1L, LocalDateTime.of(2024, 11, 12, 10, 25));
        Mockito.when(mockItemRequestRepository.findAllByRequestorIdOrderByCreatedDesc(Mockito.anyLong()))
                .thenReturn(List.of(itemRequest));
        ItemRequestDto itemRequestDto = ItemRequestMapper.toItemRequestDto(itemRequest);
        itemRequestDto.setItems(Collections.emptyList());

        List<ItemRequestDto> itemRequestDto1 = itemRequestService.findAllItemRequestByOwner(1L);

        Assertions.assertEquals(List.of(itemRequestDto), itemRequestDto1);
    }

    @Test
    void testOkFindAllItemRequest(){
        ItemRequest itemRequest = new ItemRequest(1L, "Хотел бы воспользоваться щёткой для обуви",
                2L, LocalDateTime.of(2024, 11, 12, 10, 25));
        Mockito.when(mockItemRequestRepository.findAll(Mockito.any(Sort.class)))
                .thenReturn(List.of(itemRequest));
        ItemRequestDto itemRequestDto = ItemRequestMapper.toItemRequestDto(itemRequest);
        itemRequestDto.setItems(Collections.emptyList());

        List<ItemRequestDto> itemRequestDtos = itemRequestService.findAllItemRequest(1L, null, null);

        Assertions.assertEquals(List.of(itemRequestDto), itemRequestDtos);
    }

    @Test
    void testOkFindAllItemRequestWithPageable(){
        ItemRequest itemRequest = new ItemRequest(1L, "Хотел бы воспользоваться щёткой для обуви",
                2L, LocalDateTime.of(2024, 11, 12, 10, 25));
        Mockito.when(mockItemRequestRepository.findAll(Mockito.any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(itemRequest)));
        ItemRequestDto itemRequestDto = ItemRequestMapper.toItemRequestDto(itemRequest);
        itemRequestDto.setItems(Collections.emptyList());

        List<ItemRequestDto> itemRequestDtos = itemRequestService.findAllItemRequest(1L, 1, 1);

        Assertions.assertEquals(List.of(itemRequestDto), itemRequestDtos);
    }

    @Test
    void testSizeErrorFindAllItemRequest(){
        ItemRequestException itemRequestException = Assertions.assertThrows(ItemRequestException.class,
                () -> itemRequestService.findAllItemRequest(1L, 1, 0));

        Assertions.assertEquals("Размер страницы 0", itemRequestException.getMessage());
    }

    @Test
    void testIndexErrorFindAllItemRequest(){
        ItemRequestException itemRequestException = Assertions.assertThrows(ItemRequestException.class,
                () -> itemRequestService.findAllItemRequest(1L, -1, 1));

        Assertions.assertEquals("Индекс первого эллемента меньше нуля", itemRequestException.getMessage());
    }

    @Test
    void testOkFindItemRequestById(){
        ItemRequest itemRequest = new ItemRequest(1L, "Хотел бы воспользоваться щёткой для обуви",
                1L, LocalDateTime.of(2024, 11, 12, 10, 25));
        Mockito.when(mockItemRequestRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(itemRequest));
        ItemRequestDto itemRequestDto = ItemRequestMapper.toItemRequestDto(itemRequest);
        itemRequestDto.setItems(Collections.emptyList());

        ItemRequestDto itemRequestDto1 = itemRequestService.findItemRequestById(1L,1L);

        Assertions.assertEquals(itemRequestDto, itemRequestDto1);
    }

    @Test
    void testItemRequestNotFound(){
        Mockito.when(mockItemRequestRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());

        ItemRequestNotFoundException itemRequestNotFoundException =
                Assertions.assertThrows(ItemRequestNotFoundException.class,
                        () -> itemRequestService.findItemRequestById(1L, 1L));

        Assertions.assertEquals("Запроса с id 1 не существует", itemRequestNotFoundException.getMessage());
    }
}
