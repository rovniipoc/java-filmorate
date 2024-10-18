package ru.yandex.practicum.filmorate.storage;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

@Repository
public class FriendshipDbStorage extends BaseRepository<Friendship> {

    private static final String FIND_BY_ID_QUERY = "SELECT * FROM user_friends WHERE user_id = ?";
    private static final String INSERT_QUERY = "INSERT INTO user_friends(user_id, user_friend_id) VALUES (?, ?)";
    private static final String DELETE_QUERY = "DELETE FROM user_friends WHERE user_id = ? AND user_friend_id = ?";


    public FriendshipDbStorage(JdbcTemplate jdbc, RowMapper<Friendship> mapper) {
        super(jdbc, mapper);
    }

    public Collection<Friendship> findAllFriendsByUser (User user) {
        return findMany(FIND_BY_ID_QUERY, user.getId());
    }

    public void addFriendship(User user, User userFriend) {
        insert(
                INSERT_QUERY,
                user.getId(),
                userFriend.getId()
        );
    }

    public void removeFriendship(User user, User userFriend)  {
        delete(
                DELETE_QUERY,
                user.getId(),
                userFriend.getId()
        );
    }
}
