/* users */
insert into users(user_role, email, password) 
values (1,'admin@admin.com','admin');
insert into users(user_role, email, password) 
values (2,'manu@gmail.com','123'),(2,'jhon@doe.com','123');

/* movie genre */
insert into movie_genre(genre, img_url) values 
('accion',''),
('comedia',''),
('infantil',''),
('drama',''),
('animacion','');

/* movies */
insert into movie(img_url, tittle, release_date, score) values 
('', 'La era de hielo', date('2002-06-05'), 5),
('', 'La era de hielo 2: El deshielo', date('2006-06-05'), 4),
('', 'La era de hielo 3: El origen de los dinosaurios', date('2009-06-05'), 5),

('', 'Cars', date('2006-06-05'), 4),
('', 'Cars 2', date('2011-06-05'), 3),
('', 'Cars 3', date('2017-06-05'), 4),

('', 'Toy story', date('1995-06-05'), 5),
('', 'Toy story 2', date('1999-06-05'), 5),
('', 'Toy story 3', date('2010-06-05'), 4),
('', 'Toy story 4', date('2019-06-05'), 4),

('', 'Belleza Negra', date('2020-06-05'), 4),

('', 'El joven Corcel Negro', date('2003-06-05'), 3),

('', 'Winnie the Pooh', date('2011-06-05'), 5);

/* genre_x_movie */
insert into genre_x_movie (id_movie, id_movie_genre) values
(1, 2),(1, 5),(2, 2),(2, 5),(3, 2),(3, 5),
(4, 2),(4, 5),(5, 2),(5, 5),(6, 1),(6, 2),(6, 5),
(7, 2),(7, 5),(8, 2),(8, 5),(9, 2),(9, 4),(9, 5),(10, 2),(10, 5),
(11, 4),
(12, 4),
(13, 3);

/* characters */
insert into characters(img_url, name, age, weight, story) values
('', 'Manny', 30, 500, 'Es un Mamut lanudo, muy malhumorado'),
('', 'Sid', 15, 30, 'Es un perezoso, muy bueno pero algo torpe'),
('', 'Diego', 30, '50', 'Es un tigre dientes de sable'),
('', 'Ellie', 27, '500', 'Es una Mamut lanuda, criada por zarigüeyas'),

('', 'Rayo McQueen', 20, 1000, 'Es un auto de carreras, que se cree el mejor'),
('', 'Doc Hudson', 60, 1500, 'Es un auto de carreras, viejo y gruñon'),
('', 'Mate', 30, 2000, 'Es una grua muy buena onda'),


('', 'Woody', 20, 2, 'El juguete favorito de Andy es Woody, su amigo fiel'),
('', 'Buzz Lightyear', 20, 4, 'Es un superhéroe de juguete y una figura de acción en la franquicia'),
('', 'Jessie', 22, 2, 'Es una vaquera de juguete basada en la antigua serie El Rodeo de Woody'),

('', 'Black Beauty', 12, 200, 'Es un caballo en la Inglaterra del siglo XIX'),

('', 'Neera', 14, 40, 'Es una intrepida y valiente joven que lo arriesgara todo por su familia'),

('', 'Pooh', 10, 90, 'Es ingenuo y lento, pero también es amigable, pensativo, y determinado.');

/* characters_x_movie */
insert into character_x_movie(id_movie, id_character) values 
(1, 1),(1, 2),(1, 3),
(2, 1),(2, 2),(2, 3),(2, 4),
(3, 1),(3, 2),(3, 3),(3, 4),
(4, 5),(4, 6),(4, 7),
(5, 5),(5, 6),(5, 7),
(6, 5),(6, 6),
(7, 8),(7, 9),
(8, 8),(8, 9),(8, 10),
(9, 8),(9, 9),(9, 10),
(10, 8),(10, 9),(10, 10),
(11, 11),
(12, 12),
(13, 13);