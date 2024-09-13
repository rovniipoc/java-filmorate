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
    private Long idCounter = 0L;

    @GetMapping
    public Collection<User> findAll() {
        log.info("Пришел запрос Get /users");
        log.info("Отправлен ответ Get /users с телом {}", users.values());
        return users.values();
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        log.info("Пришел запрос Post /users с телом {}", user);
        emailValidate(user);
        nameUpdate(user);
        user.setId(getNextId());
        users.put(user.getId(), user);
        log.info("Отправлен ответ Post /users с телом {}", user);
        return user;
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) {
        log.info("Пришел запрос Put /users с телом {}", user);
        if (!users.containsKey(user.getId())) {
            log.warn("Ошибка при обработке запроса Put /users с телом {}: указанный id не найден", user);
            throw new NotFoundException("Пользователь с id = " + user.getId() + " не найден");
        }
        if (!user.getEmail().equals(users.get(user.getId()).getEmail())) {
            emailValidate(user);
        }
        nameUpdate(user);
        users.put(user.getId(), user);
        log.info("Отправлен ответ Put /users с телом {}", user);
        return user;
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
