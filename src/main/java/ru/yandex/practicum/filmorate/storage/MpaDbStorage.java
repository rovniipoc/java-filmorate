package ru.yandex.practicum.filmorate.storage;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.Collection;

@Repository
public class MpaDbStorage extends BaseRepository<Mpa> {

    private static final String FIND_ALL_QUERY = "SELECT * FROM ratings ORDER BY id";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM ratings WHERE id = ?";
    private static final String INSERT_QUERY = "INSERT INTO ratings (id, mpa_name) VALUES (?, ?)";
    private static final String DELETE_QUERY = "DELETE FROM ratings WHERE id = ?";
    private static final String DELETE_ALL_QUERY = "DELETE FROM ratings";

    public MpaDbStorage(JdbcTemplate jdbc, RowMapper<Mpa> mapper) {
        super(jdbc, mapper);
    }

    public Collection<Mpa> findAllMpas() {
        return findMany(FIND_ALL_QUERY);
    }

    public Mpa getMpa(Long id) {
        return findOne(FIND_BY_ID_QUERY, id);
    }
}
