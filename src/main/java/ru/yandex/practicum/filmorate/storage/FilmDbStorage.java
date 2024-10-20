package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmGenre;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Like;

import java.util.Collection;

@Slf4j
@Repository("FilmDbStorage")
public class FilmDbStorage extends BaseRepository<Film> implements FilmStorage {

    private final LikeDbStorage likeDbStorage;
    private final MpaDbStorage mpaDbStorage;
    private final GenreDbStorage genreDbStorage;
    private final FilmGenreDbStorage filmGenreDbStorage;

    private static final String FIND_ALL_QUERY = "SELECT * FROM films";
    private static final String INSERT_QUERY = "INSERT INTO films (name, description, releaseDate, duration, rating_id) VALUES (?, ?, ?, ?, ?)";
    private static final String DELETE_QUERY = "DELETE FROM films WHERE id = ?";
    private static final String UPDATE_QUERY = "UPDATE films SET name = ?, description = ?, releaseDate = ?, " +
            "duration = ?, rating_id = ? WHERE id = ?";
    public static final String FIND_BY_ID_QUERY = "SELECT * FROM films WHERE id = ?";

    public FilmDbStorage(JdbcTemplate jdbc, RowMapper<Film> mapper,
                         @Autowired LikeDbStorage likeDbStorage,
                         @Autowired MpaDbStorage mpaDbStorage,
                         @Autowired GenreDbStorage genreDbStorage,
                         @Autowired FilmGenreDbStorage filmGenreDbStorage) {
        super(jdbc, mapper);
        this.likeDbStorage = likeDbStorage;
        this.mpaDbStorage = mpaDbStorage;
        this.genreDbStorage = genreDbStorage;
        this.filmGenreDbStorage = filmGenreDbStorage;
    }

    @Override
    public Collection<Film> findAll() {
        Collection<Film> result = findMany(FIND_ALL_QUERY).stream()
                .peek(film -> film.getLikes().addAll(likeDbStorage.findLikesByFilm(film).stream()
                        .map(Like::getUserId)
                        .toList()))
                .peek(film -> film.getGenres().addAll(filmGenreDbStorage.findGenresByFilm(film).stream()
                        .map(FilmGenre::getGenreId)
                        .map(genreDbStorage::getGenre)
                        .toList()))
                .peek(film -> film.getMpa().setName(mpaDbStorage.getMpa(film.getMpa().getId()).getName()))
                .toList();
        return result;
    }

    @Override
    public Film add(Film film) {
        Long id = insert(
                INSERT_QUERY,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId()
        );
        if (!film.getGenres().isEmpty()) {
            filmGenreDbStorage.addManyGenresToFilm(id,
                    film.getGenres().stream()
                    .map(Genre::getId)
                    .toList());
        }
        film.setId(id);
        return film;
    }

    @Override
    public Film remove(Film film) {
        Long id = film.getId();
        delete(DELETE_QUERY, id);
        return film;
    }

    @Override
    public Film update(Film film) {
        update(
                UPDATE_QUERY,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId()
        );
        if (!film.getGenres().isEmpty()) {
            filmGenreDbStorage.removeGenresFromFilm(film.getId());
            filmGenreDbStorage.addManyGenresToFilm(film.getId(), film.getGenres().stream().map(Genre::getId).toList());
        }
        return film;
    }

    @Override
    public Film get(Long id) {
        Film film = findOne(FIND_BY_ID_QUERY, id);
        if (film != null) {
            film.getLikes().addAll(likeDbStorage.findLikesByFilm(film).stream()
                    .map(Like::getUserId).toList());
            film.getGenres().addAll(filmGenreDbStorage.findGenresByFilm(film).stream()
                    .map(FilmGenre::getGenreId)
                    .map(genreDbStorage::getGenre)
                    .toList());
            film.getMpa().setName(mpaDbStorage.getMpa(film.getMpa().getId()).getName());
        }
        return film;
    }

    @Override
    public void addLike(Long filmId, Long userId) {
        likeDbStorage.addLike(filmId, userId);
    }

    @Override
    public void removeLike(Long filmId, Long userId) {
        likeDbStorage.removeLike(filmId, userId);
    }
}
