package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.Collection;

@RestController
@RequestMapping("/films")
@Slf4j
@RequiredArgsConstructor
public class FilmController {

    private final FilmStorage filmStorage;
    private final FilmService filmService;

    @GetMapping
    public Collection<Film> findAll() {
        log.info("Пришел запрос Get /films");
        return filmStorage.findAll();
    }

    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable Long id) {
        log.info("Пришел запрос Get /films/{}", id);
        return filmStorage.get(id);
    }

    @GetMapping("/popular")
    public Collection<Film> getPopularFilms(@RequestParam(defaultValue = "10") Long count) {
        log.info("Пришел запрос Get /films/popular");
        return filmService.getPopularFilms(count);
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        log.info("Пришел запрос Post /films с телом {}", film);
        return filmStorage.add(film);
    }

    @DeleteMapping
    public Film delete(@Valid @RequestBody Film film) {
        log.info("Пришел запрос Delete /films с телом {}", film);
        return filmStorage.remove(film);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public Film unlike(@PathVariable Long id, @PathVariable Long userId) {
        log.info("Пришел запрос Delete /films/{}/like/{}", id, userId);
        return filmService.unlike(id, userId);
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        log.info("Пришел запрос Put /films с телом {}", film);
        filmStorage.update(film);
        log.info("Отправлен ответ Put /films с телом {}", film);
        return film;
    }

    @PutMapping("/{id}/like/{userId}")
    public Film like(@PathVariable Long id, @PathVariable Long userId) {
        log.info("Пришел запрос Put /films/{}/like/{}", id, userId);
        return filmService.like(id, userId);
    }


}
