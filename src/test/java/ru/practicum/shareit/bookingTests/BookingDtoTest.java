package ru.practicum.shareit.bookingTests;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class BookingDtoTest {
    @Autowired
    JacksonTester<BookingDto> json;

    @Test
    void bookingDtoTest() throws Exception {
        BookingDto bookingDto = new BookingDto(
                1L,
                LocalDateTime.of(2023, 07, 15, 12, 15, 59),
                LocalDateTime.of(2023, 07, 17, 00, 00, 1),
                null,
                null,
                null);

        JsonContent<BookingDto> result = json.write(bookingDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start")
                .isEqualTo(LocalDateTime.of(2023, 07, 15, 12, 15, 59).toString());
        assertThat(result).extractingJsonPathStringValue("$.end")
                .isEqualTo(LocalDateTime.of(2023, 07, 17, 00, 00, 1).toString());
    }
}
