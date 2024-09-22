package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public Film like(Long filmId, Long userId) {
        userStorage.get(userId);
        Film film = filmStorage.get(filmId);
        film.getLikes().add(userId);
        return film;
    }

    public Film unlike(Long filmId, Long userId) {
        userStorage.get(userId);
        Film film = filmStorage.get(filmId);
        film.getLikes().remove(userId);
        return film;
    }

    public Collection<Film> getPopularFilms(Long count) {
        return filmStorage.findAll().stream()
                .sorted((f1, f2) -> Long.compare(f2.getLikes().size(), f1.getLikes().size()))
                .limit(count)
                .toList();
    }
}
