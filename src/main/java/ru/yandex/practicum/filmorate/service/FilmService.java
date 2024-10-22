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
import ru.yandex.practicum.filmorate.storage.LikeDbStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.Collection;

@Service
@Slf4j
public class FilmService {

    private final FilmStorage filmStorage;
    private final LikeDbStorage likeDbStorage;
    private static final LocalDate VALID_RELEASE_DATE = LocalDate.of(1895, 12, 28);

    public FilmService(@Autowired @Qualifier("FilmDbStorage") FilmStorage filmStorage,
                       @Autowired LikeDbStorage likeDbStorage) {
        this.filmStorage = filmStorage;
        this.likeDbStorage = likeDbStorage;
    }

    public void like(Long filmId, Long userId) {
        likeDbStorage.addLike(filmId, userId);
    }

    public void unlike(Long filmId, Long userId) {
        likeDbStorage.removeLike(filmId, userId);
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
        try {
            return filmStorage.get(id);
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
