package ru.yandex.practicum.filmorate.storage;

import lombok.AllArgsConstructor;
import org.hibernate.cfg.NotYetImplementedException;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Component
@Primary
@AllArgsConstructor
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final MpaStorage mpaStorage;
    private final DirectorStorage directorStorage;

    @Override
    public Film addFilm(Film film) {
        if (film.getMpa() != null) film.setMpa(mpaStorage.getById(film.getMpa().getId()));
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("films")
                .usingGeneratedKeyColumns("film_id");
        film.setId(simpleJdbcInsert.executeAndReturnKey(film.toMap()).intValue());
        if (!film.getGenres().isEmpty()) {
            film.getGenres().forEach(genre -> jdbcTemplate.update("INSERT INTO films_genres VALUES (?,?)",
                    film.getId(), genre.getId()));
            }
        insertDirectors(film);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        if (film.getMpa() != null) film.setMpa(mpaStorage.getById(film.getMpa().getId()));
        String sql = "UPDATE films SET film_id = ?, name = ?, description = ?," +
                " release_date = ?, duration = ?, mpa_id = ? WHERE film_id = ?";
        jdbcTemplate.update(sql, film.getId(), film.getName(), film.getDescription(),
                film.getReleaseDate(), film.getDuration(),
                film.getMpa().getId(), film.getId());
        jdbcTemplate.update("DELETE FROM films_genres WHERE film_id = ?", film.getId());
        if (!film.getGenres().isEmpty()) {
            film.getGenres().forEach((genre) -> jdbcTemplate.update("INSERT INTO films_genres VALUES (?, ?)",
                    film.getId(), genre.getId()));
            }
        jdbcTemplate.update("DELETE FROM films_directors WHERE film_id = ?", film.getId());
        insertDirectors(film);
        return film;
    }

    @Override
    public List<Film> getFilmsList() {
        String sql = "SELECT * FROM films";
        return jdbcTemplate.query(sql, this::createFilm);
    }

    @Override
    public Film getById(int id) {
        String sql = "SELECT * FROM films WHERE film_id = ?";
        return jdbcTemplate.queryForObject(sql, this::createFilm, id);
    }

    @Override
    public void addLike(int userId, int filmId) {
        String sql = "INSERT INTO likes VALUES (?, ?)";
        jdbcTemplate.update(sql, userId, filmId);
    }

    @Override
    public void removeLike(int userId, int filmId) {
        String sql = "DELETE FROM likes WHERE user_id = ? AND film_id = ?";
        jdbcTemplate.update(sql, userId, filmId);
    }

    @Override
    public void deleteFilm(int id) {
        throw new NotYetImplementedException("Не поддерживается");
    }

    @Override
    public List<Film> getFilmsByDirectorWithYear(int id) {
        String sql = "SELECT films.*" +
                "FROM films " +
                "INNER JOIN films_directors AS fd on films.film_id = fd.film_id " +
                "WHERE fd.director_id = ? GROUP BY films.film_id ORDER BY release_date";
        return new ArrayList<>(jdbcTemplate.query(sql, this::createFilm, id));
    }

    @Override
    public List<Film> getFilmsByDirectorWithLikes(int id) {
        String sql = "SELECT films.*, COUNT(likes.user_id) AS rate " +
                "FROM films " +
                "LEFT JOIN likes ON films.film_id = likes.film_id " +
                "INNER JOIN films_directors AS fd on films.film_id = fd.film_id " +
                "WHERE fd.director_id = ? GROUP BY films.film_id ORDER BY rate";
        return new ArrayList<>(jdbcTemplate.query(sql, this::createFilm, id));
    }

    @Override
    public boolean isExists(int id) {
        String sql = "SELECT * FROM films WHERE film_id = ?";
        SqlRowSet rows = jdbcTemplate.queryForRowSet(sql, id);
        return rows.next();
    }

    private Film createFilm(ResultSet resultSet, int row) throws SQLException {
        Film film = new Film();
        film.setId(resultSet.getInt("film_id"));
        film.setName(resultSet.getString("name"));
        film.setDescription(resultSet.getString("description"));
        film.setDirectors(directorStorage.getDirectorsByFilmId(film.getId()));
        if (resultSet.getDate("release_date") != null)
            film.setReleaseDate(resultSet.getDate("release_date").toLocalDate());
        film.setDuration(resultSet.getInt("duration"));
        if (resultSet.getInt("mpa_id") != 0)
            film.setMpa(mpaStorage.getById(resultSet.getInt("mpa_id")));
        String sqlGenres =
                "SELECT films_genres.genre_id, genre FROM films_genres " +
                        "JOIN genres on films_genres.genre_id = genres.genre_id WHERE film_id = ?" +
                        " ORDER BY films_genres.genre_id";
        List<Genre> genres = jdbcTemplate.query(sqlGenres, (rs, rowNum) ->
                new Genre(rs.getInt("genre_id"), rs.getString("genre")), film.getId());
        if (!genres.isEmpty()) film.setGenres(new TreeSet<>(genres));
        String sqlLikes = "SELECT user_id FROM likes WHERE film_id = ?";
        film.getLikes().addAll(jdbcTemplate.query(sqlLikes,
                (rs, rowNum) -> rs.getInt("user_id"), film.getId()));
        return film;
    }

    private void insertDirectors(Film film) {
        if (!film.getDirectors().isEmpty()) {
            film.getDirectors().forEach(director -> {
                if (directorStorage.getById(director.getId()) != null) {
                    jdbcTemplate.update("INSERT INTO films_directors VALUES (?, ?)",
                            film.getId(), director.getId());
                } else throw new NoSuchElementException("Режиссёр не найден");
            });
        }
    }
}