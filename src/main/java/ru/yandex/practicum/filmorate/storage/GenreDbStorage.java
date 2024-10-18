package ru.yandex.practicum.filmorate.storage;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;

import java.util.Collection;

@Repository
public class GenreDbStorage extends BaseRepository<Genre> {

    private static final String FIND_ALL_QUERY = "SELECT * FROM genres";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM genres WHERE id = ?";
    private static final String INSERT_QUERY = "INSERT INTO genres(id, name) VALUES (?, ?)";
    private static final String DELETE_QUERY = "DELETE FROM genres WHERE id = ?";

    public GenreDbStorage(JdbcTemplate jdbc, RowMapper<Genre> mapper) {
        super(jdbc, mapper);
    }

    public Collection<Genre> findAllGenres() {
        return findMany(FIND_ALL_QUERY);
    }

    public Genre getGenre(Long id) {
        return findOne(FIND_BY_ID_QUERY, id);
    }

    public void addGenre(Genre genre) {
        insert(
                INSERT_QUERY,
                genre.getId(),
                genre.getName()
        );
    }

    public void removeGenre(Genre genre) {
        delete(
                DELETE_QUERY,
                genre.getId()
        );
    }
}
