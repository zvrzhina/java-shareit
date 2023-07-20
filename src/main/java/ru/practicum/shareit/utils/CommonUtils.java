package ru.practicum.shareit.utils;

import lombok.experimental.UtilityClass;
import org.springframework.data.domain.PageRequest;

@UtilityClass
public class CommonUtils {
    public static PageRequest getPageRequest(int from, int size) {
        int page = from % size == 0 ? from / size : from / size + 1;
        PageRequest pageRequest = PageRequest.of(page, size);
        return pageRequest;
    }
}
