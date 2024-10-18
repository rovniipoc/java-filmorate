package ru.yandex.practicum.filmorate.storage;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;

import java.util.Collection;

@Repository
public class RatingDbStorage extends BaseRepository<Rating> {

    private static final String FIND_ALL_QUERY = "SELECT * FROM ratings";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM ratings WHERE id = ?";
    private static final String INSERT_QUERY = "INSERT INTO ratings(id, name) VALUES (?, ?)";
    private static final String DELETE_QUERY = "DELETE FROM ratings WHERE id = ?";

    public RatingDbStorage(JdbcTemplate jdbc, RowMapper<Rating> mapper) {
        super(jdbc, mapper);
    }

    public Collection<Rating> findAllRatings() {
        return findMany(FIND_ALL_QUERY);
    }

    public Rating getRating(Long id) {
        return findOne(FIND_BY_ID_QUERY, id);
    }

    public void addRating(Rating rating) {
        insert(
                INSERT_QUERY,
                rating.getId(),
                rating.getName()
        );
    }

    public void removeRating(Rating rating) {
        delete(
                DELETE_QUERY,
                rating.getId()
        );
    }

}
