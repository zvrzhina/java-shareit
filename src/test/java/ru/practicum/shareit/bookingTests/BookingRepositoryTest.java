package ru.practicum.shareit.bookingTests;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@DataJpaTest
public class BookingRepositoryTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    BookingRepository bookingRepository;

    User user1;
    User user2;
    User user3;
    Item item1;
    Booking booking1;
    Booking booking2;

    @BeforeEach
    void beforeEach() {
        user1 = userRepository.save(new User(null, "user1", "user1@mail.ru"));
        user2 = userRepository.save(new User(null, "user2", "user2@mail.ru"));
        user3 = userRepository.save(new User(null, "user3", "user3@mail.ru"));
        item1 = itemRepository.save(new Item(null, "testName", "testDescription", true, user3, null));
        booking1 = bookingRepository
                .save(new Booking(null,
                        LocalDateTime.now(),
                        LocalDateTime.now(),
                        item1,
                        user1,
                        Status.APPROVED));
        booking2 = bookingRepository
                .save(new Booking(null,
                        LocalDateTime.now(),
                        LocalDateTime.now(),
                        item1,
                        user2,
                        Status.APPROVED));
    }

    @AfterEach
    void afterEach() {
        bookingRepository.deleteAll();
        itemRepository.deleteAll();
        userRepository.deleteAll();
    }


    @Test
    void findNextBookingTest() {
        booking2.setStart(booking1.getStart().plusHours(1));
        booking2.setEnd(booking1.getEnd().plusHours(1));
        bookingRepository.save(booking1);

        List<Booking> nextBooking = bookingRepository.findNextBooking(LocalDateTime.now(), user3.getId(), item1.getId());

        Assertions.assertNotNull(nextBooking);
        Assertions.assertEquals(nextBooking.get(0).getBooker().getName(), user2.getName());
    }

    @Test
    void findLastBookingTest() {
        booking2.setEnd(booking1.getEnd().plusDays(1));
        bookingRepository.save(booking1);

        List<Booking> lastBooking = bookingRepository.findLastBooking(LocalDateTime.now(), user3.getId(), item1.getId());

        Assertions.assertNotNull(lastBooking);
        Assertions.assertEquals(lastBooking.get(0).getBooker().getName(), user2.getName());
    }

}
