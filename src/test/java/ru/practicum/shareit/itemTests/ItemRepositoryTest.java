package ru.practicum.shareit.itemTests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@DataJpaTest
public class ItemRepositoryTest {

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ItemRequestRepository itemRequestRepository;

    User user1;
    User user2;
    Item item1;
    Item item2;
    Item item3;
    ItemRequest request1;
    ItemRequest request2;
    ItemRequest request3;

    @Test
    void searchTest() {
        user1 = userRepository.save(new User(1L, "user1", "user1@mail.ru"));
        user2 = userRepository.save(new User(2L, "user2", "user2@mail.ru"));

        request1 = itemRequestRepository
                .save(new ItemRequest(1L, "request11", user1, LocalDateTime.now()));
        request2 = itemRequestRepository
                .save(new ItemRequest(2L, "request12", user1, LocalDateTime.now()));
        request3 = itemRequestRepository
                .save(new ItemRequest(3L, "request23", user2, LocalDateTime.now()));

        item1 = itemRepository
                .save(new Item(1L, "item1", "item1",
                        true, user1, request1));
        item2 = itemRepository
                .save(new Item(2L, "item2 pattern", "item2",
                        true, user2, request2));
        item3 = itemRepository
                .save(new Item(null, "item3 pattern", "item3",
                        true, user2, request3));

        List<Item> results = itemRepository.search("pattern", PageRequest.of(0, 10));

        Assertions.assertEquals(2, results.size());
    }
}
