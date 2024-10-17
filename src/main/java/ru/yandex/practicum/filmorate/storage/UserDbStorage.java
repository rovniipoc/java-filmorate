package ru.yandex.practicum.filmorate.storage;

import org.hibernate.dialect.unique.CreateTableUniqueDelegate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;

@Repository
public class UserDbStorage extends BaseRepository<User> implements UserStorage {

    private static final String FIND_ALL_QUERY = "SELECT * FROM users";
    private static final String INSERT_QUERY = "INSERT INTO users(email, login, name, birthday)" +
            "VALUES (?, ?, ?, ?) returning id";
    private static final String DELETE_QUERY = "DELETE FROM users WHERE id = ?";
    private static final String UPDATE_QUERY = "UPDATE users SET email = ?, login = ?, name = ?, birthday = ?" +
            "WHERE id = ?";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM users WHERE id = ?";

    public UserDbStorage(JdbcTemplate jdbc, RowMapper<User> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public Collection<User> findAll() {
        return findMany(FIND_ALL_QUERY);
    }

    @Override
    public User add(User user) {
        Long id = insert(
                INSERT_QUERY,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday()
        );
        user.setId(id);
        return user;
    }

    @Override
    public User remove(User user) {
        Long id = user.getId();
        delete(DELETE_QUERY, id);
        return user;
    }

    @Override
    public User update(User user) {
        update(
                UPDATE_QUERY,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday()
        );
        return user;
    }

    @Override
    public User get(Long id) {
        return findOne(FIND_BY_ID_QUERY, id);
    }
}
