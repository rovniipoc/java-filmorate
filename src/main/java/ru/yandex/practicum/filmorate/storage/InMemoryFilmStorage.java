package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component("InMemoryFilmStorage")
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Long, Film> films = new HashMap<>();
    private Long idCounter = 0L;

    @Override
    public Collection<Film> findAll() {
        return films.values();
    }

    @Override
    public Film add(Film film) {
        film.setId(getNextId());
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public void remove(Film film) {
        if (!films.containsKey(film.getId())) {
            log.warn("Ошибка при удалении film с телом {}: указанный id не найден", film);
            throw new NotFoundException("Фильм с id = " + film.getId() + " не найден");
        }
        films.remove(film.getId());
    }

    @Override
    public void removeAll() {
        films.clear();
    }

    @Override
    public Film update(Film film) {
        if (!films.containsKey(film.getId())) {
            log.warn("Ошибка при обновлении film с телом {}: указанный id не найден", film);
            throw new NotFoundException("Id = " + film.getId() + " не найден");
        }
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

//    @Override
//    public void addLike(Long filmId, Long userId) {
//        films.get(filmId).getLikes().add(userId);
//    }
//
//    @Override
//    public void removeLike(Long filmId, Long userId) {
//        films.get(filmId).getLikes().remove(userId);
//    }

    private long getNextId() {
        log.trace("Счетчик id фильмов увеличен");
        return ++idCounter;
    }
}
