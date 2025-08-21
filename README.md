# Java-filmorate
Filmorate is Yandex educational project

## Database scheme
![Database scheme](/src/main/resources/database-description/QuickDBD-export.png)

### Database description
some description
```
Users as u
-
user_id PK bigint
email varchar
login varchar
name varchar
birthday timestamp

Films as f
-
film_id PK bigint
name varchar
description varchar
releaseDate timestamp
duration bigint
rating_id bigint

Genres as g
-
genre_id PK bigint
name varchar

Ratings as r
-
rating_id PK bigint FK >- Films.rating_id
name varchar

Film_genres as fg
-
film_id bigint FK >- Films.film_id
genre_id bigint FK >- Genres.genre_id

Film_likes as fl
-
film_id bigint FK >- Films.film_id
user_id bigint FK >- Users.user_id

User_friends as uf
-
user_id bigint FK >- Users.user_id
user_friend_id bigint FK >- Users.user_id
```

### SQL requests examples
> [!NOTE]
> The symbols [] indicate user-entered parameters.

Get all users
```sql
SELECT *
FROM Users
```

Get all films
```sql
SELECT *
FROM Films
```

Get user by id
```sql
SELECT *
FROM Users
WHERE user_id = [id]
```

Get film by id
```sql
SELECT *
FROM Films
WHERE film_id = [id]
```

Get popular films
```sql
SELECT *
FROM Films AS f
JOIN Film_likes AS fl ON f.film_id = fl.film_id
ORDER BY COUNT(fl.user_id) DESC
LIMIT [top_count]
```

Get user's friends
```sql
SELECT *
FROM User_friends AS uf
JOIN Users AS u ON uf.user_friend_id = u.user_id
WHERE uf.user_id = [id] AND status = 2
```
> [!NOTE]
> Status may be:
> "0" - not a friend;
> "1" - unconfirmed friend;
> "2" - confirmed friend.
