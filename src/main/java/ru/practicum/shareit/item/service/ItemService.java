package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;

import java.util.List;

public interface ItemService {
    List<ItemDto> getAll(Long userId, int from, int size);

    ItemDto getById(Long id, Long userId);

    ItemDto add(ItemRequestDto itemDto, Long userId);

    ItemDto update(ItemRequestDto itemDto, Long id, Long userId);

    void delete(Long id);

    List<ItemDto> search(String text, int from, int size);

    CommentDto postComment(Long itemId, Long userId, CommentDto commentDto);
}
