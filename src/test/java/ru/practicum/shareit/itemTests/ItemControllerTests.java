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
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.ItemRequestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;
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

    private ItemDto itemDto;

    private UserDto userDto;
    private UserDto secondUserDto;

    private ItemRequestDto itemRequestDto;

    private CommentDto commentDto;

    private Errors errors;

    @BeforeEach
    void init() {

        itemDto = new ItemDto(
                null, "item", "desc", true, null, null, null, null);

        userDto = new UserDto(null, "Oleg", "oleg@mail.ru");
        secondUserDto = new UserDto(null, "OlegOleg", "olegOlegovoch@mail.ru");
        itemRequestDto = new ItemRequestDto(null, "itemDescription", null, null);
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
    void createByIncorrectUser() {
        assertThrows(NotFoundException.class, () -> itemController.create(itemDto, 1L, errors));
    }


    @Test
    void updateTest() {
        UserDto user = userController.add(userDto, errors);
        itemController.create(itemDto, 1L, errors);
        ItemDto updatedItem = new ItemDto(
                null, "Updated", "Updated", true, null, null, null, null);
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
    void searchWithNegativeFrom() {
        assertThrows(ConstraintViolationException.class, () -> itemController.search("text", -1, 10));
    }

    @Test
    void searchWithNegativeAndZeroSize() {
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
    void createCommentByIncorrectUser() {
        assertThrows(NotFoundException.class, () -> itemController.postComment(1L, 2L, commentDto));
    }

    @Test
    void createCommentForIncorrectItem() {
        assertThrows(NotFoundException.class, () -> itemController.postComment(2L, 1L, commentDto));
    }

}
