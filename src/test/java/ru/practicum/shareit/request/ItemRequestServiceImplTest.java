package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest
public class ItemRequestServiceImplTest {
    private final EntityManager em;
    private final UserService userService;
    private final ItemRequestService itemRequestService;

    @Test
    void createItemRequest(){
        UserDto userDto = userService.createUser(makeUserDto("user", "user@user.com"));
        ItemRequestDto itemRequestDto = makeItemRequestDto("Хотел бы воспользоваться щёткой для обуви");
        ItemRequestDto itemRequestDtoFromService =
                itemRequestService.createItemRequest(itemRequestDto, userDto.getId());

        TypedQuery<ItemRequest> query =
                em.createQuery("SELECT i FROM ItemRequest i WHERE i.id = :id", ItemRequest.class);
        ItemRequest itemRequest = query.setParameter("id", itemRequestDtoFromService.getId()).getSingleResult();

        assertThat(itemRequest.getId(), equalTo(itemRequestDtoFromService.getId()));
        assertThat(itemRequest.getDescription(), equalTo(itemRequestDtoFromService.getDescription()));
        assertThat(itemRequest.getCreated(), equalTo(itemRequestDtoFromService.getCreated()));
    }

    @Test
    void findItemRequestById(){
        UserDto userDto = userService.createUser(makeUserDto("user", "user@user.com"));
        ItemRequestDto itemRequestDto = makeItemRequestDto("Хотел бы воспользоваться щёткой для обуви");
        ItemRequestDto itemRequestDtoFromCreate =
                itemRequestService.createItemRequest(itemRequestDto, userDto.getId());
        ItemRequestDto itemRequestDtoFromService =
                itemRequestService.findItemRequestById(userDto.getId(), itemRequestDtoFromCreate.getId());

        TypedQuery<ItemRequest> query =
                em.createQuery("SELECT i FROM ItemRequest i WHERE i.id = :id", ItemRequest.class);
        ItemRequest itemRequest = query.setParameter("id", itemRequestDtoFromService.getId()).getSingleResult();

        assertThat(itemRequest.getId(), equalTo(itemRequestDtoFromService.getId()));
        assertThat(itemRequest.getDescription(), equalTo(itemRequestDtoFromService.getDescription()));
        assertThat(itemRequest.getCreated(), equalTo(itemRequestDtoFromService.getCreated()));
    }

    @Test
    void findAllItemRequest(){
        UserDto userDto = userService.createUser(makeUserDto("user", "user@user.com"));
        UserDto userDto2 = userService.createUser(makeUserDto("update", "update@user.com"));
        List<ItemRequestDto> sourceItemRequests =
                List.of(makeItemRequestDto("Хотел бы воспользоваться щёткой для обуви"),
                        makeItemRequestDto("Хотел бы воспользоваться дрелью"));

        for(ItemRequestDto itemRequestDto: sourceItemRequests){
            ItemRequest entity = ItemRequestMapper.toItemRequest(itemRequestDto, userDto.getId());
            em.persist(entity);
        }
        em.flush();

        List<ItemRequestDto> targetItemRequests =
                itemRequestService.findAllItemRequest(userDto2.getId(),null, null);

        for(ItemRequestDto sourceItemRequest: sourceItemRequests){
            assertThat(targetItemRequests, hasItem( allOf(
                    hasProperty("id", notNullValue()),
                    hasProperty("description", equalTo(sourceItemRequest.getDescription()))
                    )));
        }
    }

    @Test
    void findAllItemRequestByOwner(){
        UserDto userDto = userService.createUser(makeUserDto("user", "user@user.com"));
        List<ItemRequestDto> sourceItemRequests =
                List.of(makeItemRequestDto("Хотел бы воспользоваться щёткой для обуви"),
                        makeItemRequestDto("Хотел бы воспользоваться дрелью"));

        for(ItemRequestDto itemRequestDto: sourceItemRequests){
            ItemRequest entity = ItemRequestMapper.toItemRequest(itemRequestDto, userDto.getId());
            em.persist(entity);
        }
        em.flush();

        List<ItemRequestDto> targetItemRequests =
                itemRequestService.findAllItemRequestByOwner(userDto.getId());

        for(ItemRequestDto sourceItemRequest: sourceItemRequests){
            assertThat(targetItemRequests, hasItem( allOf(
                    hasProperty("id", notNullValue()),
                    hasProperty("description", equalTo(sourceItemRequest.getDescription()))
            )));
        }
    }

    private UserDto makeUserDto(String name, String email){
        UserDto userDto = new UserDto();
        userDto.setName(name);
        userDto.setEmail(email);
        return userDto;
    }

    private ItemRequestDto makeItemRequestDto(String description){
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setDescription(description);
        return itemRequestDto;
    }
}
