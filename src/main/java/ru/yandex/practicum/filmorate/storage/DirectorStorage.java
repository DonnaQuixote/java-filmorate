package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;
import java.util.Set;

public interface DirectorStorage {

    Director addDirector(Director director);

    Director updateDirector(Director director);

    void deleteDirector(int id);

    List<Director> getDirectorList();

    Director getById(int id);

    Set<Director> getDirectorsByFilmId(int id);
}