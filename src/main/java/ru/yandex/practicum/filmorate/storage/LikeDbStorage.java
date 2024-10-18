package ru.yandex.practicum.filmorate.storage;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Like;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

@Repository
public class LikeDbStorage extends BaseRepository<Like> {

    private static final String FIND_USER_LIKES_QUERY = "SELECT * FROM film_likes WHERE user_id = ?";
    private static final String FIND_FILM_LIKES_QUERY = "SELECT * FROM film_likes WHERE film_id = ?";
    private static final String INSERT_QUERY = "INSERT INTO film_likes(film_id, user_id) VALUES (?, ?)";
    private static final String DELETE_QUERY = "DELETE FROM film_likes WHERE film_id = ? AND user_id = ?";

    public LikeDbStorage(JdbcTemplate jdbc, RowMapper<Like> mapper) {
        super(jdbc, mapper);
    }

    public Collection<Like> findLikesByUser(User user) {
        return findMany(FIND_USER_LIKES_QUERY, user.getId());
    }

    public Collection<Like> findLikesByFilm(Film film) {
        return findMany(FIND_FILM_LIKES_QUERY, film.getId());
    }

    public void addLike(User user, Film film) {
        insert(
                INSERT_QUERY,
                film.getId(),
                user.getId()
        );
    }

    public void removeLike(User user, Film film) {
        delete(
                DELETE_QUERY,
                film.getId(),
                user.getId()
        );
    }

}
