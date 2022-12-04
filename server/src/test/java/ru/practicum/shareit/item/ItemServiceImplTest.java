package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.List;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest
public class ItemServiceImplTest {
    private final EntityManager em;
    private final ItemService itemService;
    private final UserService userService;
    private final BookingService bookingService;

    private final ItemRequestService itemRequestService;

    @Test
    void createItem() {
        UserDto userDto = userService.createUser(makeUserDto("user", "user@user.com"));
        ItemDto itemDto = makeItemDto("Дрель", "Простая дрель", true);
        ItemDto itemDtoFromService = itemService.createItem(userDto.getId(), itemDto);

        TypedQuery<Item> query = em.createQuery("SELECT i FROM Item i WHERE i.id = :id", Item.class);
        Item item = query.setParameter("id", itemDtoFromService.getId()).getSingleResult();

        assertThat(item.getId(), equalTo(itemDtoFromService.getId()));
        assertThat(item.getName(), equalTo(itemDtoFromService.getName()));
        assertThat(item.getDescription(), equalTo(itemDtoFromService.getDescription()));
        assertThat(item.isAvailable(), equalTo(itemDtoFromService.getAvailable()));
    }

    @Test
    void createComment() throws InterruptedException {
        UserDto userDto1 = userService.createUser(makeUserDto("user", "user@user.com"));
        ItemDto itemDto = makeItemDto("Дрель", "Простая дрель", true);
        ItemDto itemDtoFromService = itemService.createItem(userDto1.getId(), itemDto);
        UserDto userDto2 = userService.createUser(makeUserDto("update", "update@user.com"));
        Booking booking = makeBooking(itemDtoFromService.getId(),
                LocalDateTime.now().plusSeconds(1), LocalDateTime.now().plusSeconds(2));
        bookingService.createBooking(userDto2.getId(), booking);
        Thread.sleep(2001);
        CommentDto commentDto = makeCommentDto("Add comment from user2");
        CommentDto commentDtoFromService = itemService.createComment(userDto2.getId(),
                itemDtoFromService.getId(), commentDto);

        TypedQuery<Comment> query = em.createQuery("SELECT c FROM Comment c WHERE c.id = :id", Comment.class);
        Comment comment = query.setParameter("id", commentDtoFromService.getId()).getSingleResult();

        assertThat(comment.getId(), equalTo(commentDtoFromService.getId()));
        assertThat(comment.getText(), equalTo(commentDtoFromService.getText()));
        assertThat(userService.findUserById(comment.getAuthorId()).getName(),
                equalTo(commentDtoFromService.getAuthorName()));
        assertThat(comment.getCreated(), notNullValue());
    }

    @Test
    void findItemById() {
        UserDto userDto = userService.createUser(makeUserDto("user", "user@user.com"));
        ItemDto itemDto = makeItemDto("Дрель", "Простая дрель", true);
        ItemDto itemDtoCreate = itemService.createItem(userDto.getId(), itemDto);
        ItemDto itemDtoFromService = itemService.findItemById(userDto.getId(), itemDtoCreate.getId());

        TypedQuery<Item> query = em.createQuery("SELECT i FROM Item i WHERE i.id = :id", Item.class);
        Item item = query.setParameter("id", itemDtoFromService.getId()).getSingleResult();

        assertThat(item.getId(), equalTo(itemDtoFromService.getId()));
        assertThat(item.getName(), equalTo(itemDtoFromService.getName()));
        assertThat(item.getDescription(), equalTo(itemDtoFromService.getDescription()));
        assertThat(item.isAvailable(), equalTo(itemDtoFromService.getAvailable()));
    }

    @Test
    void patchItem() {
        UserDto userDto = userService.createUser(makeUserDto("user", "user@user.com"));
        ItemDto itemDto = makeItemDto("Дрель", "Простая дрель", true);
        ItemDto itemDtoCreate = itemService.createItem(userDto.getId(), itemDto);
        ItemDto itemDtoUpdate = makeItemDto("Дрель+", "Аккумуляторная дрель", false);
        ItemDto itemDtoFromService = itemService.patchItem(userDto.getId(), itemDtoCreate.getId(), itemDtoUpdate);

        TypedQuery<Item> query = em.createQuery("SELECT i FROM Item i WHERE i.id = :id", Item.class);
        Item item = query.setParameter("id", itemDtoFromService.getId()).getSingleResult();

        assertThat(item.getId(), equalTo(itemDtoFromService.getId()));
        assertThat(item.getName(), equalTo(itemDtoFromService.getName()));
        assertThat(item.getDescription(), equalTo(itemDtoFromService.getDescription()));
        assertThat(item.isAvailable(), equalTo(itemDtoFromService.getAvailable()));
    }

    @Test
    void findAllItem() {
        UserDto userDto = userService.createUser(makeUserDto("user", "user@user.com"));
        List<ItemDto> sourceItems =
                List.of(makeItemDto("Дрель", "Простая дрель", true),
                        makeItemDto("Отвертка", "Аккумуляторная отвертка", true));

        for (ItemDto itemDto : sourceItems) {
            Item entity = ItemMapper.toItem(userDto.getId(), itemDto);
            em.persist(entity);
        }
        em.flush();

        List<ItemDto> targetItemRequests =
                itemService.findAllItem(userDto.getId(), 0, 10);

        for (ItemDto sourceItem : sourceItems) {
            assertThat(targetItemRequests, hasItem(allOf(
                    hasProperty("id", notNullValue()),
                    hasProperty("name", equalTo(sourceItem.getName())),
                    hasProperty("description", equalTo(sourceItem.getDescription())),
                    hasProperty("available", equalTo(sourceItem.getAvailable()))
            )));
        }
    }

    @Test
    void findAllByRequestId() {
        UserDto userDto = userService.createUser(makeUserDto("user", "user@user.com"));
        ItemRequestDto itemRequestDto = makeItemRequestDto("Хотел бы воспользоваться щёткой для обуви");
        ItemRequestDto itemRequestDtoFromService =
                itemRequestService.createItemRequest(itemRequestDto, userDto.getId());
        List<ItemDto> sourceItems =
                List.of(makeItemDto("Дрель", "Простая дрель", true),
                        makeItemDto("Отвертка", "Аккумуляторная отвертка", true));

        for (ItemDto itemDto : sourceItems) {
            itemDto.setRequestId(itemRequestDtoFromService.getId());
            Item entity = ItemMapper.toItem(userDto.getId(), itemDto);
            em.persist(entity);
        }
        em.flush();

        List<ItemDto> targetItemRequests =
                itemService.findAllByRequestId(itemRequestDtoFromService.getId());

        for (ItemDto sourceItem : sourceItems) {
            assertThat(targetItemRequests, hasItem(allOf(
                    hasProperty("id", notNullValue()),
                    hasProperty("name", equalTo(sourceItem.getName())),
                    hasProperty("description", equalTo(sourceItem.getDescription())),
                    hasProperty("available", equalTo(sourceItem.getAvailable()))
            )));
        }
    }

    @Test
    void searchItem() {
        UserDto userDto = userService.createUser(makeUserDto("user", "user@user.com"));
        List<ItemDto> sourceItems =
                List.of(makeItemDto("Дрель", "Простая дрель", true),
                        makeItemDto("Отвертка", "Аккумуляторная дрель", true));

        for (ItemDto itemDto : sourceItems) {
            Item entity = ItemMapper.toItem(userDto.getId(), itemDto);
            em.persist(entity);
        }
        em.flush();

        List<ItemDto> targetItemRequests =
                itemService.searchItem(userDto.getId(), "Дрель", 0, 10);

        for (ItemDto sourceItem : sourceItems) {
            assertThat(targetItemRequests, hasItem(allOf(
                    hasProperty("id", notNullValue()),
                    hasProperty("name", equalTo(sourceItem.getName())),
                    hasProperty("description", equalTo(sourceItem.getDescription())),
                    hasProperty("available", equalTo(sourceItem.getAvailable()))
            )));
        }
    }

    private ItemRequestDto makeItemRequestDto(String description) {
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setDescription(description);
        return itemRequestDto;
    }

    private ItemDto makeItemDto(String name, String description, boolean available) {
        ItemDto itemDto = new ItemDto();
        itemDto.setName(name);
        itemDto.setDescription(description);
        itemDto.setAvailable(available);
        return itemDto;
    }

    private UserDto makeUserDto(String name, String email) {
        UserDto userDto = new UserDto();
        userDto.setName(name);
        userDto.setEmail(email);
        return userDto;
    }

    private CommentDto makeCommentDto(String text) {
        CommentDto commentDto = new CommentDto();
        commentDto.setText(text);
        return commentDto;
    }

    private Booking makeBooking(long itemId, LocalDateTime start, LocalDateTime end) {
        Booking booking = new Booking();
        booking.setItemId(itemId);
        booking.setStart(start);
        booking.setEnd(end);
        return booking;
    }
}
