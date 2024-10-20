package ru.yandex.practicum.filmorate.storage.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class MpaRowMapper implements RowMapper<Mpa> {
    @Override
    public Mpa mapRow(ResultSet rs, int rowNum) throws SQLException {
        return Mpa.builder()
                .id(rs.getLong("id"))
                .name(rs.getString("name"))
                .build();
//        Rating rating = new Rating();
//        rating.setId(rs.getLong("id"));
//        rating.setName(rs.getString("name"));
//        return rating;
    }
}
