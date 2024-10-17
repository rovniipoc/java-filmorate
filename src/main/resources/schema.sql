CREATE TABLE IF NOT EXISTS users (
            id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
            email VARCHAR NOT NULL,
            login VARCHAR NOT NULL,
            name VARCHAR NOT NULL,
            birthday DATE NOT NULL
          );

CREATE TABLE IF NOT EXISTS ratings (
            id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
            name VARCHAR NOT NULL
          );

CREATE TABLE IF NOT EXISTS films (
            id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
            name VARCHAR NOT NULL,
            description VARCHAR NOT NULL,
            releaseDate TIMESTAMP WITH TIME ZONE NOT NULL,
            duration BIGINT,
            rating_id BIGINT NOT NULL REFERENCES ratings(id)
          );

CREATE TABLE IF NOT EXISTS genres (
            id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
            name VARCHAR NOT NULL
          );

CREATE TABLE IF NOT EXISTS film_genres (
            film_id BIGINT REFERENCES films(id),
            genre_id BIGINT REFERENCES genres(id)
          );

CREATE TABLE IF NOT EXISTS film_likes (
            film_id BIGINT REFERENCES films(id),
            user_id BIGINT REFERENCES users(id)
          );

CREATE TABLE IF NOT EXISTS user_friends (
            user_id BIGINT REFERENCES users(id),
            user_friend_id BIGINT REFERENCES users(id),
            friendship_status INT
          );