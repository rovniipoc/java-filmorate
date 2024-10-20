package ru.yandex.practicum.filmorate.storage;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Friendship;

import java.util.Collection;

@Repository
public class FriendshipDbStorage extends BaseRepository<Friendship> {

    private static final String FIND_ALL_BY_USER_ID_QUERY = "SELECT * FROM user_friends WHERE user_id = ?";
//    private static final String FIND_BY_ID_QUERY = "SELECT * FROM user_friends WHERE user_id = ?";
    private static final String INSERT_QUERY = "INSERT INTO user_friends(user_id, user_friend_id) VALUES (?, ?)";
    private static final String DELETE_QUERY = "DELETE FROM user_friends WHERE user_id = ? AND user_friend_id = ?";
    private static final String FIND_ALL_QUERY = "SELECT * FROM user_friends";

    public FriendshipDbStorage(JdbcTemplate jdbc, RowMapper<Friendship> mapper) {
        super(jdbc, mapper);
    }

    public Collection<Friendship> findAllFriendsIdByUser(Long userId) {
//        return findMany(FIND_BY_ID_QUERY, userId).stream()
//                .map(Friendship::getUserFriendId)
//                .toList();
        return findMany(FIND_ALL_BY_USER_ID_QUERY, userId);
    }

    public void addFriendship(Long userId, Long userFriendId) {
        update(
                INSERT_QUERY,
                userId,
                userFriendId
        );
    }

    public void removeFriendship(Long userId, Long userFriendId)  {
        delete(
                DELETE_QUERY,
                userId,
                userFriendId
        );
    }

    public Collection<Friendship> getAll() {
        return findMany(FIND_ALL_QUERY);
    }
}
