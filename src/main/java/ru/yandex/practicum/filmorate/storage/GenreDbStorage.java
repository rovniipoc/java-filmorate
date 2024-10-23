package ru.yandex.practicum.filmorate.storage;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.function.UnaryOperator.identity;

@Repository
public class GenreDbStorage extends BaseRepository<Genre> {

    private static final String FIND_ALL_QUERY = "SELECT * FROM genres ORDER BY id";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM genres WHERE id = ?";
    private static final String INSERT_QUERY = "INSERT INTO genres(id, genre_name) VALUES (?, ?)";
    private static final String DELETE_QUERY = "DELETE FROM genres WHERE id = ?";
    private static final String DELETE_ALL_QUERY = "DELETE FROM genres";

    public GenreDbStorage(JdbcTemplate jdbc, RowMapper<Genre> mapper) {
        super(jdbc, mapper);
    }

    public Collection<Genre> getAll() {
        return findMany(FIND_ALL_QUERY);
    }

    public Genre get(Long id) {
        return findOne(FIND_BY_ID_QUERY, id);
    }

    public void load(List<Film> films) {
        String inSql = String.join(",", Collections.nCopies(films.size(), "?"));
        final Map<Long, Film> filmById = films.stream().collect(Collectors.toMap(Film::getId, identity()));
        final String sqlQuery = "SELECT * FROM genres g, film_genres fg WHERE fg.genre_id = g.id AND fg.film_id IN (" + inSql + ")";
        jdbc.query(sqlQuery, (rs) -> {
            final Film film = filmById.get(rs.getLong("film_id"));
            film.addGenre(get(rs.getLong("genre_id")));
        }, films.stream().map(Film::getId).toArray());
    }
}
