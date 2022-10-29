package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    List<User> findAllUser();

    User findUserById(long userId);

    User createUser(UserDto userDto);

    User patchUser(long id, UserDto userDto);

    void deleteUser(long id);
}
