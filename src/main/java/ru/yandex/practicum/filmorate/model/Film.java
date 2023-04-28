package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.LocalDate;
import java.util.*;

@Data
public class Film {
    private int id;
    @NotNull(message = "Нет названия фильма")
    @NotBlank(message = "Название фильма не может быть пустым")
    private String name;
    @Length(max = 200, message = "Превышен лимит описания")
    private String description;
    private LocalDate releaseDate;
    @Positive(message = "Продолжительность не может быть отрицательной")
    private int duration;
    private Set<Integer> likes = new TreeSet<>();
    private Set<Genre> genres = new TreeSet<>();
    private Mpa mpa;
    private Set<Director> directors = new TreeSet<>();

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("name", name);
        map.put("description", description);
        map.put("release_date", releaseDate);
        map.put("duration", duration);
        if (mpa != null) map.put("mpa_id", mpa.getId());
        return map;
    }
}