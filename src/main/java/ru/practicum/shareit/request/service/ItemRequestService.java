package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.RequestDto;

import java.util.List;

public interface ItemRequestService {
    RequestDto create(Long userId, RequestDto requestDto);

    List<RequestDto> getAllByUser(Long userId);

    List<RequestDto> getAll(Long userId, int from, int size);

    RequestDto getById(Long userId, Long requestId);


}
