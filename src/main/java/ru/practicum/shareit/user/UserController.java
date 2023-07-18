package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.validation.Marker;

import javax.validation.Valid;
import java.util.Collection;

/**
 * TODO Sprint add-controllers.
 */
@Slf4j
@RestController
@RequestMapping(path = "/users")
public class UserController {

    @Autowired
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public Collection<UserDto> getAll() {
        log.info("Получен запрос GET /users");
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public UserDto getUserById(@PathVariable Long id) {
        log.info("Получен запрос GET /users/" + id);
        return userService.get(id);
    }

    @PostMapping
    @Validated({Marker.OnCreate.class})
    public UserDto add(@Valid @RequestBody UserDto user, Errors errors) {
        log.info("Получен запрос POST /users/");
        if (errors.hasErrors()) {
            throw new ValidationException("Произошла ошибка валидации - " + errors.getAllErrors());
        } else {
            return userService.add(user);
        }
    }

    @PatchMapping("/{id}")
    @Validated({Marker.OnUpdate.class})
    public UserDto update(@Valid @RequestBody UserDto newUser, @PathVariable long id, Errors errors) {
        log.info("Получен запрос PATCH /users/id");
        if (errors.hasErrors()) {
            throw new ValidationException("Произошла ошибка валидации - " + errors.getAllErrors());
        } else {
            return userService.update(newUser, id);
        }
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable long id) {
        log.info("Получен запрос DELETE /users/{}", id);
        userService.delete(id);
    }
}
