package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserStorage userStorage;

    public User makeFriends(Long id1, Long id2) {
        User user1 = userStorage.get(id1);
        User user2 = userStorage.get(id2);
        user1.getFriends().add(user2.getId());
        user2.getFriends().add(user1.getId());
        return user1;
    }

    public User unfriend(Long id1, Long id2) {
        User user1 = userStorage.get(id1);
        User user2 = userStorage.get(id2);
        user1.getFriends().remove(user2.getId());
        user2.getFriends().remove(user1.getId());
        return user1;
    }

    public Collection<User> getCommonFriends(Long id1, Long id2) {
        return userStorage.findAll().stream()
                .filter(user -> user.getFriends().contains(id1))
                .filter(user -> user.getFriends().contains(id2))
                .toList();
    }

    public Collection<User> getFriends(Long id) {
        return getUsersByIds(userStorage.get(id).getFriends());
    }

    private Collection<User> getUsersByIds(Collection<Long> ids) {
        return ids.stream()
                .map(userStorage::get)
                .toList();
    }

    public Collection<User> findAll() {
        return userStorage.findAll();
    }

    public User getUserById(Long id) {
        return userStorage.get(id);
    }

    public User create(User user) {
        return userStorage.add(user);
    }

    public User delete(User user) {
        return userStorage.remove(user);
    }

    public User update(User user) {
        return userStorage.update(user);
    }
}
