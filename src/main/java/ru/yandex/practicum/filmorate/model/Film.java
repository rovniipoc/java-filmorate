package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
public class Film {
    Long id;
    @NotNull
    @NotBlank
    String name;
    @Size(max = 200)
    String description;
    LocalDate releaseDate;
    Long duration;

}
