use disneyjava;

DELIMITER $$
CREATE PROCEDURE sp_register_user(pUser_role integer, pEmail varchar(250), pPassword varchar(250), out idUser integer)
BEGIN
	DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
		signal sqlstate '45000' SET MESSAGE_TEXT = 'Incorrect user data', MYSQL_ERRNO = 139;
    END; 
    
    insert into users(user_role, email, password) values
    (pUser_role, pEmail, pPassword);
    set idUser = last_insert_id();
END;
$$
DELIMITER ;


/* 3. Listado de Personajes 
El listado deberá mostrar:
•	Imagen
•	Nombre  */
create view v_characters
as
select 
	c.img_url, c.name
from 
	characters c;

/*select * from v_characters;*/


/* 4. Creacion, Edicion y Eliminacion de Personajes (CRUD) */
/* CREATE */ 
DELIMITER $$
CREATE PROCEDURE sp_create_character(pImgUrl varchar(250), pName varchar(60), pAge integer, pWeight integer, pStory varchar(250), out idCharacter int)
BEGIN
	DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
		signal sqlstate '45000' SET MESSAGE_TEXT = 'Character name, allready exist', MYSQL_ERRNO = 136;
    END; 
    
    insert into characters(img_url, name, age, weight, story) values
    (pImgUrl, pName, pAge, pWeight, pStory);
    set idCharacter = last_insert_id();
END;
$$
DELIMITER ;

DELIMITER $$
CREATE PROCEDURE sp_create_character_2(pImgUrl varchar(250), pName varchar(60), pAge integer, pWeight integer, pStory varchar(250), out idCharacter int)
BEGIN
    insert into characters(img_url, name, age, weight, story) values
    (pImgUrl, pName, pAge, pWeight, pStory);
    set idCharacter = last_insert_id();
END;
$$
DELIMITER ;

/*
set @a = 1;
select @a;
set @a = 2;
select @a;

call sp_create_character_2('11','qq',1,1,'aaa',@a);

select * from characters;
*/

/* READ */
CREATE VIEW v_character_info
as
select
	c.id_character, c.img_url, c.name, c.age, c.weight, c.story
from 
	characters c;

/* UPDATE */ 
DELIMITER $$ 
CREATE PROCEDURE sp_update_character(pId_character int, pImgUrl varchar(250), pName varchar(60), pAge integer, pWeight integer, pStory varchar(250))
BEGIN
	DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
		signal sqlstate '45000' SET MESSAGE_TEXT = 'Incorrect character data', MYSQL_ERRNO = 137;
    END; 
    
    update 
		characters
	set
		img_url = pImgUrl,
        name = pName,
        age = pAge,
        weight = pWeight,
        story = pStory
	where
		id_character = pId_character;
END;
$$
DELIMITER ;

/* DELETE */
DELIMITER $$
CREATE PROCEDURE sp_delete_character(pId_character int)
BEGIN
	DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
		signal sqlstate '45000' SET MESSAGE_TEXT = 'Incorrect character id', MYSQL_ERRNO = 138;
    END; 
    
    delete from characters where id_character = pId_character;
END;
$$
DELIMITER ;

/* -------fin crud------- */

/* 5. Peliculas del personaje 
Deberá mostrares el detalle de las películas en las que haya participado un personaje.*/
#posteriormente vas a tener que cargar el dto con los generos correspondientes a la pelicula
create view v_movies_by_character
as
select
	cxm.id_character,
    m.id_movie, m.img_url, m.tittle, m.release_date, m.score
from 
	character_x_movie cxm inner join movie m on cxm.id_movie = m.id_movie;

# select * from v_movies_by_character where id_character = 1;

create view v_genres_x_movie
as
select 
	mg.id_movie_genre, mg.genre, mg.img_url,
    m.id_movie
from 
	movie m inner join genre_x_movie gxm on m.id_movie = gxm.id_movie
    inner join movie_genre mg on mg.id_movie_genre = gxm.id_movie_genre;

# select * from v_genres_x_movie where id_movie = 1;


/* 6. Busqueda de Personaejs
Deberán Prmitir buscar por nombre, y filtrar por edad, peso o peliculas/series en las que participo.
Para especificar el termino de búsqueda o filtros se deberán enviar como parámetros de query: */
select * from v_character_info;
/* •	GET /characers?name=nombre*/
select * from v_character_info where name = 'Manny';

/* •	GET /characers?age=edad */
select * from v_character_info where age = 30;

/* •	GET /characers?weight=peso */
select * from v_character_info where weight = 50;

/* •	GET /characers?movies=idMovie */ 
create view v_characters_by_movie_name
as
select 
	m.id_movie, m.tittle,
	c.id_character, c.img_url, c.name, c.age, c.weight, c.story
    from characters c inner join character_x_movie cxm on c.id_character = cxm.id_character
inner join movie m on m.id_movie = cxm.id_movie;

/*
select * from v_characters_by_movie_name where id_movie = -1 group by name;
select * from v_characters_by_movie_name where tittle LIKE 'La era de hi%' group by name;
*/
select * from v_characters_by_movie_name where id_movie = 1 group by name;

/*7. Listado de peliculas
Debera mostrar solamente los campos imagen, título y fecha de creación
El endpoint deberá ser: 
•	GET /movies */ 
create view v_movie_resume
as
select 
	m.img_url, m.tittle, m.release_date
from 
	movie m;
    
/*8. Detalle de película / serie con sus personajes
Devolverá todos los campos de la película o serie junto a los personajes asociados a la misma*/
/*select
	m.id_movie, m.img_url, m.tittle, m.release_date, m.score,
    c.id_character, */ 
create view v_all_characters_by_movie
as
select
	m.id_movie, m.img_url as movie_img_url, m.tittle, m.release_date, m.score,
    c.*
from 
	movie m join character_x_movie cxm on m.id_movie = cxm.id_movie
    join characters c on c.id_character = cxm.id_character
order by
	m.tittle;
    
/*
select * from v_all_characters_by_movie;
*/

/* 9. CRUD peliculas / serie */ 
/* CREATE */ 
DELIMITER $$
CREATE PROCEDURE sp_create_movie(pImgUrl varchar(250), pTittle varchar(250), pReleaseDate DATE, pScore integer, out idMovie int)
BEGIN
	DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
		signal sqlstate '45000' SET MESSAGE_TEXT = 'Incorrect movie data', MYSQL_ERRNO = 139;
    END; 
    
    insert into movie(img_url, tittle, release_date, score) values
    (pImgUrl, pTittle, pReleaseDate, pScore);
    set idMovie = last_insert_id();
END;
$$
DELIMITER ;
# ahora tenes que agregarle a la ultima movie sus generos

DELIMITER $$
CREATE PROCEDURE sp_insert_genre_x_movie(pIdMovie Integer, pIdGenre Integer)
BEGIN
    insert into genre_x_movie (id_movie, id_movie_genre) values (pIdMovie, pIdGenre);
END;
$$
DELIMITER ;

/* READ */
CREATE VIEW v_movie_info
as
select
	m.*
from 
	movie m;
    
create view v_movie_by_genre_id
as
select 
	gxm.id_movie_genre,
	m.*
from 
	movie m inner join genre_x_movie gxm on m.id_movie = gxm.id_movie;

select * from v_movie_by_genre_id where id_movie_genre = 1;

/* UPDATE */
DELIMITER $$
CREATE PROCEDURE sp_update_movie(pId_movie int, pImgUrl varchar(250), pTittle varchar(250), pReleaseDate DATE, pScore smallint)
BEGIN
	DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
		signal sqlstate '45000' SET MESSAGE_TEXT = 'Incorrect movie data', MYSQL_ERRNO = 140;
    END; 
    
    update 
		movie
	set
		img_url = pImgUrl,
        tittle = pTittle,
        release_date = pReleaseDate,
        score = pScore
	where
		id_movie = pId_movie;
END;
$$
DELIMITER ;

/* DELETE */
DELIMITER $$
CREATE PROCEDURE sp_delete_movie(pId_movie int)
BEGIN
	DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
		signal sqlstate '45000' SET MESSAGE_TEXT = 'Incorrect movie id', MYSQL_ERRNO = 141;
    END; 
    
    delete from movie where id_movie = pId_movie;
END;
$$
DELIMITER ;

/* -------fin crud------- */

/* 10. Busqueda de Pelicuals o series */
/*
select * from v_movie_info;
select * from v_movie_info where id_movie = 1;
select * from v_movie_info where tittle = '55';
*/