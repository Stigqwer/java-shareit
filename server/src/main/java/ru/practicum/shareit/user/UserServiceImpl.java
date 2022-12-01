package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.validation.PaginationValidation;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public List<UserDto> findAllUser(Integer from, Integer size) {
        PaginationValidation.doValidation(from, size);
        return userRepository.findAll(PageRequest.of(((from) / size), size))
                .stream().map(UserMapper::toUserDto).collect(Collectors.toList());
    }

    @Override
    public UserDto findUserById(long userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()) {
            return UserMapper.toUserDto(user.get());
        } else {
            throw new UserNotFoundException(String.format("Пользователь с id %d не найден", userId));
        }
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        return UserMapper.toUserDto(userRepository.save(UserMapper.toUser(userDto)));
    }

    @Override
    public UserDto patchUser(long id, UserDto userDto) {
        UserDto user = findUserById(id);
        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }
        if (userDto.getEmail() != null) {
            user.setEmail(userDto.getEmail());
        }
        return UserMapper.toUserDto(userRepository.save(UserMapper.toUser(user)));
    }


    @Override
    public void deleteUser(long id) {
        userRepository.deleteById(id);
    }

}
