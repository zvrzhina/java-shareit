package ru.practicum.shareit.user.model;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * TODO Sprint add-controllers.
 */

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull(message = "Имя обязательно")
    @Column(name = "name", length = 50, nullable = false)
    private String name;

    @NotBlank(message = "Email обязателен")
    @Email(message = "Неверный email")
    @Column(unique = true, name = "email", length = 40, nullable = false)
    private String email;
}
