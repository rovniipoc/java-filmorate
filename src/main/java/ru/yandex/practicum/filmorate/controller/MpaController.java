package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.util.Collection;

@RestController
@RequiredArgsConstructor
@RequestMapping("/mpa")
@Slf4j
public class MpaController {

    private final MpaService mpaService;

    @GetMapping
    public Collection<Mpa> findAll() {
        log.info("Пришел запрос Get /mpa");
        Collection<Mpa> response = mpaService.findAll();
        log.info("Отправлен ответ Get /mpa с телом: {}", response);
        return response;
    }

    @GetMapping("/{id}")
    public Mpa getMpaById(@PathVariable Long id) {
        log.info("Пришел запрос Get /mpa/{}", id);
        Mpa response = mpaService.get(id);
        log.info("Отправлен ответ Get /mpa/{} с телом: {}", id, response);
        return response;
    }

}
