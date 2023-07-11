package ru.practicum.shareit.exception;

public class EmaiIsNotUniqueException extends RuntimeException {
    public EmaiIsNotUniqueException(String message) {
        super(message);
    }
}
