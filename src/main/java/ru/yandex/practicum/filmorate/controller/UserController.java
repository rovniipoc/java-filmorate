package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;

@RestController
@RequestMapping("/users")
@Slf4j
@RequiredArgsConstructor
public class UserController {

    private final UserStorage userStorage;
    private final UserService userService;

    @GetMapping
    public Collection<User> findAll() {
        log.info("Пришел запрос Get /users");
        Collection<User> response = userStorage.findAll();
        log.info("Сформирован ответ Get /users с телом {}", response);
        return response;
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable Long id) {
        log.info("Пришел запрос Get /users/{}", id);
        return userStorage.get(id);
    }

    @GetMapping("/{id}/friends")
    public Collection<User> getFriends(@PathVariable Long id) {
        log.info("Пришел запрос Get /users/{}/friends", id);
        return userService.getFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public Collection<User> getCommonFriends(@PathVariable Long id, @PathVariable Long otherId) {
        log.info("Пришел запрос Get /users/{}/friends/common/{}", id, otherId);
        return userService.getCommonFriends(id, otherId);
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

    @DeleteMapping("/{id}/friends/{friendId}")
    public User unfriend(@PathVariable Long id, @PathVariable Long friendId) {
        log.info("Пришел запрос Delete /users/{}/friends/{}", id, friendId);
        return userService.unfriend(id, friendId);
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) {
        log.info("Пришел запрос Put /users с телом {}", user);
        userStorage.update(user);
        log.info("Отправлен ответ Put /users с телом {}", user);
        return user;
    }

    @PutMapping("/{id}/friends/{friendId}")
    public User makeFriend(@PathVariable Long id, @PathVariable Long friendId) {
        log.info("Пришел запрос Put /users/{}/friends/{}", id, friendId);
        return userService.makeFriends(id, friendId);
    }

}
