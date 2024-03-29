package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.validation.Marker;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@AllArgsConstructor
@Slf4j
@Validated
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @PostMapping
    @Validated({Marker.OnCreate.class})
    public RequestDto create(@RequestHeader("X-Sharer-User-Id") Long userId,
                             @Valid @RequestBody RequestDto requestDto) {
        log.info("Получен запрос POST /requests от юзера с id = " + userId);
        return itemRequestService.create(userId, requestDto);
    }

    @GetMapping
    public List<RequestDto> getAllByUser(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Получен запрос GET /requests от юзера с id = " + userId);
        return itemRequestService.getAllByUser(userId);
    }

    @GetMapping("/all")
    public List<RequestDto> getAll(@RequestHeader("X-Sharer-User-Id") Long userId,
                                   @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                   @RequestParam(defaultValue = "10") @Positive int size) {
        log.info("Получен запрос GET /requests/all от юзера с id = " + userId);
        return itemRequestService.getAll(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public RequestDto getById(@PathVariable Long requestId, @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Получен запрос GET /requests/" + requestId + " от юзера с id = " + userId);
        return itemRequestService.getById(userId, requestId);
    }

}
