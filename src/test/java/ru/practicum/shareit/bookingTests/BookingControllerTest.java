package ru.practicum.shareit.bookingTests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.ConstraintViolationException;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static ru.practicum.shareit.booking.model.Status.*;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class BookingControllerTest {
    @Autowired
    private BookingController bookingController;

    @Autowired
    private UserController userController;

    @Autowired
    private ItemController itemController;

    private ItemDto itemDto;

    private UserDto userDto;

    private UserDto secondUserDto;

    private BookingRequestDto bookingRequestDto;

    private Errors errors;

    @BeforeEach
    void init() {
        itemDto = new ItemDto(
                null, "item", "desc", true, null, null, null, null);

        userDto = new UserDto(null, "Oleg", "oleg@mail.ru");
        secondUserDto = new UserDto(null, "OlegOleg", "olegOlegovoch@mail.ru");

        bookingRequestDto = BookingRequestDto.builder()
                .start(LocalDateTime.of(2023, 6, 16, 10, 10))
                .end(LocalDateTime.of(2023, 6, 18, 10, 10))
                .itemId(1L)
                .bookerId(1L)
                .build();

        errors = new BeanPropertyBindingResult(itemDto, "ItemDto");
    }

    @Test
    void createByOwnerTest() {
        UserDto user = userController.add(userDto, errors);
        ItemDto item = itemController.create(itemDto, user.getId(), errors);
        assertThrows(NotFoundException.class, () -> bookingController.create(bookingRequestDto, user.getId(), errors));
    }

    @Test
    void createTest() {
        UserDto user = userController.add(userDto, errors);
        ItemDto item = itemController.create(itemDto, user.getId(), errors);
        UserDto secondUser = userController.add(secondUserDto, errors);
        BookingDto booking = bookingController.create(bookingRequestDto, secondUser.getId(), errors);
        assertEquals(1L, bookingController.getById(booking.getId(), secondUser.getId()).getId());
    }

    @Test
    void createByIncorrectUserTest() {
        assertThrows(ConstraintViolationException.class, () -> bookingController.create(bookingRequestDto, 1L, errors));
    }

    @Test
    void createToUnavailableItemTest() {
        UserDto user = userController.add(userDto, errors);
        itemDto.setAvailable(false);
        ItemDto item = itemController.create(itemDto, user.getId(), errors);
        UserDto second = userController.add(secondUserDto, errors);
        assertThrows(ValidationException.class, () -> bookingController.create(bookingRequestDto, 2L, errors));
    }

    @Test
    void approveStatusTest() {
        UserDto user = userController.add(userDto, errors);
        ItemDto item = itemController.create(itemDto, user.getId(), errors);
        UserDto secondUser = userController.add(secondUserDto, errors);
        BookingDto booking = bookingController.create(bookingRequestDto, secondUser.getId(), errors);
        assertEquals(WAITING, bookingController.getById(booking.getId(), secondUser.getId()).getStatus());
        bookingController.approve(booking.getId(), user.getId(), true);
        assertEquals(APPROVED, bookingController.getById(booking.getId(), secondUser.getId()).getStatus());
    }

    @Test
    void rejectStatusTest() {
        UserDto user = userController.add(userDto, errors);
        ItemDto item = itemController.create(itemDto, user.getId(), errors);
        UserDto secondUser = userController.add(secondUserDto, errors);
        BookingDto booking = bookingController.create(bookingRequestDto, secondUser.getId(), errors);
        assertEquals(WAITING, bookingController.getById(booking.getId(), secondUser.getId()).getStatus());
        bookingController.approve(booking.getId(), user.getId(), false);
        assertEquals(REJECTED, bookingController.getById(booking.getId(), secondUser.getId()).getStatus());
    }

    @Test
    void approveByWrongUserTest() {
        UserDto user = userController.add(userDto, errors);
        ItemDto item = itemController.create(itemDto, user.getId(), errors);
        UserDto secondUser = userController.add(secondUserDto, errors);
        BookingDto booking = bookingController.create(bookingRequestDto, secondUser.getId(), errors);
        assertThrows(NotFoundException.class, () -> bookingController.approve(1L, secondUser.getId(), true));
    }

    @Test
    void getAllByUserTest() {
        UserDto user = userController.add(userDto, errors);
        ItemDto item = itemController.create(itemDto, user.getId(), errors);
        UserDto secondUser = userController.add(secondUserDto, errors);
        BookingDto booking = bookingController.create(bookingRequestDto, secondUser.getId(), errors);
        assertEquals(1, bookingController.getAllByUser(secondUser.getId(), "WAITING", 0, 10).size());
        assertEquals(1, bookingController.getAllByUser(secondUser.getId(), "ALL", 0, 10).size());
        assertEquals(1, bookingController.getAllByUser(secondUser.getId(), "PAST", 0, 10).size());
        assertEquals(0, bookingController.getAllByUser(secondUser.getId(), "CURRENT", 0, 10).size());
        assertEquals(0, bookingController.getAllByUser(secondUser.getId(), "FUTURE", 0, 10).size());
        assertEquals(0, bookingController.getAllByUser(secondUser.getId(), "REJECTED", 0, 10).size());
        bookingController.approve(booking.getId(), user.getId(), true);
        assertEquals(0, bookingController.getAllByOwner(user.getId(), "CURRENT", 0, 10).size());
        assertEquals(1, bookingController.getAllByOwner(user.getId(), "ALL", 0, 10).size());
        assertEquals(0, bookingController.getAllByOwner(user.getId(), "WAITING", 0, 10).size());
        assertEquals(0, bookingController.getAllByOwner(user.getId(), "FUTURE", 0, 10).size());
        assertEquals(0, bookingController.getAllByOwner(user.getId(), "REJECTED", 0, 10).size());
        assertEquals(1, bookingController.getAllByOwner(user.getId(), "PAST", 0, 10).size());
    }

    @Test
    void getAllByIncorrectUserTest() {
        assertThrows(NotFoundException.class, () -> bookingController.getAllByUser(1L, "ALL", 0, 10));
        assertThrows(NotFoundException.class, () -> bookingController.getAllByOwner(1L, "ALL", 0, 10));
    }

}
