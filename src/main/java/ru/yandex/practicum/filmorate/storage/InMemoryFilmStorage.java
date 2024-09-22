package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {

    private static final LocalDate VALID_RELEASE_DATE = LocalDate.of(1895, 12, 28);
    private final Map<Long, Film> films = new HashMap<>();
    private Long idCounter = 0L;

    @Override
    public Collection<Film> findAll() {
        return films.values();
    }

    @Override
    public Film add(Film film) {
        releaseDateValidation(film);
        film.setId(getNextId());
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film remove(Film film) {
        if (!films.containsKey(film.getId())) {
            log.warn("Ошибка при удалении film с телом {}: указанный id не найден", film);
            throw new NotFoundException("Фильм с id = " + film.getId() + " не найден");
        }
        films.remove(film.getId());
        return film;
    }

    @Override
    public Film update(Film film) {
        if (!films.containsKey(film.getId())) {
            log.warn("Ошибка при обновлении film с телом {}: указанный id не найден", film);
            throw new NotFoundException("Id = " + film.getId() + " не найден");
        }
        releaseDateValidation(film);
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film get(Long id) {
        if (!films.containsKey(id)) {
            log.warn("Ошибка при поиске film с id {}: указанный id не найден", id);
            throw new NotFoundException("Фильм с id = " + id + " не найден");
        }
        return films.get(id);
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
