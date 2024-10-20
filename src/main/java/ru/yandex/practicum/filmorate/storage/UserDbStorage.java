package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

@Repository("UserDbStorage")
public class UserDbStorage extends BaseRepository<User> implements UserStorage {

    FriendshipDbStorage friendshipDbStorage;

    private static final String FIND_ALL_QUERY = "SELECT * FROM users";
    private static final String INSERT_QUERY = "INSERT INTO users (email, login, name, birthday) VALUES (?, ?, ?, ?)";
    private static final String DELETE_QUERY = "DELETE FROM users WHERE id = ?";
    private static final String UPDATE_QUERY = "UPDATE users SET email = ?, login = ?, name = ?, birthday = ?" +
            "WHERE id = ?";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM users WHERE id = ?";
    private static final String USER_FRIENDSHIP_QUERY = "SELECT * FROM users WHERE id IN (SELECT user_friend_id FROM user_friends WHERE user_id = ?)";
    private static final String USER_COMMON_FRIENDSHIP_QUERY = "SELECT * FROM users WHERE id IN (SELECT user_friend_id FROM user_friends WHERE user_id = ?) " +
            "AND id IN (SELECT user_friend_id FROM user_friends WHERE user_id = ?)";

    public UserDbStorage(JdbcTemplate jdbc, RowMapper<User> mapper,
                         @Autowired FriendshipDbStorage friendshipDbStorage) {
        super(jdbc, mapper);
        this.friendshipDbStorage = friendshipDbStorage;
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
                user.getBirthday(),
                user.getId()
        );
        return user;
    }

    @Override
    public User get(Long id) {
        User user = findOne(FIND_BY_ID_QUERY, id);
        if (user != null) {
            user.getFriends().addAll(friendshipDbStorage.findAllFriendsIdByUser(user.getId())
                    .stream()
                    .map(Friendship::getUserFriendId)
                    .toList()
            );
        }
        return user;
    }

    @Override
    public void addFriend(Long userId, Long friendId) {
        friendshipDbStorage.addFriendship(userId, friendId);
    }

    @Override
    public void removeFriend(Long userId, Long friendId) {
        friendshipDbStorage.removeFriendship(userId, friendId);
    }

    @Override
    public Collection<User> getUserFriends(Long id) {
        Collection<User> users = findMany(USER_FRIENDSHIP_QUERY, id);
        return loadFriends(users);
//        return friendshipDbStorage.findAllFriendsIdByUser(id).stream()
//                .map(this::get)
//                .toList();
    }

    public Collection<User> getCommonFriends(Long id1, Long id2) {
        Collection<User> users = findMany(
                USER_COMMON_FRIENDSHIP_QUERY,
                id1,
                id2);
        return loadFriends(users);
    }

    private Collection<User> loadFriends(Collection<User> users) {
        if (!users.isEmpty()) {
            Collection<Friendship> friendshipCollection = friendshipDbStorage.getAll();
            for (User user : users) {
                user.getFriends().addAll(friendshipCollection
                        .stream()
                        .filter(friendship -> friendship.getUserId().equals(user.getId()))
                        .map(Friendship::getUserFriendId)
                        .toList()
                );
            }
        }
        return users;
    }
}
