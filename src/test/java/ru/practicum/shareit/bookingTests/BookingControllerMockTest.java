package ru.practicum.shareit.bookingTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.booking.model.Status.APPROVED;
import static ru.practicum.shareit.item.ItemMapper.toItem;
import static ru.practicum.shareit.user.UserMapper.toUser;

@WebMvcTest(BookingController.class)
@ContextConfiguration(classes = {BookingController.class, BookingService.class, BookingRepository.class})
public class BookingControllerMockTest {
    @MockBean
    private BookingService bookingService;
    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper mapper;

    private UserDto userDto;

    private ItemRequestDto itemRequestDto;

    private BookingDto bookingDto;

    private BookingRequestDto bookingRequestDto;

    @BeforeEach
    void init() {
        itemRequestDto = new ItemRequestDto(1L, "item", null, true, null, null);
        userDto = new UserDto(1L, "Oleg", "oleg@mail.ru");

        bookingDto = BookingDto
                .builder()
                .id(1L)
                .start(LocalDateTime.of(2025, 6, 16, 10, 10))
                .end(LocalDateTime.of(2025, 6, 18, 10, 10))
                .booker(toUser(userDto))
                .item(toItem(itemRequestDto))
                .build();

        bookingRequestDto = BookingRequestDto.builder()
                .start(LocalDateTime.of(2025, 6, 16, 10, 10))
                .end(LocalDateTime.of(2025, 6, 18, 10, 10))
                .itemId(1L)
                .bookerId(1L)
                .build();

    }

    @Test
    void createTest() throws Exception {
        when(bookingService.create(any(), anyLong()))
                .thenReturn(bookingDto);

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(bookingDto)));

        Mockito.verify(bookingService, Mockito.times(1))
                .create(any(), anyLong());
    }

    @Test
    void approveTest() throws Exception {
        bookingDto.setStatus(APPROVED);
        when(bookingService.approve(anyLong(), anyLong(), anyBoolean()))
                .thenReturn(bookingDto);

        mvc.perform(patch("/bookings/1?approved=true")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(bookingDto)));

        Mockito.verify(bookingService, Mockito.times(1))
                .approve(anyLong(), anyLong(), anyBoolean());
    }

    @Test
    void getAllByUserTest() throws Exception {
        when(bookingService.getAllByUser(anyLong(), anyString(), anyInt(), anyInt()))
                .thenReturn(List.of(bookingDto));

        mvc.perform(get("/bookings")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(bookingDto))));

        Mockito.verify(bookingService, Mockito.times(1))
                .getAllByUser(anyLong(), anyString(), anyInt(), anyInt());
    }

    @Test
    void getAllByOwnerTest() throws Exception {
        when(bookingService.getAllByOwner(anyLong(), anyString(), anyInt(), anyInt()))
                .thenReturn(List.of(bookingDto));

        mvc.perform(get("/bookings/owner")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(bookingDto))));

        Mockito.verify(bookingService, Mockito.times(1))
                .getAllByOwner(anyLong(), anyString(), anyInt(), anyInt());
    }


    @Test
    void getByIdTest() throws Exception {
        when(bookingService.getById(anyLong(), anyLong()))
                .thenReturn(bookingDto);

        mvc.perform(get("/bookings/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(bookingDto)));

        Mockito.verify(bookingService, Mockito.times(1))
                .getById(anyLong(), anyLong());
    }

}
