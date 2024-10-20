package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage {

    Collection<User> findAll();

    User add(User user);

    User remove(User user);

    User update(User user);

    User get(Long id);

    void addFriend (Long userId, Long friendId);

    void removeFriend (Long userId, Long friendId);

    Collection<User> getUserFriends(Long id);

    Collection<User> getCommonFriends(Long id1, Long id2);
}
