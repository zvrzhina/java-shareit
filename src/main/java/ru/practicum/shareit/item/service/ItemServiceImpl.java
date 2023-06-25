package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static ru.practicum.shareit.item.ItemMapper.toItem;
import static ru.practicum.shareit.item.ItemMapper.toItemDto;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ItemDto> getAll(Long userId) {
        List<ItemDto> ownerItems = new ArrayList<>();
        for (Item i : itemRepository.findAll()) {
            if (i.getOwner() != null && userId.equals(i.getOwner().getId())) {
                ownerItems.add(toItemDto(i));
            }
        }
        log.info("Список вещей владельца с id {} успешно отправлен", userId);
        return ownerItems;
    }

    @Override
    @Transactional(readOnly = true)
    public ItemDto get(Long id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Вещь с id {} не найдена", id)));
        return toItemDto(item);
    }

    @Override
    @Transactional
    public ItemDto add(ItemDto itemDto, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с id {} не существует" + userId)));
        Item item = toItem(itemDto);
        item.setOwner(user);
        itemRepository.save(item);
        return toItemDto(item);
    }

    @Override
    @Transactional
    public ItemDto update(ItemDto itemDto, Long id, Long userId) {
        Item founded = itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Не найдена вещь с id: " + id));
        if (founded.getOwner() != null && !founded.getOwner().getId().equals(userId)) {
            throw new NotFoundException("У пользователя с id = " + userId + "нет вещи с id = " + id);
        }

        if (itemDto.getName() != null) {
            founded.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            founded.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            founded.setAvailable(itemDto.getAvailable());
        }
        founded.setId(id);
        itemRepository.save(founded);
        return toItemDto(founded);
    }

    @Transactional
    @Override
    public void delete(Long id) {
        itemRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemDto> search(String text) {
        List<ItemDto> founded = new ArrayList<>();
        if (text.isBlank()) {
            return founded;
        }
        return itemRepository.search(text)
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(toList());
    }

    // ToDo remove if tests passed
//    private Boolean isFounded(String text, Item item) {
//        return (item.getName().toLowerCase().contains(text.toLowerCase()) ||
//                item.getDescription().toLowerCase().contains(text.toLowerCase())) && item.getAvailable();
//    }
}
