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
        Collection<Film> response = filmStorage.findAll();
        log.info("Сформирован ответ Get /films с телом {}", response);
        return response;
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        log.info("Пришел запрос Post /films с телом {}", film);
        filmStorage.add(film);
        log.info("Сформирован ответ Post /films с телом {}", film);
        return film;
    }

    @DeleteMapping
    public Film delete(@Valid @RequestBody Film film) {
        log.info("Пришел запрос Delete /films с телом {}", film);
        filmStorage.remove(film);
        log.info("Сформирован ответ Delete /films с телом {}", film);
        return film;
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        log.info("Пришел запрос Put /films с телом {}", film);
        filmStorage.update(film);
        log.info("Отправлен ответ Put /films с телом {}", film);
        return film;
    }

}
