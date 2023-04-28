package ru.yandex.practicum.filmorate.storage;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Director;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

@Component
@AllArgsConstructor
public class DirectorDbStorage implements DirectorStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Director addDirector(Director director) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("directors")
                .usingGeneratedKeyColumns("director_id");
        director.setId(simpleJdbcInsert.executeAndReturnKey(director.toMap()).intValue());
        return director;
    }

    @Override
    public Director updateDirector(Director director) {
        String sql = "UPDATE directors SET director_name = ? WHERE director_id = ?";
        jdbcTemplate.update(sql, director.getName(), director.getId());
        return director;
    }

    @Override
    public void deleteDirector(int id) {
        String sql = "DELETE FROM directors WHERE director_id = ?";
        jdbcTemplate.update(sql, id);
    }

    @Override
    public List<Director> getDirectorList() {
        String sql = "SELECT * FROM directors";
        return jdbcTemplate.query(sql, this::createDirector);
    }

    @Override
    public Director getById(int id) {
        if (isExists(id)) {
            String sql = "SELECT * FROM directors WHERE director_id = ?";
            return jdbcTemplate.queryForObject(sql, this::createDirector, id);
        } else return null;
    }

    @Override
    public Set<Director> getDirectorsByFilmId(int id) {
        String sql = "SELECT directors.* FROM films_directors " +
                "JOIN directors ON films_directors.director_id = directors.director_id " +
                "WHERE film_id = ?";
        return new TreeSet<>(jdbcTemplate.query(sql, this::createDirector, id));
    }

    private Director createDirector(ResultSet resultSet, int rowNum) throws SQLException {
        return new Director(resultSet.getInt("director_id"),
                resultSet.getString("director_name"));
    }

    private boolean isExists(int id) {
        String sql = "SELECT * FROM directors WHERE director_id = ?";
        SqlRowSet rows = jdbcTemplate.queryForRowSet(sql, id);
        return rows.next();
    }
}