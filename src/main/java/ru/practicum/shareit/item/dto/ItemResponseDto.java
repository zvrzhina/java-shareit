package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder(toBuilder = true)
public class ItemResponseDto {
    private Long id;

    private String name;

    private String description;

    private Boolean available;

    private Long requestId;

    private Long ownerId;

}
