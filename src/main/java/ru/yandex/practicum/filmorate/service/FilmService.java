package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.LikeDbStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class FilmService {

    private final FilmStorage filmStorage;
    private final LikeDbStorage likeDbStorage;
    private final GenreDbStorage genreDbStorage;
    private static final LocalDate VALID_RELEASE_DATE = LocalDate.of(1895, 12, 28);

    public FilmService(@Autowired @Qualifier("FilmDbStorage") FilmStorage filmStorage,
                       @Autowired LikeDbStorage likeDbStorage,
                       @Autowired GenreDbStorage genreDbStorage) {
        this.filmStorage = filmStorage;
        this.likeDbStorage = likeDbStorage;
        this.genreDbStorage = genreDbStorage;
    }

    public void like(Long filmId, Long userId) {
        likeDbStorage.addLike(filmId, userId);
    }

    public void unlike(Long filmId, Long userId) {
        likeDbStorage.removeLike(filmId, userId);
    }

    public Collection<Film> getPopularFilms(Long count) {
        return likeDbStorage.getPopular(count);
    }

    public Collection<Film> getAll() {
        final Collection<Film> films = filmStorage.findAll();
        genreDbStorage.load(films);
        return films;
    }

    public Film getFilmById(Long id) {
        try {
            final Film film = filmStorage.get(id);
            genreDbStorage.load(Collections.singletonList(film));
            return film;
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Фильм не найден, message: " + e.getMessage());
        }
    }

    public Film create(Film film) {
        releaseDateValidation(film);
        return filmStorage.add(film);
    }

    public void delete(Film film) {
        filmStorage.remove(film);
    }

    public void deleteAll() {
        filmStorage.removeAll();
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
