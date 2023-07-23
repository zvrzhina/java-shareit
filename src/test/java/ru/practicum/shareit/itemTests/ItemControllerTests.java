package ru.practicum.shareit.itemTests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.request.ItemRequestController;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ItemControllerTests {

    @Autowired
    private ItemController itemController;

    @Autowired
    private UserController userController;

    @Autowired
    private BookingController bookingController;

    @Autowired
    private ItemRequestController itemRequestController;

    private ItemRequestDto itemDto;

    private UserDto userDto;
    private UserDto secondUserDto;

    private RequestDto requestDto;

    private CommentDto commentDto;

    private Errors errors;

    @BeforeEach
    void init() {

        itemDto = new ru.practicum.shareit.item.dto.ItemRequestDto(
                null, "item", "desc", true, null, null);

        userDto = new UserDto(null, "Oleg", "oleg@mail.ru");
        secondUserDto = new UserDto(null, "OlegOleg", "olegOlegovoch@mail.ru");
        requestDto = new RequestDto(null, "itemDescription", null, null);
        commentDto = new CommentDto(null, "comment", null, null);


        errors = new BeanPropertyBindingResult(itemDto, "IteDto");
    }

    @Test
    void createTest() {
        UserDto user = userController.add(userDto, errors);
        ItemDto item = itemController.create(itemDto, 1L, errors);
        assertEquals(item.getName(), itemController.getById(item.getId(), user.getId()).getName());
    }

    @Test
    void createByIncorrectUserTest() {
        assertThrows(NotFoundException.class, () -> itemController.create(itemDto, 1L, errors));
    }

    @Test
    void createWithIncorrectRequestTest() {
        itemDto.setRequestId(99L);
        assertThrows(NotFoundException.class, () -> itemController.create(itemDto, 1L, errors));
    }


    @Test
    void updateTest() {
        UserDto user = userController.add(userDto, errors);
        itemController.create(itemDto, 1L, errors);
        ItemRequestDto updatedItem = new ItemRequestDto(
                null, "Updated", "Updated", true, null, null);
        itemController.update(updatedItem, 1L, 1L);
        assertEquals(updatedItem.getName(), itemController.getById(1L, 1L).getName());
    }

    @Test
    void updateForIncorrectItemIdTest() {
        assertThrows(NotFoundException.class, () -> itemController.update(itemDto, 2L, 1L));
    }

    @Test
    void updateByIncorrectUserTest() {
        userController.add(userDto, errors);
        itemController.create(itemDto, 1L, errors);
        assertThrows(NotFoundException.class, () -> itemController.update(itemDto, 1L, 2L));
    }

    @Test
    void deleteTest() {
        userController.add(userDto, errors);
        itemController.create(itemDto, 1L, errors);
        assertEquals(1, itemController.getAll(1L, 0, 10).size());
        itemController.delete(1L);
        assertEquals(0, itemController.getAll(1L, 0, 10).size());
    }

    @Test
    void deleteForIncorrectItemIdTest() {
        assertThrows(NotFoundException.class, () -> itemController.delete(2L));
    }

    @Test
    void searchTest() {
        userController.add(userDto, errors);
        itemController.create(itemDto, 1L, errors);
        assertEquals(itemDto.getName(), itemController.search("Desc", 0, 10).get(0).getName());
    }

    @Test
    void searchWithNegativeFromTest() {
        assertThrows(ConstraintViolationException.class, () -> itemController.search("text", -1, 10));
    }

    @Test
    void searchWithNegativeAndZeroSizeTest() {
        assertThrows(ConstraintViolationException.class, () -> itemController.search("text", 0, -10));
        assertThrows(ConstraintViolationException.class, () -> itemController.search("text", 0, 0));
    }


    @Test
    void searchEmptyTextTest() {
        userController.add(userDto, errors);
        itemController.create(itemDto, 1L, errors);
        assertEquals(new ArrayList<ItemDto>(), itemController.search("", 0, 10));
    }

    @Test
    void createCommentTest() throws InterruptedException {
        userController.add(userDto, errors);
        UserDto second = userController.add(secondUserDto, errors);
        ItemDto item = itemController.create(itemDto, 1L, errors);

        bookingController.create(BookingRequestDto.builder()
                        .start(LocalDateTime.now().plusSeconds(1))
                        .end(LocalDateTime.now().plusSeconds(2))
                        .itemId(item.getId())
                        .bookerId(second.getId())
                        .build(),
                second.getId(), errors);
        bookingController.approve(1L, 1L, true);
        TimeUnit.SECONDS.sleep(2);
        itemController.postComment(item.getId(), second.getId(), commentDto);
        assertEquals(commentDto.getText(), itemController.getById(1L, 1L).getComments().get(0).getText());
    }

    @Test
    void createCommentByIncorrectUserTest() {
        assertThrows(NotFoundException.class, () -> itemController.postComment(1L, 2L, commentDto));
    }

    @Test
    void createCommentForIncorrectItemTest() {
        assertThrows(NotFoundException.class, () -> itemController.postComment(2L, 1L, commentDto));
    }

    @Test
    void userCantPostCommentIfHeDidntBookItemTest() {
        userController.add(userDto, errors);
        UserDto second = userController.add(secondUserDto, errors);
        ItemDto item = itemController.create(itemDto, 1L, errors);

        assertThrows(ValidationException.class, () -> itemController.postComment(item.getId(), second.getId(), commentDto));
    }

}
