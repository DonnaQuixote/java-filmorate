package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

public interface ReviewStorage {
    Review addReview(Review review);

    Review updateReview(Review review);

    Review getById(int id);

    void deleteReview(int id);

    List<Review> getByFilm(int filmId, int count);

    List<Review> getWithCount(int count);

    void addLike(int id, int userId, boolean isPositive);

    void removeLike(int id, int userId, boolean isPositive);

    boolean isExists(int id);
}