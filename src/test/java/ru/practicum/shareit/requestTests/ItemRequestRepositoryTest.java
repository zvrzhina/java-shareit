package ru.practicum.shareit.requestTests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@DataJpaTest
public class ItemRequestRepositoryTest {
    @Autowired
    UserRepository userRepository;

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    ItemRequestRepository itemRequestRepository;

    User user1;
    User user2;
    ItemRequest request1;
    ItemRequest request2;

    @Test
    void findAllByRequestorIdNotLikeTest() {
        user1 = userRepository.save(new User(null, "oleg", "oleg@mail.ru"));
        user2 = userRepository.save(new User(null, "notOleg", "notoleg@mail.ru"));

        request1 = itemRequestRepository
                .save(new ItemRequest(null, "item1", user1, LocalDateTime.now()));
        request2 = itemRequestRepository
                .save(new ItemRequest(null, "item2", user2, LocalDateTime.now()));


        List<ItemRequest> requests = itemRequestRepository.findAllByRequestorIdNotLike(
                user1.getId(),
                Pageable.unpaged());

        Assertions.assertEquals(1, requests.size());
        Assertions.assertEquals(request2.getId(), requests.get(0).getId());

        userRepository.deleteAll();
        itemRepository.deleteAll();
        itemRequestRepository.deleteAll();
    }
}
