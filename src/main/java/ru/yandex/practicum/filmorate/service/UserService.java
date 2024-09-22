package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserStorage userStorage;

    public User makeFriends(User user1, User user2) {
        user1.getFriends().add(user2.getId());
        user2.getFriends().add(user1.getId());
        return user1;
    }

    public User unfriend(User user1, User user2) {
        user1.getFriends().remove(user2.getId());
        user2.getFriends().remove(user1.getId());
        return user1;
    }

    public Collection<User> getCommonFriends(User user1, User user2) {
        return userStorage.findAll().stream()
                .filter(user -> user.getFriends().contains(user1.getId()))
                .filter(user -> user.getFriends().contains(user2.getId()))
                .toList();
    }
}
