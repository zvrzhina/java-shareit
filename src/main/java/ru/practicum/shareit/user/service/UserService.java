package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;

public interface UserService {
    Collection<UserDto> getAllUsers();

    UserDto get(Long id);

    UserDto add(UserDto user);

    UserDto update(UserDto user, long id);

    void delete(Long id);
}
