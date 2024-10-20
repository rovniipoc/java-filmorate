package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaDbStorage;

import java.util.Collection;

@Slf4j
@Service
@RequiredArgsConstructor
public class MpaService {

    private final MpaDbStorage mpaDbStorage;

    public Mpa get(Long id) {
        Mpa mpa = mpaDbStorage.getMpa(id);
        if (mpa == null) {
            throw new NotFoundException("Рейтинг с заданным id не найден");
        }
        return mpa;
    }

    public Collection<Mpa> findAll() {
        return mpaDbStorage.findAllMpas();
    }

    public void deleteAll() {
        mpaDbStorage.removeAll();
    }
}
