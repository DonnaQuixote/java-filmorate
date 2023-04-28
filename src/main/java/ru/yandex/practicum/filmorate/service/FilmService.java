package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import javax.validation.ValidationException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FilmService {
    private static final LocalDate MIN_DATE = LocalDate.of(1895, 12, 28);
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final DirectorService directorService;

    public Film addFilm(Film film) {
        if (film.getReleaseDate() == null || film.getReleaseDate().isAfter(MIN_DATE)) {
            return filmStorage.addFilm(film);
        } else throw new ValidationException("Некорректная дата выхода");
    }

    public List<Film> getFilms() {
        return filmStorage.getFilmsList();
    }

    public Film updateFilm(Film film) {
        if (filmStorage.isExists(film.getId())) {
            if (film.getReleaseDate() == null || film.getReleaseDate().isAfter(MIN_DATE)) {
                return filmStorage.updateFilm(film);
            } else throw new ValidationException("Некорректная дата выхода");
        } else throw new NoSuchElementException("Фильм с данным id не найден");
    }

    public Film getFilmById(int id) {
        if (filmStorage.isExists(id)) {
            return filmStorage.getById(id);
        } else throw new NoSuchElementException("Фильм не найден");
    }

    public void addLike(int filmId, int userId) {
        if (filmStorage.isExists(filmId) && userStorage.isExists(userId)) {
            filmStorage.addLike(userId, filmId);
        } else throw new NoSuchElementException("Некорректный id пользователя/фильма");
    }

    public void removeLike(int filmId, int userId) {
        if (filmStorage.isExists(filmId) && userStorage.isExists(userId)) {
            filmStorage.removeLike(userId, filmId);
        } else throw new NoSuchElementException("Некорректный id пользователя/фильма");
    }

    public List<Film> getPopular(int count, Integer genreId, int year) {
        List<Film> films = new ArrayList<>(filmStorage.getFilmsList());
        if (count != 1) count--;
        if (count > films.size()) count = films.size();
        films.sort((Comparator.comparingInt(o -> o.getLikes().size())));
        Collections.reverse(films);
        if (genreId != null) {
            films = films.stream()
                    .filter(film -> film.getGenres() != null)
                    .filter(film -> film.getGenres().stream()
                            .map(Genre::getId)
                            .collect(Collectors.toList())
                            .contains(genreId))
                    .collect(Collectors.toList());
        }
        if (year != 0) {
            films = films.stream()
                    .filter(film -> film.getReleaseDate().getYear() == year)
                    .collect(Collectors.toList());
        }
        return films.stream().limit(count).collect(Collectors.toList());
    }

    public List<Film> getFilmsByDirector(int id, String sortBy) {
        directorService.getDirectorById(id);
        sortBy = sortBy.trim().toLowerCase();
        switch (sortBy) {
            case "year":
                return filmStorage.getFilmsByDirectorWithYear(id);
            case "likes":
                return filmStorage.getFilmsByDirectorWithLikes(id);
            default:
                throw new NoSuchElementException("Некорректный параметр запроса");
        }
    }
}