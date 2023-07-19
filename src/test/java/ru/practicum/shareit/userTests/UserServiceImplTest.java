package ru.practicum.shareit.userTests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;


public class UserServiceImplTest {
    UserRepository userRepository;
    UserService userService;

    @BeforeEach
    void beforeEach() {
        userRepository = Mockito.mock(UserRepository.class);
        userService = new UserServiceImpl(userRepository);
    }

    @Test
    void saveTest() {
        User user = new User(1L, "oleg", "oleg@mail.ru");
        UserDto userDto = UserMapper.toUserDto(user);

        Mockito.when(userRepository.save(Mockito.any(User.class)))
                .thenReturn(user);

        UserDto foundUser = userService.add(userDto);

        Assertions.assertNotNull(foundUser);
        Assertions.assertEquals(user.getId(), foundUser.getId());
        Assertions.assertEquals(user.getName(), foundUser.getName());
        Assertions.assertEquals(user.getEmail(), foundUser.getEmail());

        Mockito.verify(userRepository, Mockito.times(1))
                .save(Mockito.any(User.class));
    }

    @Test
    void updateTest() {
        User user = new User(1L, "oleg", "oleg@mail.ru");
        User updatedUser = new User(1L, "newOleg", "fresh.oleg@mail.ru");
        UserDto userDto = UserMapper.toUserDto(updatedUser);

        Mockito.when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));

        Mockito.when(userRepository.save(Mockito.any(User.class)))
                .thenReturn(updatedUser);

        UserDto foundedUser = userService.update(userDto, user.getId());

        Assertions.assertNotNull(foundedUser);
        Assertions.assertEquals(updatedUser.getId(), foundedUser.getId());
        Assertions.assertEquals(updatedUser.getName(), foundedUser.getName());
        Assertions.assertEquals(updatedUser.getEmail(), foundedUser.getEmail());
    }

    @Test
    void findAllTest() {
        List<User> users = new ArrayList<>();
        users.add(new User(1L, "oleg", "oleg@mail.ru"));

        Mockito.when(userRepository.findAll())
                .thenReturn(users);

        Collection<UserDto> usersDto = userService.getAllUsers();

        Assertions.assertNotNull(usersDto);
        Assertions.assertEquals(1, usersDto.size());

        Mockito.verify(userRepository, Mockito.times(1))
                .findAll();
    }
    
}
