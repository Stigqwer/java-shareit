package ru.practicum.shareit.user;


import java.util.List;

public interface UserStorage {
    List<User> findAllUser();

    User findUserById(long userId);

    User createUser(User user);

    User patchUser(User user);

    void deleteUser(long userId);
}