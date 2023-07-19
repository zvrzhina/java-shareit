package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.validation.Marker.OnCreate;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class RequestDto {
    private Long id;

    @NotBlank(groups = {OnCreate.class})
    private String description;

    private LocalDateTime created;

    private List<ru.practicum.shareit.item.dto.ItemRequestDto> items;
}
