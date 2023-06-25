package ru.practicum.shareit.item.model;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * TODO Sprint add-controllers.
 */
@Data
@Builder
@Entity
@Table(name = "items")
public class Item {
    @Id
    @Column(name = "id")
    private Long id;

    @NotNull
    @NotBlank
    @Column(name = "name", length = 50, nullable = false)
    private String name;

    @NotNull
    @Column(name = "description", length = 300, nullable = false)
    private String description;

    @NotNull
    @Column(name = "available", nullable = false)
    private Boolean available;

    @ManyToOne
    @JoinColumn(name = "owner_id", referencedColumnName = "id")
    private User owner;

    @ManyToOne
    @JoinColumn(name = "request_id", referencedColumnName = "id")
    private ItemRequest request;
}
