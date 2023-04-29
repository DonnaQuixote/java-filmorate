package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class DirectorService {
    private final DirectorStorage directorStorage;

    public Director addDirector(Director director) {
        return directorStorage.addDirector(director);
    }

    public Director updateDirector(Director director) {
        if (directorStorage.getById(director.getId()) != null) {
            return directorStorage.updateDirector(director);
        } else throw new NoSuchElementException("Режиссёр не найден");
    }

    public void deleteDirector(int id) {
        directorStorage.deleteDirector(id);
    }

    public Director getDirectorById(int id) {
        if (directorStorage.getById(id) != null) {
            return directorStorage.getById(id);
        } else throw new NoSuchElementException("Режиссёр не найден");
    }

    public List<Director> getDirectorList() {
        return directorStorage.getDirectorList();
    }
}