CREATE TABLE IF NOT EXISTS users (
            id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
            email VARCHAR(100) NOT NULL,
            login VARCHAR(100) NOT NULL,
            name VARCHAR(100),
            birthday DATE NOT NULL
          );

CREATE TABLE IF NOT EXISTS user_friends (
            user_id BIGINT REFERENCES users(id) on delete cascade,
            user_friend_id BIGINT REFERENCES users(id) on delete cascade,
            PRIMARY KEY (user_id, user_friend_id)
          );

CREATE TABLE IF NOT EXISTS ratings (
            id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
            mpa_name VARCHAR(100) NOT NULL
          );

CREATE TABLE IF NOT EXISTS films (
            id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
            name VARCHAR(100) NOT NULL,
            description VARCHAR(200),
            releaseDate DATE,
            duration BIGINT,
            rate BIGINT,
            rating_id BIGINT REFERENCES ratings(id)
          );

CREATE TABLE IF NOT EXISTS film_likes (
            film_id BIGINT REFERENCES films(id) on delete cascade,
            user_id BIGINT REFERENCES users(id) on delete cascade,
            PRIMARY KEY (film_id, user_id)
          );

CREATE TABLE IF NOT EXISTS genres (
            id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
            genre_name VARCHAR(100) NOT NULL
          );

CREATE TABLE IF NOT EXISTS film_genres (
            genre_id bigint REFERENCES genres (id) on delete cascade,
            film_id  bigint REFERENCES films (id) on delete cascade,
            PRIMARY KEY (genre_id, film_id)
          );


