package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Mpa {
    private Long id;
    private String name;

//    G - у фильма нет возрастных ограничений
//    PG - детям рекомендуется смотреть фильм с родителями
//    PG13 - детям до 13 лет просмотр не желателен
//    R - лицам до 17 лет просматривать фильм можно только в присутствии взрослого
//    NC17 - лицам до 18 лет просмотр запрещён
}
