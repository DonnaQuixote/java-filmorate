package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {

    User addUser(User user);

    User updateUser(User user);

    void deleteUser(int id);

    User getById(int id);

    List<User> getUsersList();

    void addFriend(int first, int second);

    void deleteFriend(int first, int second);

    boolean isExists(int id);
}