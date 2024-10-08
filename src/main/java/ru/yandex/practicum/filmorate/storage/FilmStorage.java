package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmStorage {

    Collection<Film> findAll();

    Film add(Film film);

    Film remove(Film film);

    Film update(Film film);

    Film get(Long id);
}
