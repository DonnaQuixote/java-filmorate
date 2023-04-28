package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.*;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewStorage reviewStorage;
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;

    public Review addReview(Review review) {
        if (userStorage.isExists(review.getUserId())
                && filmStorage.isExists(review.getFilmId())) {
            return reviewStorage.addReview(review);
        } else throw new NoSuchElementException("Пользователь/Фильм не найдены");
    }

    public Review updateReview(Review review) {
        if (reviewStorage.isExists(review.getReviewId())) {
            return reviewStorage.updateReview(review);
        } else throw new NoSuchElementException("Отзыв не найден");
    }

    public Review getReviewById(int id) {
        if (reviewStorage.isExists(id)) {
            return reviewStorage.getById(id);
        } else throw new NoSuchElementException("Отзыв не найден");
    }

    public void deleteReview(int id) {
        reviewStorage.deleteReview(id);
    }

    public List<Review> getReviewListForFilm(Integer filmId, int count) {
        if (filmId == null) return getReviewList(count);
        if (filmStorage.isExists(filmId)) {
           return reviewStorage.getByFilm(filmId, count);
        } else throw new NoSuchElementException("Фильм не найден");
    }

    public List<Review> getReviewList(int count) {
        return reviewStorage.getWithCount(count);
    }

    public void addLike(int id, int userId, boolean isPositive) {
        if (reviewStorage.isExists(id) && userStorage.isExists(userId)) {
            reviewStorage.addLike(id, userId, isPositive);
        } else throw new NoSuchElementException("Отзыв/Пользователь не найдены");
    }

    public void removeLike(int id, int userId, boolean isPositive) {
        reviewStorage.removeLike(id, userId, isPositive);
    }
}