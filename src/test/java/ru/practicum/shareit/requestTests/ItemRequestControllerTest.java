package ru.practicum.shareit.requestTests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.ItemRequestController;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.dto.UserDto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ItemRequestControllerTest {

    @Autowired
    private ItemRequestController itemRequestController;

    @Autowired
    private UserController userController;

    private RequestDto requestDto;
    private RequestDto secondRequestDto;

    private UserDto userDto;
    private UserDto secondUserDto;

    private Errors errors;

    @BeforeEach
    void init() {

        requestDto = new RequestDto(null, "testDescription", null, null);
        secondRequestDto = new RequestDto(null, "secondDescription", null, null);
        userDto = new UserDto(null, "Oleg", "oleg@mail.ru");
        secondUserDto = new UserDto(null, "Another Oleg", "olegoleg@mail.ru");
        errors = new BeanPropertyBindingResult(requestDto, "itemRequestDto");
    }

    @Test
    void createTest() {
        UserDto user = userController.add(userDto, errors);
        RequestDto itemRequest = itemRequestController.create(user.getId(), requestDto);
        assertEquals(1L, itemRequestController.getById(itemRequest.getId(), user.getId()).getId());
    }

    @Test
    void createByIncorrectUserTest() {
        assertThrows(NotFoundException.class, () -> itemRequestController.create(1L, requestDto));
    }

    @Test
    void getAllByUserWithWrongUserTest() {
        assertThrows(NotFoundException.class, () -> itemRequestController.getAllByUser(1L));
    }

    @Test
    void getAllByUserTest() {
        UserDto user = userController.add(userDto, errors);
        itemRequestController.create(user.getId(), requestDto);
        UserDto userDto2 = userController.add(secondUserDto, errors);
        itemRequestController.create(userDto2.getId(), requestDto);

        assertEquals(1, itemRequestController.getAllByUser(user.getId()).size());
    }

    @Test
    void getAll() {
        UserDto userDto1 = userController.add(userDto, errors);
        itemRequestController.create(userDto1.getId(), requestDto);
        UserDto userDto2 = userController.add(secondUserDto, errors);
        itemRequestController.create(userDto2.getId(), secondRequestDto);

        assertEquals(1, itemRequestController.getAll(userDto1.getId(), 0, 10).size());
    }

    @Test
    void getAllByIncorrectUser() {
        assertThrows(NotFoundException.class, () -> itemRequestController.getAll(2L, 10, 1));
    }
}
