package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {

    private final UserStorage userStorage = new InMemoryUserStorage();

    @GetMapping
    public Collection<User> findAll() {
        log.info("Пришел запрос Get /users");
        Collection<User> response = userStorage.findAll();
        log.info("Сформирован ответ Get /users с телом {}", response);
        return response;
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        log.info("Пришел запрос Post /users с телом {}", user);
        userStorage.add(user);
        log.info("Сформирован ответ Post /users с телом {}", user);
        return user;
    }

    @DeleteMapping
    public User delete(@Valid @RequestBody User user) {
        log.info("Пришел запрос Delete /users с телом {}", user);
        userStorage.remove(user);
        log.info("Сформирован ответ Delete /users с телом {}", user);
        return user;
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) {
        log.info("Пришел запрос Put /users с телом {}", user);
        userStorage.update(user);
        log.info("Отправлен ответ Put /users с телом {}", user);
        return user;
    }

}
