package ru.practicum.shareit.user.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

import static ru.practicum.shareit.user.UserMapper.toUser;
import static ru.practicum.shareit.user.UserMapper.toUserDto;

@Service
@Slf4j
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private static final String USER_NOT_EXISTS_MSG = "Пользователь с id = %d не существует";
    private final UserRepository userRepository;


    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getAllUsers() {
        log.info("Список всех пользователей успешно отправлен");
        List<UserDto> users = new ArrayList<>();
        for (User user : userRepository.findAll()) {
            users.add(toUserDto(user));
        }
        return users;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto get(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new NotFoundException(String.format(USER_NOT_EXISTS_MSG, id)));
        log.info("Пользователь с id = {} успешно отправлен", id);
        return toUserDto(user);
    }

    @Override
    @Transactional
    public UserDto add(UserDto userDto) {
        User user = toUser(userDto);
        //checkIsEmailUnique(user);
        log.info("Пользователь с id = {} успешно добавлен", user.getId());
        return toUserDto(userRepository.save(user));
    }

    @Override
    @Transactional
    public UserDto update(UserDto userDto, long id) {
        User user = toUser(userDto);
        user.setId(id);
        User updatedUser = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format(USER_NOT_EXISTS_MSG, user.getId())));
        if (user.getName() != null) {
            updatedUser.setName(user.getName());
        }
        if (user.getEmail() != null && !user.getEmail().equals(updatedUser.getEmail())) {
            updatedUser.setEmail(user.getEmail());
        }
        log.info("Пользователь с id = {} успешно обновлен", user.getId());
        return toUserDto(userRepository.save(updatedUser));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        get(id);
        log.info("Пользователь с id = {} успешно удален", id);
        userRepository.deleteById(id);
    }

}
