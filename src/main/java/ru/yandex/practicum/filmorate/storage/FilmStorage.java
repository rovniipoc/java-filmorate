package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmStorage {

    Collection<Film> findAll();

    Film add(Film film);

    void remove(Film film);

    void removeAll();

    Film update(Film film);

    Film get(Long id);

    void addLike(Long filmId, Long userId);

    void removeLike(Long filmId, Long userId);
}
