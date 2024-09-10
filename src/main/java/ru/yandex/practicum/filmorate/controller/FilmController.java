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

    private final Map<Long, Film> films = new HashMap<>();
    private static final LocalDate VALID_RELEASE_DATE = LocalDate.of(1895, 12, 28);

    @GetMapping
    public Collection<Film> findAll() {
        log.debug("Отправка списка всех фильмов");
        return films.values();
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        if (film.getReleaseDate().isBefore(VALID_RELEASE_DATE)) {
            log.warn("Указана слишком старая дата релиза");
            throw new ValidationException("Дата релиза не может быть раньше " + VALID_RELEASE_DATE);
        }
        film.setId(getNextId());
        log.trace("Фильму \"{}\" присвоен id = {}", film.getName(), film.getId());
        films.put(film.getId(), film);
        log.debug("Добавлен фильм \"{}\"", film.getName());
        return film;
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        if (film.getId() == null) {
            log.warn("В запросе отсутствует id");
            throw new ValidationException("Id должен быть указан");
        }
        if (films.containsKey(film.getId())) {
            Film oldFilm = films.get(film.getId());
            if (film.getReleaseDate().isBefore(VALID_RELEASE_DATE)) {
                log.trace("Дата релиза фильма \"{}\" изменена", oldFilm.getName());
                throw new ValidationException("Дата релиза не может быть раньше " + VALID_RELEASE_DATE);
            } else {
                oldFilm.setReleaseDate(film.getReleaseDate());
            }
            oldFilm.setName(film.getName());
            oldFilm.setDescription(film.getDescription());
            oldFilm.setDuration(film.getDuration());
            log.debug("Информация о фильме {} (id = {}) изменена", oldFilm.getName(), oldFilm.getId());
            return oldFilm;
        }
        log.warn("В запросе указан несуществующий id = {}", film.getId());
        throw new NotFoundException("Id = " + film.getId() + " не найден");
    }

    private long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        log.trace("Счетчик id фильмов увеличен");
        return ++currentMaxId;
    }
}
