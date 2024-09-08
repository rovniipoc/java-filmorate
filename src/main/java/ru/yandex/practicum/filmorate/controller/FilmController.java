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
    private final Long MAX_DESCRIPTION_LENGTH = 200L;
    private final LocalDate VALID_RELEASE_DATE = LocalDate.of(1895, 12, 28);

    @GetMapping
    public Collection<Film> findAll() {
        log.debug("Отправка списка всех фильмов");
        return films.values();
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        if (film.getDescription().length() > MAX_DESCRIPTION_LENGTH) {
            log.warn("Указано слишком длинное описание");
            throw new ValidationException("Максимальная длина описания - " + MAX_DESCRIPTION_LENGTH + " символов");
        }
        if (film.getReleaseDate().isBefore(VALID_RELEASE_DATE)) {
            log.warn("Указана слишком старая дата релиза");
            throw new ValidationException("Дата релиза не может быть раньше " + VALID_RELEASE_DATE);
        }
        if (film.getDuration() < 0) {
            log.warn("Указана отрицательная продолжительность фильма");
            throw new ValidationException("Продолжительность фильма не может быть отрицательной");
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
            oldFilm.setName(film.getName());
            if (film.getDescription().length() <= MAX_DESCRIPTION_LENGTH) {
                oldFilm.setDescription(film.getDescription());
                log.trace("Описание фильма \"{}\" изменено", oldFilm.getName());
            }
            if (film.getReleaseDate().isAfter(VALID_RELEASE_DATE)) {
                oldFilm.setReleaseDate(film.getReleaseDate());
                log.trace("Дата релиза фильма \"{}\" изменена", oldFilm.getName());
            }
            if (film.getDuration() >= 0) {
                oldFilm.setDuration(film.getDuration());
                log.trace("Продолжительность фильма \"{}\" изменена", oldFilm.getName());
            }
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
