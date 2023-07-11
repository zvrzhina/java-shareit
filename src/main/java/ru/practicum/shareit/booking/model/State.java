package ru.practicum.shareit.booking.model;

public enum State {
    ALL,
    CURRENT,
    PAST,
    FUTURE,
    WAITING,
    REJECTED;

    public static State includeValue(String stateText) {
        for (State state : State.values()) {
            if (state.name().equals(stateText)) {
                return state;
            }
        }
        return null;
    }
}
