package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder(toBuilder = true)
public class UserDto {
    private Long id;
    @NotNull
    private String name;
    @Email
    @NotBlank
    private String email;
}
