package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;

import java.util.Collection;

@RestController
@RequiredArgsConstructor
@RequestMapping("/genres")
@Slf4j
public class GenreController {

    private final GenreService genreService;

    @GetMapping
    public Collection<Genre> findAll() {
        log.info("Пришел запрос Get /genres");
        Collection<Genre> response = genreService.findAll();
        log.info("Отправлен ответ Get /genres с телом: {}", response);
        return response;
    }

    @GetMapping("/{id}")
    public Genre findById(@PathVariable Long id) {
        log.info("Пришел запрос Get /genres/{}", id);
        Genre response = genreService.getGenreById(id);
        log.info("Отправлен ответ Get /genres/{} с телом: {}", id, response);
        return response;
    }

}
