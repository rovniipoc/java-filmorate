package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.Collection;

@RestController
@RequestMapping("/users")
@Slf4j
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public Collection<User> findAll() {
        log.info("Пришел запрос Get /users");
        Collection<User> response = userService.findAll();
        log.info("Сформирован ответ Get /users с телом {}", response);
        return response;
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable Long id) {
        log.info("Пришел запрос Get /users/{}", id);
        User response = userService.getUserById(id);
        log.info("Отправлен ответ Get /users/{} с телом: {}", id, response);
        return response;
    }

    @GetMapping("/{id}/friends")
    public Collection<User> getFriends(@PathVariable Long id) {
        log.info("Пришел запрос Get /users/{}/friends", id);
        Collection<User> response = userService.getFriends(id);
        log.info("Отправлен ответ Get /users/{}/friends с телом: {}", id, response);
        return response;
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public Collection<User> getCommonFriends(@PathVariable Long id, @PathVariable Long otherId) {
        log.info("Пришел запрос Get /users/{}/friends/common/{}", id, otherId);
        Collection<User> response = userService.getCommonFriends(id, otherId);
        log.info("Отправлен ответ Get /users/{}/friends/common/{} с телом: {}", id, otherId, response);
        return response;
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        log.info("Пришел запрос Post /users с телом {}", user);
        User response = userService.create(user);
        log.info("Сформирован ответ Post /users с телом {}", response);
        return response;
    }

    @DeleteMapping
    public User delete(@Valid @RequestBody User user) {
        log.info("Пришел запрос Delete /users с телом {}", user);
        User response = userService.delete(user);
        log.info("Сформирован ответ Delete /users с телом {}", response);
        return response;
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public User unfriend(@PathVariable Long id, @PathVariable Long friendId) {
        log.info("Пришел запрос Delete /users/{}/friends/{}", id, friendId);
        User response = userService.unfriend(id, friendId);
        log.info("Отправлен ответ Delete /users/{}/friends/{} с телом: {}", id, friendId, response);
        return response;
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) {
        log.info("Пришел запрос Put /users с телом {}", user);
        User response = userService.update(user);
        log.info("Отправлен ответ Put /users с телом {}", response);
        return response;
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void makeFriend(@PathVariable Long id, @PathVariable Long friendId) {
        log.info("Пришел запрос Put /users/{}/friends/{}", id, friendId);
        userService.makeFriends(id, friendId);
        log.info("Выполнен запрос Put /users/{}/friends/{}", id, friendId);
    }

}
