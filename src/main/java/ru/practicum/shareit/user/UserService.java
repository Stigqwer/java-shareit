package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    List<UserDto> findAllUser(Integer from, Integer size);

    UserDto findUserById(long userId);

    UserDto createUser(UserDto userDto);

    UserDto patchUser(long id, UserDto userDto);

    void deleteUser(long id);
}
