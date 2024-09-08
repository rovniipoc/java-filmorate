package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {

    private final Map<Long, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> findAll() {
        log.debug("Отправка списка пользователей");
        return users.values();
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        if (users.values()
                .stream()
                .map(User::getEmail)
                .anyMatch(email -> email.equals(user.getEmail()))) {
            log.warn("Указанный email уже занят");
            throw new ValidationException("Этот email уже используется");
        }
        if (user.getLogin().contains(" ")) {
            log.warn("Указан некорректный логин");
            throw new ValidationException("Указан некорректный логин");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.trace("Имя пользователя {} ({}) приравнено его логину, т.к. оно не было указано", user.getLogin(),
                    user.getEmail());
        }
        user.setId(getNextId());
        log.trace("Пользователю {} ({}) присвоен id = {}", user.getLogin(), user.getEmail(), user.getId());
        users.put(user.getId(), user);
        log.debug("Добавлен пользователь {} ({})", user.getLogin(), user.getEmail());
        return user;
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) {
        if (user.getId() == null) {
            log.warn("В запросе отсутствует id");
            throw new ValidationException("Id должен быть указан");
        }
        if (users.containsKey(user.getId())) {
            User oldUser = users.get(user.getId());
            if (!oldUser.getEmail().equals(user.getEmail()) && users.values()
                    .stream()
                    .map(User::getEmail)
                    .anyMatch(email -> email.equals(user.getEmail()))) {
                log.warn("Указанный email уже занят");
                throw new ValidationException("Этот email уже используется");
            }
            oldUser.setEmail(user.getEmail());
            oldUser.setBirthday(user.getBirthday());
            oldUser.setLogin(user.getLogin());
            if (user.getName() != null && !user.getName().isBlank()) {
                oldUser.setName(user.getName());
                log.trace("Имя пользователя {} ({}) изменено", oldUser.getLogin(), user.getEmail());
            } else {
                oldUser.setName(user.getLogin());
                log.trace("Имя пользователя {} ({}) приравнено его логину, т.к. оно не было указано",
                        user.getLogin(), user.getEmail());
            }
            log.debug("Информация о пользователе {} ({}) изменена", oldUser.getLogin(), user.getEmail());
            return oldUser;
        }
        throw new NotFoundException("Пользователь с id = " + user.getId() + " не найден");
    }

    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        log.trace("Счетчик id пользователей увеличен");
        return ++currentMaxId;
    }
}
