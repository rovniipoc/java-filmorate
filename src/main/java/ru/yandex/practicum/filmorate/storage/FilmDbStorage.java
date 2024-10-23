package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmGenre;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Like;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

@Slf4j
@Repository("FilmDbStorage")
public class FilmDbStorage extends BaseRepository<Film> implements FilmStorage {

    private static final String FIND_ALL_QUERY = "SELECT * FROM films";
    private static final String INSERT_QUERY = "INSERT INTO films (name, description, releaseDate, duration, rating_id) VALUES (?, ?, ?, ?, ?)";
    private static final String DELETE_QUERY = "DELETE FROM films WHERE id = ?";
    private static final String UPDATE_QUERY = "UPDATE films SET name = ?, description = ?, releaseDate = ?, " +
            "duration = ?, rating_id = ? WHERE id = ?";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM films f, ratings r WHERE f.rating_id = r.id AND f.id = ?";
    private static final String DELETE_ALL_QUERY = "DELETE FROM films";

    public FilmDbStorage(JdbcTemplate jdbc, RowMapper<Film> mapper) {
        super(jdbc, mapper);
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
            saveGenres(film);
        }
        film.setId(id);
        return film;
    }

    private void saveGenres(Film film) {
        final Long filmId = film.getId();
        jdbc.update("delete from FILM_GENRES where FILM_ID = ?", filmId);
        final Set<Genre> genres = film.getGenres();
        if (genres == null || genres.isEmpty()) {
            return;
        }
        final ArrayList<Genre> genreList = new ArrayList<>(genres);
        jdbc.batchUpdate(
                "insert into FILM_GENRES (FILM_ID, GENRE_ID) values (?, ?)",
                new BatchPreparedStatementSetter() {
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setLong(1, filmId);
                        ps.setLong(2, genreList.get(i).getId());
                    }

                    public int getBatchSize() {
                        return genreList.size();
                    }
                });
    }

    @Override
    public void remove(Film film) {
        Long id = film.getId();
        delete(DELETE_QUERY, id);
    }

    @Override
    public void removeAll() {
        delete(DELETE_ALL_QUERY);
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
            saveGenres(film);
        }
        return film;
    }

    @Override
    public Film get(Long id) {
        return findOne(FIND_BY_ID_QUERY, id);
    }
}
