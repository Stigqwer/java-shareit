package ru.practicum.shareit.user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    UserRepository mockUserRepository;

    UserService userService;

    @BeforeEach
    void beforeEach() {
        userService = new UserServiceImpl(mockUserRepository);
    }

    @Test
    void testOkFindAllUser() {
        Mockito.when(mockUserRepository.findAll(Mockito.any(PageRequest.class)))
                .thenReturn(new PageImpl<>(List.of(new User(1L, "update", "update@user.com"),
                        new User(2L, "user", "user@user.com"))));

        List<UserDto> userDtoList = userService.findAllUser(0,10);

        Assertions.assertEquals(List.of(new UserDto(1L, "update", "update@user.com"),
                new UserDto(2L, "user", "user@user.com")), userDtoList);
    }

    @Test
    void testOkFindUserById() {
        Mockito.when(mockUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(new User(1L, "update", "update@user.com")));

        UserDto userDto = userService.findUserById(1L);

        Assertions.assertEquals(new UserDto(1L, "update", "update@user.com"), userDto);
    }

    @Test
    void testUserNotFound() {
        Mockito.when(mockUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());

        UserNotFoundException userNotFoundException =
                Assertions.assertThrows(UserNotFoundException.class, () -> userService.findUserById(1L));

        Assertions.assertEquals("Пользователь с id 1 не найден", userNotFoundException.getMessage());
    }

    @Test
    void testOkCreateUser() {
        Mockito.when(mockUserRepository.save(Mockito.any(User.class)))
                .thenReturn(new User(1L, "update", "update@user.com"));

        UserDto userDto = userService.createUser(new UserDto(null, "update", "update@user.com"));

        Assertions.assertEquals(new UserDto(1L, "update", "update@user.com"), userDto);
    }

    @Test
    void testOkPatchUser() {
        Mockito.when(mockUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(new User(1L, "update", "update@user.com")));
        Mockito.when(mockUserRepository.save(Mockito.any(User.class)))
                .thenReturn(new User(1L, "user", "user@user.com"));

        UserDto userDto = userService.patchUser(1L, new UserDto(1L, "user", "user@user.com"));

        Assertions.assertEquals(new UserDto(1L, "user", "user@user.com"), userDto);
    }

    @Test
    void testOkDeleteUser() {
        userService.deleteUser(1L);

        Mockito.verify(mockUserRepository, Mockito.times(1))
                .deleteById(Mockito.anyLong());
    }
}
