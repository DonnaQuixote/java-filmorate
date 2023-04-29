package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {

    Film addFilm(Film film);

    Film updateFilm(Film film);

    void deleteFilm(int id);

    List<Film> getFilmsList();

    Film getById(int id);

    void addLike(int userId, int filmId);

    void removeLike(int userId, int filmId);

    List<Film> getFilmsByDirectorWithYear(int id);

    List<Film> getFilmsByDirectorWithLikes(int id);

    boolean isExists(int id);
}