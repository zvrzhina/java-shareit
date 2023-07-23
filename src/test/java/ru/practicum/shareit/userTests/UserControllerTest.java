package ru.practicum.shareit.userTests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.dto.UserDto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class UserControllerTest {
    @Autowired
    private UserController userController;

    private UserDto user;
    private Errors errors;

    @BeforeEach
    void init() {
        user = new UserDto(null, "Oleg", "oleg@mail.ru");
        errors = new BeanPropertyBindingResult(user, "user");
    }

    @Test
    void addTest() {
        UserDto userDto = userController.add(user, errors);
        assertEquals(userDto.getId(), userController.getUserById(userDto.getId()).getId());
    }

    @Test
    void updateTest() {
        userController.add(user, errors);
        UserDto updatedUserDto = new UserDto(null, "NotOleg", "notOleg@mail.ru");
        userController.update(updatedUserDto, 1L, errors);
        assertEquals(updatedUserDto.getEmail(), userController.getUserById(1L).getEmail());
        assertEquals(updatedUserDto.getName(), userController.getUserById(1L).getName());
    }

    @Test
    void updateByIncorrectUserTest() {
        assertThrows(NotFoundException.class, () -> userController.update(user, 2L, errors));
    }

    @Test
        //this test also covers get method
    void deleteTest() {
        UserDto userDto = userController.add(user, errors);
        assertEquals(1, userController.getAll().size());
        userController.delete(userDto.getId());
        assertEquals(0, userController.getAll().size());
    }

    @Test
    void getByIncorrectIdTest() {
        assertThrows(NotFoundException.class, () -> userController.getUserById(99L));
    }
}
