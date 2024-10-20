package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

@Builder(toBuilder = true)
@Data
public class Genre {
    private Long id = 0L;
    private String name;
}
