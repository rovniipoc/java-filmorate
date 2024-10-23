package ru.yandex.practicum.filmorate.storage;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Like;
import ru.yandex.practicum.filmorate.storage.mapper.FilmRowMapper;

import java.util.List;

@Repository
public class LikeDbStorage extends BaseRepository<Like> {

    private static final String FIND_USER_LIKES_QUERY = "SELECT * FROM film_likes WHERE user_id = ?";
    private static final String FIND_FILM_LIKES_QUERY = "SELECT * FROM film_likes WHERE film_id = ?";
    private static final String ADD_LIKE_QUERY = "MERGE INTO film_likes (film_id, user_id) VALUES (?, ?)";
    private static final String UPDATE_RATE_QUERY = "UPDATE films f SET rate = (SELECT COUNT(fl.user_id) FROM film_likes fl WHERE fl.film_id = f.id) WHERE f.id = ?";
    private static final String DELETE_LIKE_QUERY = "DELETE FROM film_likes WHERE film_id = ? AND user_id = ?";
    private static final String GET_POPULAR_QUERY = "SELECT * FROM films f, ratings r WHERE f.rating_id = r.id ORDER BY rate DESC LIMIT ?";

    public LikeDbStorage(JdbcTemplate jdbc, RowMapper<Like> mapper) {
        super(jdbc, mapper);
    }

    public void addLike(long filmId, long userId) {
        jdbc.update(ADD_LIKE_QUERY, filmId, userId);
        updateRate(filmId);
    }

    private void updateRate(long filmId) {
        jdbc.update(UPDATE_RATE_QUERY, filmId);
    }

    public void removeLike(long filmId, long userId) {
        jdbc.update(DELETE_LIKE_QUERY, filmId, userId);
        updateRate(filmId);
    }

    public List<Film> getPopular(Long count) {
        return jdbc.query(GET_POPULAR_QUERY, FilmRowMapper::makeFilm, count);
    }

}
