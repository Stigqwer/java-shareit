package ru.practicum.shareit.user;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;
    private static long id = 0;

    public UserServiceImpl(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    @Override
    public List<User> findAllUser() {
        return userStorage.findAllUser();
    }

    @Override
    public User findUserById(long userId) {
        return userStorage.findUserById(userId);
    }

    @Override
    public User createUser(UserDto userDto) {
        validateEmail(userDto.getEmail());
        return userStorage.createUser(UserMapper.toUser(++id, userDto));
    }

    @Override
    public User patchUser(long id, UserDto userDto) {
        User user = findUserById(id);
        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }
        if (userDto.getEmail() != null) {
            validateEmail(userDto.getEmail());
            user.setEmail(userDto.getEmail());
        }
        deleteUser(id);
        return userStorage.patchUser(user);
    }


    @Override
    public void deleteUser(long id) {
        userStorage.deleteUser(id);
    }

    private void validateEmail(String email) {
        List<String> emails = userStorage.findAllUser().stream().map(User::getEmail).collect(Collectors.toList());
        if (emails.contains(email)) {
            throw new UserValidationException(String.format("%s уже существует", email));
        }
    }
}
