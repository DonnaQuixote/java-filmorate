package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor
public class Director implements Comparable<Director> {
    private Integer id;
    @NotBlank(message = "Имя режиссёра не может быть пустым")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String name;

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("director_name", name);
        return map;
    }

    @Override
    public int compareTo(Director o) {
        return id.compareTo(o.id);
    }
}