package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {

    private final Map<Long, User> users = new HashMap<>();
    private Long idCounter = 0L;


    @Override
    public Collection<User> findAll() {
        return users.values();
    }

    @Override
    public User add(User user) {
        emailValidate(user);
        nameUpdate(user);
        user.setId(getNextId());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User remove(User user) {
        if (!users.containsKey(user.getId())) {
            log.warn("Ошибка при удалении user с телом {}: указанный id не найден", user);
            throw new NotFoundException("Пользователь с id = " + user.getId() + " не найден");
        }
        users.remove(user.getId());
        return user;
    }

    @Override
    public User update(User user) {
        if (!users.containsKey(user.getId())) {
            log.warn("Ошибка при обновлении user с телом {}: указанный id не найден", user);
            throw new NotFoundException("Пользователь с id = " + user.getId() + " не найден");
        }
        if (!user.getEmail().equals(users.get(user.getId()).getEmail())) {
            emailValidate(user);
        }
        nameUpdate(user);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User get(Long id) {
        if (!users.containsKey(id)) {
            log.warn("Ошибка при поиске user с id {}: указанный id не найден", id);
            throw new NotFoundException("Пользователь с id = " + id + " не найден");
        }
        return users.get(id);
    }

    private void nameUpdate(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.trace("Имя пользователя {} ({}) приравнено его логину, т.к. оно не было указано",
                    user.getLogin(), user.getEmail());
        }
    }

    private void emailValidate(User user) throws ValidationException {
        if (users.values()
                .stream()
                .map(User::getEmail)
                .anyMatch(email -> email.equals(user.getEmail()))) {
            log.warn("Ошибка при обработке запроса с телом {}: указанный email уже используется", user);
            throw new ValidationException("Этот email уже используется");
        }
    }

    private long getNextId() {
        log.trace("Счетчик id фильмов увеличен");
        return ++idCounter;
    }
}
