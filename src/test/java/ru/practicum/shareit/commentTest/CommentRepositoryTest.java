package ru.practicum.shareit.commentTest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@DataJpaTest
public class CommentRepositoryTest {

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    CommentRepository commentRepository;

    User user1;
    Item item1;
    Item item2;
    Comment comment1;

    @Test
    void findAllByItemIdTest() {

        user1 = userRepository.save(new User(null, "oleg", "oleg@mail.ru"));

        item1 = itemRepository
                .save(new Item(null, "umbrella", "umbrella", true, user1, null));
        item2 = itemRepository
                .save(new Item(null, "hat", "hat", true, user1, null));
        comment1 = commentRepository.save(new Comment(null, "good umbrella", item1, user1, LocalDateTime.now().minusSeconds(10)));

        List<Comment> results = commentRepository.findAllByItemId(item1.getId());

        Assertions.assertNotNull(results);
        Assertions.assertEquals(1, results.size());

        commentRepository.deleteAll();
        itemRepository.deleteAll();
        userRepository.deleteAll();
    }

}
