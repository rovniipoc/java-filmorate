package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;

@RestController
@RequestMapping("/films")
@Slf4j
@RequiredArgsConstructor
public class FilmController {

    private final FilmService filmService;

    @GetMapping
    public Collection<Film> findAll() {
        log.info("Пришел запрос Get /films");
        Collection<Film> response = filmService.getAll();
        log.info("Отправлен ответ Get /films с телом: {}", response);
        return response;
    }

    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable Long id) {
        log.info("Пришел запрос Get /films/{}", id);
        Film response = filmService.getFilmById(id);
        log.info("Отправлен ответ Get /films/{} с телом: {}", id, response);
        return response;
    }

    @GetMapping("/popular")
    public Collection<Film> getPopularFilms(@RequestParam(defaultValue = "10") Long count) {
        log.info("Пришел запрос Get /films/popular");
        Collection<Film> response = filmService.getPopularFilms(count);
        log.info("Отправлен ответ Get /films/popular с телом: {}", response);
        return response;
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        log.info("Пришел запрос Post /films с телом {}", film);
        Film response = filmService.create(film);
        log.info("Отправлен ответ Post /films с телом {}", response);
        return response;
    }

    @DeleteMapping
    public void delete(@Valid @RequestBody Film film) {
        log.info("Пришел запрос Delete /films с телом {}", film);
        filmService.delete(film);
        log.info("Выполнен запрос Delete /films");
    }

    @DeleteMapping("/all")
    public void deleteAll() {
        log.info("Пришел запрос Delete /films/all");
        filmService.deleteAll();
        log.info("Выполнен запрос Delete /films/all");
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void unlike(@PathVariable Long id, @PathVariable Long userId) {
        log.info("Пришел запрос Delete /films/{}/like/{}", id, userId);
        filmService.unlike(id, userId);
        log.info("Выполнен запрос Delete /films/{}/like/{}", id, userId);
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        log.info("Пришел запрос Put /films с телом {}", film);
        Film response = filmService.update(film);
        log.info("Отправлен ответ Put /films с телом {}", film);
        return response;
    }

    @PutMapping("/{id}/like/{userId}")
    public void like(@PathVariable Long id, @PathVariable Long userId) {
        log.info("Пришел запрос Put /films/{}/like/{}", id, userId);
        filmService.like(id, userId);
        log.info("Выполнен запрос Put /films/{}/like/{}", id, userId);
    }


}
