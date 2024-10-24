package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.mapper.FriendshipRowMapper;
import ru.yandex.practicum.filmorate.storage.mapper.UserRowMapper;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@ActiveProfiles("test")
@Import({UserDbStorage.class, UserRowMapper.class, FriendshipDbStorage.class, FriendshipRowMapper.class})
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserDbStorageTest {

    private final UserDbStorage userDbStorage;
    private final List<User> userList = new ArrayList<>();

    @BeforeEach
    public void addTestUsers() {
        userList.clear();
        userDbStorage.removeAll();

        User user1 = new User();
        user1.setName("testname1");
        user1.setEmail("test1@mail.com");
        user1.setLogin("testlogin1");
        user1.setBirthday(LocalDate.of(2001, 1, 1));
        user1 = userDbStorage.add(user1);
        userList.add(user1);

        User user2 = new User();
        user2.setName("testname2");
        user2.setEmail("test2@mail.com");
        user2.setLogin("testlogin2");
        user2.setBirthday(LocalDate.of(2002, 2, 2));
        user2 = userDbStorage.add(user2);
        userList.add(user2);

        User user3 = new User();
        user3.setName("testname3");
        user3.setEmail("test3@mail.com");
        user3.setLogin("testlogin3");
        user3.setBirthday(LocalDate.of(2003, 3, 3));
        user3 = userDbStorage.add(user3);
        userList.add(user3);
    }

    @Test
    void findUserByIdTest() {

        User user1 = userList.get(0);
        User user2 = userList.get(1);
        User user3 = userList.get(2);

        assertEquals(user1.getName(), userDbStorage.get(user1.getId()).getName());
        assertEquals(user2.getName(), userDbStorage.get(user2.getId()).getName());
        assertEquals(user3.getName(), userDbStorage.get(user3.getId()).getName());
    }

    @Test
    void findUnknownUserByIdTest() {

        User user4 = userDbStorage.get(999L);
        assertNull(user4);
    }

    @Test
    void getAllUsersShouldBeOk() {

        assertEquals(userList, userDbStorage.findAll());
    }
}
