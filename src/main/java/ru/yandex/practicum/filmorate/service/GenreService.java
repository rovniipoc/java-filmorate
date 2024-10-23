package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreDbStorage;

import java.util.Collection;

@Slf4j
@Service
@RequiredArgsConstructor
public class GenreService {

    private final GenreDbStorage genreDbStorage;

    public Genre getGenreById(Long id) {
        Genre genre = genreDbStorage.get(id);
        if (genre == null) {
            throw new NotFoundException("Жанр с указанным id не найден");
        }
        return genre;
    }

    public Collection<Genre> findAll() {
        return genreDbStorage.getAll();
    }

}
