package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.validation.Marker;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@AllArgsConstructor
@Builder(toBuilder = true)
public class UserDto {
    private Long id;
    @NotNull(groups = Marker.OnCreate.class)
    private String name;
    @Email(groups = {Marker.OnCreate.class, Marker.OnUpdate.class})
    @NotBlank(groups = {Marker.OnCreate.class, Marker.OnUpdate.class})
    private String email;
}
