package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {
    private final FilmService filmService;

    @PostMapping
    public Film addFilm(@RequestBody @Valid Film film) {
        log.debug("Получен запрос POST /films.\n" + film.toString());
        return filmService.addFilm(film);
    }

    @GetMapping
    public List<Film> getFilms() {
        log.debug("Получен запрос GET /films.");
        return filmService.getFilms();
    }

    @PutMapping
    public Film updateFilm(@RequestBody @Valid Film film) {
        log.debug("Получен запрос PUT /films.\n" + film.toString());
        return filmService.updateFilm(film);
    }

    @GetMapping("/{filmId}")
    public Film getFilmById(@PathVariable int filmId) {
        log.debug(String.format("Получен запрос GET films/%d", filmId));
        return filmService.getFilmById(filmId);
    }

    @SuppressWarnings("checkstyle:DesignForExtension")
    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable int id, @PathVariable int userId) {
        log.debug(String.format("Получен запрос PUT films/%d/like/%d", id, userId));
        filmService.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable int id, @PathVariable int userId) {
        log.debug(String.format("Получен запрос DELETE films/%d/like/%d", id, userId));
        filmService.removeLike(id, userId);
    }

    @GetMapping("/popular")
    public List<Film> getPopular(@RequestParam(defaultValue = "10") int count,
                                 @RequestParam(required = false) Integer genreId,
                                 @RequestParam(defaultValue = "0") int year) {
        log.debug(String.format("Получен запрос GET films/popular на %d фильмов", count));
        return filmService.getPopular(count, genreId, year);
    }

    @GetMapping("/director/{directorId}")
    public List<Film> getFilmsByDirector(@PathVariable int directorId, @RequestParam String sortBy) {
        log.debug(String.format("Получен запрос GET /films/directors/%d", directorId));
        return filmService.getFilmsByDirector(directorId, sortBy);
    }
}