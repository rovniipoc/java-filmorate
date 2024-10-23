package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FriendshipDbStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;

@Slf4j
@Service
public class UserService {

    private final UserStorage userStorage;
    private final FriendshipDbStorage friendshipDbStorage;

    public UserService(@Autowired @Qualifier("UserDbStorage") UserStorage userStorage,
                       @Autowired FriendshipDbStorage friendshipDbStorage) {
        this.userStorage = userStorage;
        this.friendshipDbStorage = friendshipDbStorage;
    }

    public void makeFriends(Long id1, Long id2) {
        existUserIdValidate(userStorage.get(id1));
        existUserIdValidate(userStorage.get(id2));
        friendshipDbStorage.addFriendship(id1, id2);
    }

    public User unfriend(Long id1, Long id2) {
        existUserIdValidate(userStorage.get(id1));
        existUserIdValidate(userStorage.get(id2));
        friendshipDbStorage.removeFriendship(id1, id2);
        return getUserById(id1);
    }

    public Collection<User> getCommonFriends(Long id1, Long id2) {
        return userStorage.getCommonFriends(id1, id2);
    }

    public Collection<User> getFriends(Long id) {
        existUserIdValidate(userStorage.get(id));
        return getUsersByIds(userStorage.get(id).getFriends());
    }

    private Collection<User> getUsersByIds(Collection<Long> ids) {
        return ids.stream()
                .map(this::getUserById)
                .toList();
    }

    public Collection<User> findAll() {
        return userStorage.findAll();
    }

    public User getUserById(Long id) {
        User user = userStorage.get(id);
        existUserIdValidate(user);
        return user;
    }

    public User create(User user) {
        emailValidate(user);
        nameUpdate(user);
        return userStorage.add(user);
    }

    public User delete(User user) {
        existUserIdValidate(user);
        return userStorage.remove(user);
    }

    public User update(User user) {
        if (!user.getEmail().equals(getUserById(user.getId()).getEmail())) {
            emailValidate(user);
        }
        nameUpdate(user);
        return userStorage.update(user);
    }

    private void nameUpdate(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.trace("Имя пользователя {} ({}) приравнено его логину, т.к. оно не было указано",
                    user.getLogin(), user.getEmail());
        }
    }

    private void existUserIdValidate(User user) {
        if (user == null) {
            throw new NotFoundException("Пользователь не найден");
        }
    }

    private void emailValidate(User user) throws ValidationException {
        if (findAll().stream()
                .map(User::getEmail)
                .anyMatch(email -> email.equals(user.getEmail()))) {
            log.warn("Ошибка при обработке запроса с телом {}: указанный email уже используется", user);
            throw new ValidationException("Этот email уже используется");
        }
    }
}
