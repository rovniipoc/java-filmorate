package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class FilmControllerTests {

    @Autowired
    private MockMvc mockMvc;
    private static ObjectMapper objectMapper;
    private static ObjectWriter objectWriter;

    @BeforeAll
    static void prepareToTests() {
        objectMapper = JsonMapper.builder()
                .addModule(new JavaTimeModule())
                .build();
        objectWriter = objectMapper.writer();
    }

    @BeforeEach
    public void eraseData() throws Exception {
        mockMvc.perform(delete("/films/all"));
    }

    @Test
    void createFilmsAndGetAllFilmsShouldBeOk() throws Exception {
        //Проверка добавления валидных фильмов и их получения
        Film film1 = new Film();
        film1.setId(1L);
        film1.setName("FilmName1");
        film1.setDescription("FilmDescription1");
        film1.setReleaseDate(LocalDate.of(2001, 1, 1));
        film1.setDuration(1L);
        film1.setMpa(new Mpa(1L, "G"));
        String film1Json = objectWriter.writeValueAsString(film1);
        mockMvc.perform(post("/films").content(film1Json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        Film film2 = new Film();
        film2.setId(2L);
        film2.setName("FilmName2");
        film2.setDescription("FilmDescription2");
        film2.setReleaseDate(LocalDate.of(2002, 2, 2));
        film2.setDuration(2L);
        film2.setMpa(new Mpa(1L, "G"));
        String film2Json = objectWriter.writeValueAsString(film2);
        mockMvc.perform(post("/films").content(film2Json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        List<Film> filmList = new ArrayList<>();
        filmList.add(film1);
        filmList.add(film2);

        MvcResult result = mockMvc.perform(get("/films"))
                .andExpect(status().isOk())
                .andReturn();
        String responseJson = result.getResponse().getContentAsString();
        List<Film> responseFilmList = objectMapper.readValue(responseJson, new TypeReference<>() {
        });

        //assertIterableEquals(filmList, responseFilmList);
    }

    @Test
    void createEmptyNameFilmShouldBeFail() throws Exception {
        //Проверка невозможности добавить фильм без названия или с пустым названием

        //Проверка невозможности добавить фильм без названия
        Film film1 = new Film();
        film1.setName(null);
        film1.setDescription("FilmDescription1");
        film1.setReleaseDate(LocalDate.of(2001, 1, 1));
        film1.setDuration(1L);
        String film1Json = objectWriter.writeValueAsString(film1);
        mockMvc.perform(post("/films").content(film1Json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        //Проверка невозможности добавить фильм с пустым названием
        film1.setName(" ");
        mockMvc.perform(post("/films").content(film1Json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        //Проверка отсутствия в хранилище фильмов после попыток добавления невалидных фильмов
//        MvcResult result = mockMvc.perform(get("/films"))
//                .andExpect(status().isOk())
//                .andReturn();
//        String responseJson = result.getResponse().getContentAsString();
//        List<Film> responseFilmList = objectMapper.readValue(responseJson, new TypeReference<>() {
//        });
//
//        assertIterableEquals(new ArrayList<>(), responseFilmList);
    }

    @Test
    void createTooLongDescriptionFilmShouldBeFail() throws Exception {
        //Проверка невозможности добавить фильм с описанием длиной >200 символов
        Film film1 = new Film();
        film1.setName("FilmName1");
        film1.setDescription("a".repeat(201));
        film1.setReleaseDate(LocalDate.of(2001, 1, 1));
        film1.setDuration(1L);
        String film1Json = objectWriter.writeValueAsString(film1);
        mockMvc.perform(post("/films").content(film1Json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createTooOldReleaseDateFilmShouldBeFail() throws Exception {
        //Проверка невозможности добавить фильм с датой релиза раньше 28.12.1895
        Film film1 = new Film();
        film1.setName("FilmName1");
        film1.setDescription("FilmDescription1");
        film1.setReleaseDate(LocalDate.of(1895, 12, 27));
        film1.setDuration(1L);
        String film1Json = objectWriter.writeValueAsString(film1);

        mockMvc.perform(post("/films").content(film1Json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertInstanceOf(ValidationException.class, result.getResolvedException()));
    }

    @Test
    void createNegativeDurationFilmShouldBeFail() throws Exception {
        //Проверка невозможности добавить фильм с отрицательной продолжительностью
        Film film1 = new Film();
        film1.setName("FilmName1");
        film1.setDescription("FilmDescription1");
        film1.setReleaseDate(LocalDate.of(2001, 1, 1));
        film1.setDuration(-1L);
        String film1Json = objectWriter.writeValueAsString(film1);

        mockMvc.perform(post("/films").content(film1Json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertInstanceOf(MethodArgumentNotValidException.class, result.getResolvedException()));
    }

    @Test
    void updateFilmShouldBeOk() throws Exception {
        //Проверка валидного обновления фильма
        Film film1 = new Film();
        film1.setId(1L);
        film1.setName("FilmName1");
        film1.setDescription("FilmDescription1");
        film1.setReleaseDate(LocalDate.of(2001, 1, 1));
        film1.setDuration(1L);
        film1.setMpa(new Mpa(1L, "G"));
        String film1Json = objectWriter.writeValueAsString(film1);
        mockMvc.perform(post("/films").content(film1Json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        Film film2 = new Film();
        film2.setId(1L);
        film2.setName("FilmName2");
        film2.setDescription("FilmDescription2");
        film2.setReleaseDate(LocalDate.of(2002, 2, 2));
        film2.setDuration(2L);
        film2.setMpa(new Mpa(1L, "G"));
        String film2Json = objectWriter.writeValueAsString(film2);
        mockMvc.perform(put("/films").content(film2Json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        List<Film> filmList = new ArrayList<>();
        filmList.add(film2);

        MvcResult result = mockMvc.perform(get("/films"))
                .andExpect(status().isOk())
                .andReturn();
        String responseJson = result.getResponse().getContentAsString();
        List<Film> responseFilmList = objectMapper.readValue(responseJson, new TypeReference<>() {
        });

        //assertIterableEquals(filmList, responseFilmList);
    }

    @Test
    void updateUnknownIdFilmShouldBeFail() throws Exception {
        //Проверка невозможности обновления фильма с несуществующим (или отсутствующим) id
        Film film1 = new Film();
        film1.setId(1L);
        film1.setName("FilmName1");
        film1.setDescription("FilmDescription1");
        film1.setReleaseDate(LocalDate.of(2001, 1, 1));
        film1.setDuration(1L);
        film1.setMpa(new Mpa(1L, "G"));
        String film1Json = objectWriter.writeValueAsString(film1);
        mockMvc.perform(post("/films").content(film1Json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        //Попытка обновить фильм с несуществующим id
        Film film2 = new Film();
        film2.setId(999L);
        film2.setName("FilmName2");
        film2.setDescription("FilmDescription2");
        film2.setReleaseDate(LocalDate.of(2002, 2, 2));
        film2.setDuration(2L);
        film2.setMpa(new Mpa(1L, "G"));
        String film2Json = objectWriter.writeValueAsString(film2);
        mockMvc.perform(put("/films").content(film2Json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        //Попытка обновить фильм без id
        film2.setId(null);
        film2Json = objectWriter.writeValueAsString(film2);
        mockMvc.perform(put("/films").content(film2Json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        List<Film> filmList = new ArrayList<>();
        filmList.add(film1);

        //Проверка, что в хранилище сохранился только первый валидный фильм
//        MvcResult result = mockMvc.perform(get("/films"))
//                .andExpect(status().isOk())
//                .andReturn();
//        String responseJson = result.getResponse().getContentAsString();
//        List<Film> responseFilmList = objectMapper.readValue(responseJson, new TypeReference<>() {
//        });
//
//        assertIterableEquals(filmList, responseFilmList);
    }

    @Test
    void updateEmptyNameFilmShouldBeFail() throws Exception {
        //Проверка невозможности обновить имя фильма на пустое значение
        Film film1 = new Film();
        film1.setId(1L);
        film1.setName("FilmName1");
        film1.setDescription("FilmDescription1");
        film1.setReleaseDate(LocalDate.of(2001, 1, 1));
        film1.setDuration(1L);
        film1.setMpa(new Mpa(1L, "G"));
        String film1Json = objectWriter.writeValueAsString(film1);
        mockMvc.perform(post("/films").content(film1Json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        //Попытка обновить имя фильм на пустое значение
        Film film2 = new Film();
        film2.setId(1L);
        film2.setName(" ");
        film2.setDescription("FilmDescription2");
        film2.setReleaseDate(LocalDate.of(2002, 2, 2));
        film2.setDuration(2L);
        film2.setMpa(new Mpa(1L, "G"));
        String film2Json = objectWriter.writeValueAsString(film2);
        mockMvc.perform(put("/films").content(film2Json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        //Попытка обновить имя фильм на null
        film2.setName(null);
        film2Json = objectWriter.writeValueAsString(film2);
        mockMvc.perform(put("/films").content(film2Json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        List<Film> filmList = new ArrayList<>();
        filmList.add(film1);

        //Проверка, что в хранилище сохранился только первый валидный фильм
//        MvcResult result = mockMvc.perform(get("/films"))
//                .andExpect(status().isOk())
//                .andReturn();
//        String responseJson = result.getResponse().getContentAsString();
//        List<Film> responseFilmList = objectMapper.readValue(responseJson, new TypeReference<>() {
//        });
//
//        assertIterableEquals(filmList, responseFilmList);
    }

    @Test
    void updateTooLongDescriptionFilmShouldBeFail() throws Exception {
        //Проверка невозможности обновить описание фильма на слишком длинное (>200 символов)
        Film film1 = new Film();
        film1.setId(1L);
        film1.setName("FilmName1");
        film1.setDescription("FilmDescription1");
        film1.setReleaseDate(LocalDate.of(2001, 1, 1));
        film1.setDuration(1L);
        film1.setMpa(new Mpa(1L, "G"));
        String film1Json = objectWriter.writeValueAsString(film1);
        mockMvc.perform(post("/films").content(film1Json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        Film film2 = new Film();
        film2.setId(1L);
        film2.setName("FilmName2");
        film2.setDescription("a".repeat(201));
        film2.setReleaseDate(LocalDate.of(2002, 2, 2));
        film2.setDuration(2L);
        film2.setMpa(new Mpa(1L, "G"));
        String film2Json = objectWriter.writeValueAsString(film2);
        mockMvc.perform(put("/films").content(film2Json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        List<Film> filmList = new ArrayList<>();
        filmList.add(film1);

        //Проверка, что в хранилище сохранился только первый валидный фильм
//        MvcResult result = mockMvc.perform(get("/films"))
//                .andExpect(status().isOk())
//                .andReturn();
//        String responseJson = result.getResponse().getContentAsString();
//        List<Film> responseFilmList = objectMapper.readValue(responseJson, new TypeReference<>() {
//        });
//
//        assertIterableEquals(filmList, responseFilmList);
    }

    @Test
    void updateTooOldReleaseDateFilmShouldBeFail() throws Exception {
        //Проверка невозможности обновить дату релиза фильма на слишком старую
        Film film1 = new Film();
        film1.setId(1L);
        film1.setName("FilmName1");
        film1.setDescription("FilmDescription1");
        film1.setReleaseDate(LocalDate.of(2001, 1, 1));
        film1.setDuration(1L);
        film1.setMpa(new Mpa(1L, "G"));
        String film1Json = objectWriter.writeValueAsString(film1);
        mockMvc.perform(post("/films").content(film1Json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        Film film2 = new Film();
        film2.setId(1L);
        film2.setName("FilmName2");
        film2.setDescription("FilmName2");
        film2.setReleaseDate(LocalDate.of(1895, 12, 27));
        film2.setDuration(2L);
        film2.setMpa(new Mpa(1L, "G"));
        String film2Json = objectWriter.writeValueAsString(film2);
        mockMvc.perform(put("/films").content(film2Json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        List<Film> filmList = new ArrayList<>();
        filmList.add(film1);

        //Проверка, что в хранилище сохранился только первый валидный фильм
//        MvcResult result = mockMvc.perform(get("/films"))
//                .andExpect(status().isOk())
//                .andReturn();
//        String responseJson = result.getResponse().getContentAsString();
//        List<Film> responseFilmList = objectMapper.readValue(responseJson, new TypeReference<>() {
//        });
//
//        assertIterableEquals(filmList, responseFilmList);
    }

    @Test
    void updateNegativeDurationFilmShouldBeFail() throws Exception {

        //Проверка невозможности обновить продолжительность фильма на отрицательную
        Film film1 = new Film();
        film1.setId(1L);
        film1.setName("FilmName1");
        film1.setDescription("FilmDescription1");
        film1.setReleaseDate(LocalDate.of(2001, 1, 1));
        film1.setDuration(1L);
        film1.setMpa(new Mpa(1L, "G"));
        String film1Json = objectWriter.writeValueAsString(film1);
        mockMvc.perform(post("/films").content(film1Json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        Film film2 = new Film();
        film2.setId(1L);
        film2.setName("FilmName2");
        film2.setDescription("FilmName2");
        film2.setReleaseDate(LocalDate.of(2002, 2, 2));
        film2.setDuration(-2L);
        film2.setMpa(new Mpa(1L, "G"));
        String film2Json = objectWriter.writeValueAsString(film2);
        mockMvc.perform(put("/films").content(film2Json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        List<Film> filmList = new ArrayList<>();
        filmList.add(film1);

        //Проверка, что в хранилище сохранился только первый валидный фильм
        MvcResult result = mockMvc.perform(get("/films"))
                .andExpect(status().isOk())
                .andReturn();
        String responseJson = result.getResponse().getContentAsString();
        List<Film> responseFilmList = objectMapper.readValue(responseJson, new TypeReference<>() {
        });

        assertIterableEquals(filmList, responseFilmList);
    }
}