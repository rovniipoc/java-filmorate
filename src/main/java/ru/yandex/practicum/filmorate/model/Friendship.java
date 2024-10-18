package ru.yandex.practicum.filmorate.model;

import lombok.Data;

@Data
public class Friendship {
    private Long firstUserId;
    private Long secondUserId;
}
