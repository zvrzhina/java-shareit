package ru.practicum.shareit.item.dto;

import lombok.*;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.validation.Marker;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
@Builder(toBuilder = true)
public class ItemDto {
    private Long id;

    @NotNull(groups = Marker.OnCreate.class)
    @NotBlank(groups = Marker.OnCreate.class)
    private String name;

    @NotBlank(groups = Marker.OnCreate.class)
    private String description;

    @NotNull(groups = Marker.OnCreate.class)
    private Boolean available;

    private Long requestId;

    private BookingRequestDto lastBooking;

    private BookingRequestDto nextBooking;

    private List<CommentDto> comments;
}
