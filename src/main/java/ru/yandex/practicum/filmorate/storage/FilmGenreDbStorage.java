package ru.yandex.practicum.filmorate.storage;

import org.springframework.jdbc.core.*;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmGenre;

import java.util.Collection;

@Repository
public class FilmGenreDbStorage extends BaseRepository<FilmGenre> {

    private static final String FIND_ALL_QUERY = "SELECT * FROM film_genres ORDER BY genre_id";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM film_genres WHERE film_id = ? ORDER BY genre_id";
    private static final String INSERT_QUERY = "INSERT INTO film_genres(film_id, genre_id) VALUES (?, ?)";
    private static final String DELETE_QUERY = "DELETE FROM film_genres WHERE film_id = ?";

    public FilmGenreDbStorage(JdbcTemplate jdbc, RowMapper<FilmGenre> mapper) {
        super(jdbc, mapper);
    }

    public Collection<FilmGenre> getAll() {
        return findMany(FIND_ALL_QUERY);
    }

    public Collection<FilmGenre> findGenresByFilm(Film film) {
        return findMany(FIND_BY_ID_QUERY, film.getId());
    }
}
