package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {

    private static final LocalDate VALID_RELEASE_DATE = LocalDate.of(1895, 12, 28);
    private final Map<Long, Film> films = new HashMap<>();
    private Long idCounter = 0L;

    @GetMapping
    public Collection<Film> findAll() {
        log.info("Пришел запрос Get /films");
        Collection<Film> response = films.values();
        log.info("Отправлен ответ Get /films с телом {}", response);
        return response;
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        log.info("Пришел запрос Post /films с телом {}", film);
        releaseDateValidation(film);
        film.setId(getNextId());
        films.put(film.getId(), film);
        log.info("Отправлен ответ Post /films с телом {}", film);
        return film;
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        log.info("Пришел запрос Put /films с телом {}", film);
        if (!films.containsKey(film.getId())) {
            log.warn("Ошибка при обработке запроса Put /films с телом {}: указанный id не найден", film);
            throw new NotFoundException("Id = " + film.getId() + " не найден");
        }
        releaseDateValidation(film);
        films.put(film.getId(), film);
        log.info("Отправлен ответ Put /films с телом {}", film);
        return film;
    }

    private void releaseDateValidation(Film film) throws ValidationException {
        if (film.getReleaseDate().isBefore(VALID_RELEASE_DATE)) {
            log.warn("Ошибка при обработке запроса с телом {}: указанная дата релиза слишком старая", film);
            throw new ValidationException("Дата релиза не может быть раньше " + VALID_RELEASE_DATE);
        }
    }

    private long getNextId() {
        log.trace("Счетчик id фильмов увеличен");
        return ++idCounter;
    }
}
