package ru.practicum.shareit.itemTests;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.ItemDto;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class ItemDtoTest {
    @Autowired
    JacksonTester<ItemDto> json;

    @Test
    void itemDtoTest() throws Exception {
        ItemDto itemDto = new ItemDto(
                1L,
                "umbrella",
                "good",
                true,
                null,
                null,
                null,
                null);

        JsonContent<ItemDto> result = json.write(itemDto);

        assertThat(result).extractingJsonPathNumberValue("$.id")
                .isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name")
                .isEqualTo("umbrella");
        assertThat(result).extractingJsonPathStringValue("$.description")
                .isEqualTo("good");
        assertThat(result).extractingJsonPathBooleanValue("$.available")
                .isTrue();
    }
}
