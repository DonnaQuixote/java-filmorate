package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    @PostMapping
    public Review addReview(@RequestBody @Valid Review review) {
        log.debug("Получен запрос POST /reviews.\n" + review.toString());
        return reviewService.addReview(review);
    }

    @PutMapping
    public Review updateReview(@RequestBody @Valid Review review) {
        log.debug("Получен запрос PUT /reviews.\n" + review.toString());
        return reviewService.updateReview(review);
    }

    @DeleteMapping("/{id}")
    public void deleteReview(@PathVariable int id) {
        log.debug(String.format("Получен запрос DELETE /reviews c id %d", id));
        reviewService.deleteReview(id);
    }

    @GetMapping("/{id}")
    public Review getReviewById(@PathVariable int id) {
        log.debug(String.format("Получен запрос GET /reviews c id %d", id));
        return reviewService.getReviewById(id);
    }

    @GetMapping
    public List<Review> getReviewList(@RequestParam(required = false) Integer filmId,
                                      @RequestParam(defaultValue = "10") int count) {
        log.debug("Получен запрос GET /reviews");
        return reviewService.getReviewListForFilm(filmId, count);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable int id, @PathVariable int userId) {
        log.debug(String.format("Получен запрос PUT /reviews/%d/like/%d", id, userId));
        reviewService.addLike(id, userId, true);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable int id, @PathVariable int userId) {
        log.debug(String.format("Получен запрос DELETE /reviews/%d/like/%d", id, userId));
        reviewService.removeLike(id, userId, true);
    }

    @PutMapping("/{id}/dislike/{userId}")
    public void addDislike(@PathVariable int id, @PathVariable int userId) {
        log.debug(String.format("Получен запрос PUT /reviews/%d/dislike/%d", id, userId));
        reviewService.addLike(id, userId, false);
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    public void removeDislike(@PathVariable int id, @PathVariable int userId) {
        log.debug(String.format("Получен запрос DELETE /reviews/%d/dislike/%d", id, userId));
        reviewService.removeLike(id, userId, false);
    }
}