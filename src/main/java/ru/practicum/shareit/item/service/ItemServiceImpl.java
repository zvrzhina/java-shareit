package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.CommentMapper;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;
import static ru.practicum.shareit.booking.BookingMapper.toBookingRequestDto;
import static ru.practicum.shareit.booking.model.Status.APPROVED;
import static ru.practicum.shareit.item.CommentMapper.toComment;
import static ru.practicum.shareit.item.CommentMapper.toCommentDto;
import static ru.practicum.shareit.item.ItemMapper.toItem;
import static ru.practicum.shareit.item.ItemMapper.toItemDto;

@Service
@Slf4j
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ItemDto> getAll(Long userId) {
        List<ItemDto> ownerItems = new ArrayList<>();
        for (Item i : itemRepository.findAllByOwnerIdOrderByIdAsc(userId)) {
            ItemDto itemDto = toItemDto(i);
            Booking lastBooking = bookingRepository.findLastBooking(LocalDateTime.now(), userId, i.getId()).stream().findFirst().orElse(null);
            Booking nextBooking = bookingRepository.findNextBooking(LocalDateTime.now(), userId, i.getId()).stream().findFirst().orElse(null);

            itemDto.setLastBooking(lastBooking == null ? null : toBookingRequestDto(lastBooking));
            itemDto.setNextBooking(nextBooking == null ? null : toBookingRequestDto(nextBooking));

            itemDto.setComments(commentRepository.findAllByItemId(itemDto.getId())
                    .stream()
                    .map(CommentMapper::toCommentDto)
                    .collect(Collectors.toList()));

            ownerItems.add(itemDto);
        }
        log.info("Список вещей владельца с id {} успешно отправлен", userId);
        return ownerItems;
    }

    @Override
    @Transactional(readOnly = true)
    public ItemDto getById(Long id, Long userId) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Вещь с id {} не найдена", id)));
        ItemDto itemDto = toItemDto(item);

        itemDto.setComments(commentRepository.findAllByItemId(itemDto.getId())
                .stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList()));

        if (userId.equals(item.getOwner().getId())) {
            Booking lastBooking = bookingRepository.findLastBooking(LocalDateTime.now(), userId, id).stream().findFirst().orElse(null);
            Booking nextBooking = bookingRepository.findNextBooking(LocalDateTime.now(), userId, id).stream().findFirst().orElse(null);

            itemDto.setLastBooking(lastBooking == null ? null : toBookingRequestDto(lastBooking));
            itemDto.setNextBooking(nextBooking == null ? null : toBookingRequestDto(nextBooking));

        }
        return itemDto;
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
        if (founded.getOwner() != null && !userId.equals(founded.getOwner().getId())) {
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

    @Override
    @Transactional
    public CommentDto postComment(Long itemId, Long userId, CommentDto commentDto) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format("Вещь с id {} не найдена", itemId)));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с id {} не существует" + userId)));

        if (bookingRepository.findAllByItemIdAndBookerIdAndStatusEqualsAndEndIsBefore(itemId, userId, APPROVED, LocalDateTime.now()).isEmpty()) {
            throw new ValidationException("Пользователь, который не оставил завершил аренду или не брал вещь в аренду, не может оставлять комментарии.");
        }
        Comment comment = toComment(commentDto);
        comment.setItem(item);
        comment.setAuthor(user);
        comment.setCreated(LocalDateTime.now());
        commentRepository.save(comment);
        log.info("Коммент оставлен пользователем с id " + userId + " для вещи с id " + itemId);
        return toCommentDto(comment);
    }

}
