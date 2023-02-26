package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static ru.practicum.shareit.item.ItemMapper.toItem;
import static ru.practicum.shareit.item.ItemMapper.toItemDto;

@Service
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemStorage itemStorage;
    private final UserStorage userStorage;

    public ItemServiceImpl(ItemStorage itemStorage, UserStorage userStorage) {
        this.itemStorage = itemStorage;
        this.userStorage = userStorage;
    }

    @Override
    public List<ItemDto> getAll(Long userId) {
        List<Item> items = itemStorage.getAll().stream()
                .filter(i -> userId.equals(i.getId()))
                .collect(Collectors.toList());
        List<ItemDto> ownerItems = new ArrayList<>();
        items.forEach(i -> ownerItems.add(toItemDto(i)));
        log.info("Список вещей владельца с id {} успешно отправлен", userId);
        return ownerItems;
    }

    @Override
    public ItemDto get(Long id) {
        Item item = itemStorage.get(id)
                .orElseThrow(() -> new NotFoundException(String.format("Вещь с id {} не найдена", id)));
        return toItemDto(item);
    }

    @Override
    public ItemDto add(ItemDto itemDto, Long userId) {
        User user = userStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с id {} не существует" + userId)));
        Item item = toItem(itemDto);
        item.setOwner(user);
        itemStorage.add(item);
        return toItemDto(item);
    }

    @Override
    public ItemDto update(ItemDto itemDto, Long id, Long userId) {
        Optional<ItemDto> founded = Optional.empty();
        for (ItemDto iDto : getAll(userId)) {
            if (id.equals(iDto.getId())) {
                log.info("Вещь для обновления найдена у владельца");
                founded = Optional.of(iDto);
            }
        }
        if (founded.isEmpty()) {
            throw new NotFoundException("У пользователя с id = " + userId + "нет вещи с id = " + id);
        }
        if (itemDto.getName() != null) {
            founded.get().setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            founded.get().setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            founded.get().setAvailable(itemDto.getAvailable());
        }
        founded.get().setId(id);
        itemStorage.update(toItem(founded.get()));
        return founded.get();
    }

    @Override
    public List<ItemDto> search(String text) {
        List<ItemDto> founded = new ArrayList<>();
        if (text.isBlank()) {
            return founded;
        }
        for (Item item : itemStorage.getAll()) {
            if (isFounded(text, item)) {
                founded.add(toItemDto(item));
            }
        }
        return founded;
    }

    private Boolean isFounded(String text, Item item) {
        return (item.getName().toLowerCase().contains(text.toLowerCase()) ||
                item.getDescription().toLowerCase().contains(text.toLowerCase())) && item.getAvailable();
    }
}
