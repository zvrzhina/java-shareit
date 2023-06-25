package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.EmailIsNotUniqueException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

import static ru.practicum.shareit.user.UserMapper.toUserDto;

@Service
@Slf4j
@RequiredArgsConstructor
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
    public UserDto add(User user) {
        checkIsEmailUnique(user);
        log.info("Пользователь с id = {} успешно добавлен", user.getId());
        return toUserDto(userRepository.save(user));
    }

    @Override
    @Transactional
    public UserDto update(User user, long id) {
        user.setId(id);
        User updatedUser = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format(USER_NOT_EXISTS_MSG, user.getId())));
        if (user.getName() != null) {
            updatedUser.setName(user.getName());
        }
        if (user.getEmail() != null && !user.getEmail().equals(updatedUser.getEmail())) {
            checkIsEmailUnique(user);
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

    private void checkIsEmailUnique(User user) {
        for (User u : userRepository.findAll()) {
            if (user.getEmail().equals(u.getEmail())) {
                throw new EmailIsNotUniqueException(String.format("Пользователь с таким email {} уже зарегистрирован", user.getEmail()));
            }
        }
    }
}
