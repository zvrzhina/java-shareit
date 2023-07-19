package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.ItemRequestMapper;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.request.ItemRequestMapper.toItemRequest;
import static ru.practicum.shareit.request.ItemRequestMapper.toItemRequestDto;
import static ru.practicum.shareit.utils.CommonUtils.getPageRequest;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemRequestServiceImpl implements ItemRequestService {
    private final UserRepository userRepository;

    private final ItemRepository itemRepository;

    private final ItemRequestRepository itemRequestRepository;


    @Transactional
    @Override
    public RequestDto create(Long userId, RequestDto requestDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с id {} не существует" + userId)));
        ItemRequest itemRequest = toItemRequest(requestDto);
        itemRequest.setCreated(LocalDateTime.now());
        itemRequest.setRequestor(user);
        itemRequestRepository.save(itemRequest);
        log.info("Создан Item Request для пользователя с id = " + userId + " и id запроса " + itemRequest.getId());
        return toItemRequestDto(itemRequest);
    }

    @Transactional(readOnly = true)
    @Override
    public List<RequestDto> getAllByUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с id {} не существует" + userId)));
        List<RequestDto> itemRequestsDto = itemRequestRepository.findAllByRequestorIdOrderByCreatedAsc(userId)
                .stream()
                .map(ItemRequestMapper::toItemRequestDto)
                .collect(Collectors.toList());
        itemRequestsDto.forEach(this::setItemsToItemRequestDto);
        log.info("Получены все запросы от пользователя с id = " + userId);
        return itemRequestsDto;
    }

    @Transactional(readOnly = true)
    @Override
    public List<RequestDto> getAll(Long userId, int from, int size) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с id {} не существует" + userId)));

        List<RequestDto> itemRequestsDto = itemRequestRepository.findAllByRequestorIdNotLike(userId, getPageRequest(from, size))
                .stream()
                .map(ItemRequestMapper::toItemRequestDto)
                .collect(Collectors.toList());
        itemRequestsDto.forEach(this::setItemsToItemRequestDto);
        return itemRequestsDto;
    }

    @Transactional(readOnly = true)
    @Override
    public RequestDto getById(Long userId, Long requestId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с id {} не существует" + userId)));
        ItemRequest itemRequest = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException(String.format("Запроса с id {} не существует" + requestId)));
        RequestDto requestDto = toItemRequestDto(itemRequest);
        setItemsToItemRequestDto(requestDto);

        return requestDto;
    }


    private void setItemsToItemRequestDto(RequestDto requestDto) {
        requestDto.setItems(itemRepository.findAllByRequestId(requestDto.getId())
                .stream()
                .map(ItemMapper::toItemResponseDto)
                .collect(Collectors.toList()));
    }
}
