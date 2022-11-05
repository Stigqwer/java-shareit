package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping(path = "/users")
@AllArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    public List<UserDto> findAllUser() {
        return userService.findAllUser();
    }

    @GetMapping("/{userId}")
    public UserDto findUserById(@PathVariable long userId) {
        return userService.findUserById(userId);
    }

    @PostMapping
    public UserDto createUser(@RequestBody @Valid UserDto userDto) {
        return userService.createUser(userDto);
    }

    @PatchMapping("/{userId}")
    public UserDto patchUser(@PathVariable long userId, @RequestBody UserDto userDto) {
        return userService.patchUser(userId, userDto);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable long userId) {
        userService.deleteUser(userId);
    }
}
