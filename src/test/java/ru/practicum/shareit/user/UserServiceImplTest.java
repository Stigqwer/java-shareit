package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.UserDto;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest
public class UserServiceImplTest {
    private final EntityManager em;
    private final UserService service;

    @Test
    void createUser() {
        UserDto userDto = makeUserDto("user", "user@user.com");
        UserDto userDtoFromService = service.createUser(userDto);

        TypedQuery<User> query = em.createQuery("SELECT u FROM User u WHERE u.id = :id", User.class);
        User user = query.setParameter("id", userDtoFromService.getId()).getSingleResult();

        assertThat(user.getId(), equalTo(userDtoFromService.getId()));
        assertThat(user.getName(), equalTo(userDtoFromService.getName()));
        assertThat(user.getEmail(), equalTo(userDtoFromService.getEmail()));
    }

    @Test
    void findUserById() {
        UserDto userDto = makeUserDto("user", "user@user.com");
        UserDto userDtoCreate = service.createUser(userDto);
        UserDto userDtoFromService = service.findUserById(userDtoCreate.getId());

        TypedQuery<User> query = em.createQuery("SELECT u FROM User u WHERE u.id = :id", User.class);
        User user = query.setParameter("id", userDtoFromService.getId()).getSingleResult();

        assertThat(user.getId(), equalTo(userDtoFromService.getId()));
        assertThat(user.getName(), equalTo(userDtoFromService.getName()));
        assertThat(user.getEmail(), equalTo(userDtoFromService.getEmail()));
    }

    @Test
    void patchUser() {
        UserDto userDto = makeUserDto("user", "user@user.com");
        UserDto userDtoCreate = service.createUser(userDto);
        UserDto userDtoUpdate = makeUserDto("update", "update@user.com");
        UserDto userDtoFromService = service.patchUser(userDtoCreate.getId(), userDtoUpdate);

        TypedQuery<User> query = em.createQuery("SELECT u FROM User u WHERE u.id = :id", User.class);
        User user = query.setParameter("id", userDtoFromService.getId()).getSingleResult();

        assertThat(user.getId(), equalTo(userDtoFromService.getId()));
        assertThat(user.getName(), equalTo(userDtoFromService.getName()));
        assertThat(user.getEmail(), equalTo(userDtoFromService.getEmail()));
    }

    @Test
    void findAllUser() {
        List<UserDto> sourceUsers = List.of(
                makeUserDto("user", "user@user.com"),
                makeUserDto("update", "update@user.com")
        );
        for (UserDto user : sourceUsers) {
            User entity = UserMapper.toUser(user);
            em.persist(entity);
        }
        em.flush();

        List<UserDto> targetUsers = service.findAllUser();

        assertThat(targetUsers, hasSize(sourceUsers.size()));
        for (UserDto sourceUser : sourceUsers) {
            assertThat(targetUsers, hasItem(allOf(
                    hasProperty("id", notNullValue()),
                    hasProperty("name", equalTo(sourceUser.getName())),
                    hasProperty("email", equalTo(sourceUser.getEmail()))
            )));
        }
    }

    @Test
    void deleteUser() {
        List<UserDto> sourceUsers = List.of(
                makeUserDto("user", "user@user.com"),
                makeUserDto("update", "update@user.com")
        );
        for (UserDto user : sourceUsers) {
            User entity = UserMapper.toUser(user);
            em.persist(entity);
        }
        em.flush();

        List<UserDto> targetUsers = service.findAllUser();
        service.deleteUser(targetUsers.get(0).getId());

        UserDto userDto = service.findAllUser().get(0);
        assertThat(userDto.getId(), notNullValue());
        assertThat(userDto.getName(), equalTo(sourceUsers.get(1).getName()));
        assertThat(userDto.getEmail(), equalTo(sourceUsers.get(1).getEmail()));
    }

    private UserDto makeUserDto(String name, String email) {
        UserDto userDto = new UserDto();
        userDto.setName(name);
        userDto.setEmail(email);
        return userDto;
    }
}
