package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;

@Data
public class Review {
    private int reviewId;
    @NotNull(message = "Невозможно опубликовать без текста отзыва")
    @NotBlank(message = "Отзыв не может быть пустым")
    private String content;
    @JsonProperty("isPositive")
    @NotNull(message = "Необходимо указать, является ли отзыв положительным")
    private Boolean isPositive;
    @NotNull(message = "Необходим Id пользователя")
    private Integer userId;
    @NotNull(message = "Необходим Id фильма")
    private Integer filmId;
    private int useful;

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("review_id", reviewId);
        map.put("content", content);
        map.put("is_positive", isPositive);
        map.put("user_id", userId);
        map.put("film_id", filmId);
        map.put("useful", useful);
        return map;
    }
}