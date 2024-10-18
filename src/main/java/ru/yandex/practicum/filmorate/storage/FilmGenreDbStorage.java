package ru.yandex.practicum.filmorate.storage;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmGenre;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;

@Repository
public class FilmGenreDbStorage extends BaseRepository<FilmGenre> {

    private static final String FIND_BY_ID_QUERY = "SELECT * FROM film_genres WHERE film_id = ?";
    private static final String INSERT_QUERY = "INSERT INTO film_genres(film_id, genre_id) VALUES (?, ?)";
    private static final String DELETE_QUERY = "DELETE FROM film_genres WHERE film_id = ? AND genre_id = ?";

    public FilmGenreDbStorage(JdbcTemplate jdbc, RowMapper<FilmGenre> mapper) {
        super(jdbc, mapper);
    }

    public Collection<FilmGenre> findGenresByFilm(Film film) {
        return findMany(FIND_BY_ID_QUERY, film.getId());
    }

    public void addGenreToFilm(Genre genre, Film film) {
        insert(
                INSERT_QUERY,
                film.getId(),
                genre.getId()
        );
    }

    public void removeGenreFromFilm(Genre genre, Film film) {
        delete(
                DELETE_QUERY,
                film.getId(),
                genre.getId()
        );
    }
}
