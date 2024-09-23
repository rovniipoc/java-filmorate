package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.Collection;

@Service
@RequiredArgsConstructor
@Slf4j
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private static final LocalDate VALID_RELEASE_DATE = LocalDate.of(1895, 12, 28);

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

    public Collection<Film> getAll() {
        return filmStorage.findAll();
    }

    public Film getFilmById(Long id) {
        return filmStorage.get(id);
    }

    public Film create(Film film) {
        releaseDateValidation(film);
        return filmStorage.add(film);
    }

    public Film delete(Film film) {
        return filmStorage.remove(film);
    }

    public Film update(Film film) {
        releaseDateValidation(film);
        return filmStorage.update(film);
    }

    private void releaseDateValidation(Film film) throws ValidationException {
        if (film.getReleaseDate().isBefore(VALID_RELEASE_DATE)) {
            log.warn("Ошибка при обработке запроса с телом {}: указанная дата релиза слишком старая", film);
            throw new ValidationException("Дата релиза не может быть раньше " + VALID_RELEASE_DATE);
        }
    }
}
