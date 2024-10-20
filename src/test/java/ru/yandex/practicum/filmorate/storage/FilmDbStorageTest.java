package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.mapper.FilmRowMapper;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@JdbcTest
@Import({FilmDbStorage.class, FilmRowMapper.class})
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FilmDbStorageTest {

    private final FilmDbStorage filmDbStorage;
    private final List<Film> filmList = new ArrayList<>();

    @BeforeEach
    public void addTestFilms() {
        filmList.clear();

        Film film1 = new Film();
        film1.setName("testname1");
        film1.setDescription("test1sdfmailsdfcom");
        film1.setDuration(10L);
        film1.setReleaseDate(LocalDate.of(2001, 1, 1));
        film1 = filmDbStorage.add(film1);
        filmList.add(film1);

        Film film2 = new Film();
        film2.setName("testname2");
        film2.setDescription("test2sdfmailsdfcom");
        film2.setDuration(20L);
        film2.setReleaseDate(LocalDate.of(2002, 2, 2));
        film2 = filmDbStorage.add(film2);
        filmList.add(film2);

    }

}
