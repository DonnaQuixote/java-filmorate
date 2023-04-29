package ru.yandex.practicum.filmorate.storage;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Review;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
@AllArgsConstructor
public class ReviewDbStorage implements ReviewStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Review addReview(Review review) {
        review.setUseful(0);
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("reviews")
                .usingGeneratedKeyColumns("review_id");
        int id = simpleJdbcInsert.executeAndReturnKey(review.toMap()).intValue();
        review.setReviewId(id);
        return review;
    }

    @Override
    public Review updateReview(Review review) {
        String sql = "UPDATE reviews SET content = ?, is_positive = ? WHERE review_id = ?";
        jdbcTemplate.update(sql, review.getContent(), review.getIsPositive(), review.getReviewId());
        return getById(review.getReviewId());
    }

    @Override
    public Review getById(int id) {
        String sql = "SELECT * FROM reviews WHERE review_id = ?";
        return jdbcTemplate.queryForObject(sql, this::createReview, id);
    }

    @Override
    public void deleteReview(int id) {
        String sql = "DELETE FROM reviews WHERE review_id = ?";
        jdbcTemplate.update(sql, id);
    }

    @Override
    public List<Review> getByFilm(int filmId, int count) {
        String sql = "SELECT * FROM reviews WHERE film_id = ? ORDER BY useful DESC LIMIT ?";
        return jdbcTemplate.query(sql, this::createReview, filmId, count);
    }

    @Override
    public List<Review> getWithCount(int count) {
        String sql = "SELECT * FROM reviews ORDER BY useful DESC LIMIT ?";
        return jdbcTemplate.query(sql, this::createReview, count);
    }

    @Override
    public void addLike(int id, int userId, boolean isPositive) {
        String sql = "MERGE INTO review_ratings (review_id, user_id, is_positive) " +
                "KEY (review_id, user_id) VALUES (?,?,?)";
        jdbcTemplate.update(sql, id, userId, isPositive);
        updateUseful(id, isPositive);
    }

    @Override
    public void removeLike(int id, int userId, boolean isPositive) {
        String sql = "DELETE FROM review_ratings WHERE review_id = ? AND user_id = ? AND is_positive = ?";
        jdbcTemplate.update(sql, id, userId, isPositive);
        isPositive = !isPositive;
        updateUseful(id, isPositive);
    }

    @Override
    public boolean isExists(int id) {
        String sql = "SELECT * FROM reviews WHERE review_id = ?";
        SqlRowSet rows = jdbcTemplate.queryForRowSet(sql, id);
        return rows.next();
    }

    private Review createReview(ResultSet resultSet, int rowNum) throws SQLException {
        Review review = new Review();
        review.setReviewId(resultSet.getInt("review_id"));
        review.setContent(resultSet.getString("content"));
        review.setIsPositive(resultSet.getBoolean("is_positive"));
        review.setUserId(resultSet.getInt("user_id"));
        review.setFilmId(resultSet.getInt("film_id"));
        review.setUseful(resultSet.getInt("useful"));
        return review;
    }

    private void updateUseful(int id, boolean isPositive) {
        String sql;
        if (isPositive) {
            sql = "UPDATE reviews SET useful = useful + 1 WHERE review_id = ?";
        } else {
            sql = "UPDATE reviews SET useful = useful - 1 WHERE review_id = ?";
        }
        jdbcTemplate.update(sql, id);
    }
}