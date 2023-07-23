package ru.practicum.shareit.request;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.ArrayList;

@UtilityClass
public class ItemRequestMapper {
    public static ItemRequest toItemRequest(RequestDto requestDto) {
        return ItemRequest
                .builder()
                .id(requestDto.getId())
                .description(requestDto.getDescription())
                .build();
    }

    public static RequestDto toItemRequestDto(ItemRequest itemRequest) {
        return RequestDto
                .builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .items(new ArrayList<>())
                .build();
    }
}
