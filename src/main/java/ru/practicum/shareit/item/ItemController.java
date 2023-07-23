package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.validation.Marker;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@Slf4j
@RestController
@Validated
@AllArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @GetMapping
    public List<ItemDto> getAll(@RequestHeader("X-Sharer-User-Id") Long userId,
                                @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                @RequestParam(defaultValue = "10") @Positive int size) {
        log.info("Получен запрос GET /items");
        return itemService.getAll(userId, from, size);
    }

    @GetMapping("/{id}")
    public ItemDto getById(@PathVariable Long id, @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Получен запрос GET /items/id");
        return itemService.getById(id, userId);
    }

    @PostMapping
    @Validated({Marker.OnCreate.class})
    public ItemDto create(@Valid @RequestBody ItemRequestDto itemDto, @RequestHeader("X-Sharer-User-Id") Long userId, Errors errors) {
        log.info("Получен запрос POST /items");
        if (errors.hasErrors()) {
            throw new ValidationException("Произошла ошибка валидации - " + errors.getAllErrors());
        } else {
            return itemService.add(itemDto, userId);
        }
    }

    @PatchMapping("/{id}")
    public ItemDto update(@Valid @RequestBody ItemRequestDto itemDto, @PathVariable Long id, @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Получен запрос PATCH /items/id");
        return itemService.update(itemDto, id, userId);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        log.info("Получен запрос DELETE /items/id");
        itemService.delete(id);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam String text,
                                @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                @RequestParam(defaultValue = "10") @Positive int size) {
        log.info("Получен запрос PATCH /items/search");
        return itemService.search(text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto postComment(@PathVariable Long itemId, @RequestHeader("X-Sharer-User-Id") Long userId,
                                  @Valid @RequestBody CommentDto commentDto) {
        return itemService.postComment(itemId, userId, commentDto);
    }
}
