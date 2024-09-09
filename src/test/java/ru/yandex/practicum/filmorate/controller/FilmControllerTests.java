package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.gson.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.lang.reflect.Type;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static net.bytebuddy.matcher.ElementMatchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
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

    @Test
    void createFilmsAndGetAllFilmsShouldBeOk() throws Exception {
        //Проверка добавления валидных фильмов и их получения
        Film film1 = new Film();
        film1.setId(1L);
        film1.setName("FilmName1");
        film1.setDescription("FilmDescription1");
        film1.setReleaseDate(LocalDate.of(2001, 1, 1));
        film1.setDuration(1L);
        String film1Json = objectWriter.writeValueAsString(film1);
        mockMvc.perform(post("/films").content(film1Json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        Film film2 = new Film();
        film2.setId(2L);
        film2.setName("FilmName2");
        film2.setDescription("FilmDescription2");
        film2.setReleaseDate(LocalDate.of(2002, 2, 2));
        film2.setDuration(2L);
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
        List<Film> responseFilmList = objectMapper.readValue(responseJson, new TypeReference<>(){});

        assertIterableEquals(filmList, responseFilmList);
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
        MvcResult result = mockMvc.perform(get("/films"))
                .andExpect(status().isOk())
                .andReturn();
        String responseJson = result.getResponse().getContentAsString();
        List<Film> responseFilmList = objectMapper.readValue(responseJson, new TypeReference<>(){});

        assertIterableEquals(new ArrayList<>(), responseFilmList);
    }

    @Test
    void tooLongDescriptionFilmShouldBeFail() throws Exception {
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
    void tooOldReleaseDateFilmShouldBeFail() throws Exception {
        //Проверка невозможности добавить фильм с датой релиза раньше 28.12.1895
        Film film1 = new Film();
        film1.setName("FilmName1");
        film1.setDescription("FilmDescription1");
        film1.setReleaseDate(LocalDate.of(1895, 12, 27));
        film1.setDuration(1L);
        String film1Json = objectWriter.writeValueAsString(film1);

        assertThrows(ServletException.class, () -> mockMvc.perform(post("/films").content(film1Json).contentType(MediaType.APPLICATION_JSON)));
        //Так и не понял как правильно отловить исключение ValidationException...
    }

}