package ru.yandex.practicum.filmorate.storage;

import lombok.AllArgsConstructor;
import org.hibernate.cfg.NotYetImplementedException;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

@Component
@Primary
@AllArgsConstructor
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public User addUser(User user) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users").usingGeneratedKeyColumns("user_id");
        user.setId(simpleJdbcInsert.executeAndReturnKey(user.toMap()).intValue());
        return user;
    }

    @Override
    public User updateUser(User user) {
        String sql = "UPDATE users SET email = ?, login = ?, name = ?, birthday = ? WHERE user_id = ?";
        jdbcTemplate
                .update(sql, user.getEmail(), user.getLogin(), user.getName(), user.getBirthday(), user.getId());
        return user;
    }

    @Override
    public User getById(int id) {
        String sql = "SELECT * FROM users WHERE user_id = ?";
        return jdbcTemplate.queryForObject(sql, this::createUser, id);
    }

    @Override
    public void addFriend(int firstId, int secondId) {
        String sql = "INSERT INTO friends VALUES (?, ?)";
        jdbcTemplate.update(sql, secondId, firstId);
    }

    @Override
    public void deleteFriend(int firstId, int secondId) {
        String sql = "DELETE FROM friends WHERE user_id = ? AND friend_id = ?";
        jdbcTemplate.update(sql, secondId, firstId);
    }

    @Override
    public List<User> getUsersList() {
        String sql = "SELECT * FROM users";
        return jdbcTemplate.query(sql, this::createUser);
    }

    @Override
    public void deleteUser(int id) {
        throw new NotYetImplementedException("Не поддерживается");
    }

    @Override
    public boolean isExists(int id) {
        String sql = "SELECT * FROM users WHERE user_id = ?";
        SqlRowSet rows = jdbcTemplate.queryForRowSet(sql, id);
        return rows.next();
    }

    private User createUser(ResultSet resultSet, int rowNum) throws SQLException {
        User user = new User();
        user.setId(resultSet.getInt("user_id"));
        user.setEmail(resultSet.getString("email"));
        user.setLogin(resultSet.getString("login"));
        user.setName(resultSet.getString("name"));
        user.setBirthday(Objects.requireNonNull(resultSet.getDate("birthday")).toLocalDate());
        getFriends(user.getId()).forEach((friend) -> user.getFriends().add(friend.getId()));
        return user;
    }

    private List<User> getFriends(int id) {
        String sql = "SELECT * FROM users JOIN friends ON users.user_id=friends.user_id WHERE friends.friend_id = ?";
        return jdbcTemplate.query(sql, this::createUser, id);
    }
}