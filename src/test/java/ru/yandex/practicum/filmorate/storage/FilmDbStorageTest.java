package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.mapper.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@JdbcTest
@Import({FilmDbStorage.class, FilmRowMapper.class,
        LikeDbStorage.class, LikeRowMapper.class,
        MpaDbStorage.class, MpaRowMapper.class,
        GenreDbStorage.class, GenreRowMapper.class,
        UserDbStorage.class, UserRowMapper.class,
        FriendshipDbStorage.class, FriendshipRowMapper.class,
        FilmGenreDbStorage.class, FilmGenreMapper.class})
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
        film1.setMpa(Mpa.builder().id(1L).build());
        film1 = filmDbStorage.add(film1);
        film1 = filmDbStorage.get(film1.getId());
        filmList.add(film1);

        Film film2 = new Film();
        film2.setName("testname2");
        film2.setDescription("test2sdfmailsdfcom");
        film2.setDuration(20L);
        film2.setReleaseDate(LocalDate.of(2002, 2, 2));
        film2.setMpa(Mpa.builder().id(2L).build());
        film2 = filmDbStorage.add(film2);
        film2 = filmDbStorage.get(film2.getId());
        filmList.add(film2);
    }

    @Test
    void findFilmByIdTest() {

        Film film1 = filmList.get(0);
        Film film2 = filmList.get(1);

        assertEquals(film1.getName(), filmDbStorage.get(film1.getId()).getName());
        assertEquals(film2.getName(), filmDbStorage.get(film2.getId()).getName());
    }

    @Test
    void findUnknownFilmByIdTest() {

        Film film3 = filmDbStorage.get(999L);
        assertNull(film3);
    }

    @Test
    void getAllFilmsShouldBeOk() {

        assertEquals(filmList, filmDbStorage.findAll());
    }


}
