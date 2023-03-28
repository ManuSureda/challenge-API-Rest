drop DATABASE disneyjava;

CREATE DATABASE disneyjava;

USE disneyjava;

CREATE TABLE users(
id_user integer auto_increment,
user_role enum('admin', 'client'),
email varchar(40) unique,
password varchar(30),

constraint pk_user primary key (id_user)
);

CREATE TABLE characters(
id_character integer auto_increment,
img_url varchar(250),
name varchar(60) unique,
age integer,
weight integer,
story varchar(250),

constraint pk_characters primary key (id_character)
);

CREATE TABLE movie_genre(
id_movie_genre integer auto_increment,
genre varchar(60) unique,
img_url varchar(250),

constraint pk_movie_genre primary key (id_movie_genre)
);

CREATE TABLE movie(
id_movie integer auto_increment,
img_url varchar(250) not null,
tittle varchar(60) not null unique,
release_date DATE not null,
score integer,

constraint pk_movie primary key (id_movie)
);

CREATE TABLE genre_x_movie(
id_genre_x_movie integer auto_increment,
id_movie integer not null,
id_movie_genre integer not null,

constraint pk_genre_x_movie primary key (id_genre_x_movie),
constraint fk_movie foreign key (id_movie) references movie(id_movie),
constraint fk_movie_genre foreign key (id_movie_genre) references movie_genre (id_movie_genre),
unique key genre_x_movie_unique (id_movie, id_movie_genre)
);

CREATE TABLE character_x_movie(
id_character_by_movie integer auto_increment,
id_movie integer not null,
id_character integer not null,

constraint pk_character_by_movie primary key (id_character_by_movie),
constraint fk_movie_x_c foreign key (id_movie) references movie(id_movie) on delete cascade,
constraint fk_character foreign key (id_character) references characters(id_character) on delete cascade,
unique key character_x_movie_unique (id_movie, id_character)
);