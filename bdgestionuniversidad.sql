/*
Sistema de gestión para la Universidad de La Punta:

La ULP cree necesario utilizar un sistema para poder llevar el registro de los
alumnos de la institución y las materias que se dictan en la misma. Adicionalemente
se necesita poder registrar las materias que cursa cada alumnos. El sistema debe permitir
cargar la calificación obtenica (nota) cuando un alumno rinde un examen final. Para cada
materia que cursa un alumno solo se registrará la última calificación obtenida, o sea no 
se mantiene registro de las notas obtenidas anteriormente, por lo que, si un alumno rinde
el examen final de una materia y obtiene una calificación de "2", y luego rinde nuevamente
el examen para la materia y obtiene una calificación de "9" solo quedará registro de esta última.

Funcionalidad: el sistema deberá
1. Permitir al personal administrativo listar las materias que cursa un alumno.
2. Permitir al personal administrativo listar los alumnos inscriptos en una determinada materia.
3. Permitir que un alumno se pueda inscribir o des-inscribir en las materias que desee.
4. Permitir registrar la calificación final de una materia que está cursando un alumno.
5. Permitir el alta, baja y modificación de los alumnos y las materias.

Modelo de BD sugerido

alumno:
	* idAlumno: int(11)
	* dni: int(11)
	  apellido: varchar(100)
	  nombre: varchar(100)
	  fechaNacimiento: date
	  estado: tinyint(1)
inscripcion:
	* idInscripto: int(11)
	  nota: double
	  idAlumno: int(11)
	  idMateria: int(11)
materia:
	* idMateria: int(11)
	* nombre: varchar(100)
	  anio: int(11)
	  estado: tinyint(1)
*/

-- -------------------------------------------------------------------------
-- ----			ESTRUCTURA DE LA BD										----
-- -------------------------------------------------------------------------


drop schema if exists gestionuniversidad;

CREATE SCHEMA `gestionuniversidad` ;

CREATE TABLE `gestionuniversidad`.`alumno` (
  `idalumno` INT NOT NULL AUTO_INCREMENT,
  `dni` INT NOT NULL,
  `apellido` VARCHAR(100) NULL,
  `nombre` VARCHAR(100) NULL,
  `fechaNacimiento` DATE NULL,
  `estado` TINYINT NOT NULL,
  PRIMARY KEY (`idalumno`),
  UNIQUE INDEX `dni_UNIQUE` (`dni` ASC));


CREATE TABLE `gestionuniversidad`.`materia` (
  `idmateria` INT NOT NULL AUTO_INCREMENT,
  `nombre` VARCHAR(100) NULL,
  `anio` INT NULL,
  `estado` TINYINT NULL,
  PRIMARY KEY (`idmateria`));


CREATE TABLE `gestionuniversidad`.`inscripcion` (
  `idinscripcion` INT NOT NULL AUTO_INCREMENT,
  `nota` DOUBLE NULL,
  `idalumno` INT NOT NULL,
  `idmateria` INT NOT NULL,
  PRIMARY KEY (`idinscripcion`),
  INDEX `fk_inscripcion_alumno_idx` (`idalumno` ASC),
  CONSTRAINT `fk_inscripcion_alumno`
    FOREIGN KEY (`idalumno`)
    REFERENCES `gestionuniversidad`.`alumno` (`idalumno`)
    ON DELETE CASCADE
    ON UPDATE CASCADE);

ALTER TABLE `gestionuniversidad`.`inscripcion` 
ADD INDEX `fk_inscripcion_materia_idx` (`idmateria` ASC);
ALTER TABLE `gestionuniversidad`.`inscripcion` 
ADD CONSTRAINT `fk_inscripcion_materia`
  FOREIGN KEY (`idmateria`)
  REFERENCES `gestionuniversidad`.`materia` (`idmateria`)
  ON DELETE CASCADE
  ON UPDATE CASCADE;



-- -------------------------------------------------------------------------
-- ----			DATOS en tablas											----
-- -------------------------------------------------------------------------
INSERT INTO gestionuniversidad.alumno (idalumno, dni, apellido, nombre, fechaNacimiento, estado)
VALUES 
    (null, 12564278, 'Rivas', 'Jorge Alberto', '1976-08-15', 1),
    (null, 19456789, 'Gómez', 'María Laura', '1999-02-23', 1),
    (null, 21234567, 'Martínez', 'Carlos Daniel', '2001-05-10', 1),
    (null, 28976543, 'López', 'Ana Sofía', '1998-11-30', 1),
    (null, 34567890, 'Fernández', 'Luis Sebastián', '2000-07-18', 1),
    (null, 41234567, 'Rodríguez', 'Mariana Valeria', '1997-03-06', 1),
    (null, 51234567, 'Sánchez', 'Diego Alejandro', '2002-09-09', 1),
    (null, 67890123, 'Pérez', 'Carolina Andrea', '1995-12-14', 1),
    (null, 78901234, 'García', 'Fernando Raúl', '1994-04-20', 1),
    (null, 81234567, 'Ramírez', 'Lorena Gabriela', '2003-01-31', 1),
    (null, 91234567, 'Hernández', 'Andrés Eduardo', '1996-06-27', 1),
    (null, 10123456, 'Torres', 'Valentina Roxana', '2004-10-05', 1),
    (null, 11234567, 'Flores', 'Martín Ignacio', '1993-09-12', 1),
    (null, 12123456, 'Chávez', 'Paula Alejandra', '1997-07-28', 1),
    (null, 13123456, 'Álvarez', 'Javier Eduardo', '2000-03-17', 1),
    (null, 14123456, 'González', 'Natalia Lorena', '1999-08-21', 1),
    (null, 15123456, 'Reyes', 'Ezequiel Alejandro', '2002-12-03', 1),
    (null, 16123456, 'Díaz', 'Marcela Victoria', '1998-04-02', 1),
    (null, 17123456, 'Ortiz', 'Gabriel Alberto', '1996-11-11', 1),
    (null, 18123456, 'Silva', 'Luciana Rocío', '2001-06-25', 1),
    (null, 19123456, 'Pereyra', 'Matías Nicolás', '2003-02-08', 1),
    (null, 20123456, 'Vargas', 'Cecilia María', '1995-10-19', 1),
    (null, 21123456, 'Romero', 'Juan Pablo', '1994-01-09', 1),
    (null, 22123456, 'Álvarez', 'Florencia Andrea', '2002-07-22', 1),
    (null, 23123456, 'Soto', 'Maximiliano Sebastián', '1997-09-16', 1),
    (null, 24123456, 'Pérez', 'Victoria Belén', '1999-04-14', 1),
    (null, 25123456, 'Gómez', 'Santiago Ariel', '2000-11-05', 1),
    (null, 26123456, 'López', 'Romina Valeria', '2003-08-17', 1),
    (null, 27123456, 'Fernández', 'Francisco David', '1998-12-28', 1),
    (null, 28123456, 'Martínez', 'Agustina Gabriela', '1996-05-06', 1);



INSERT INTO gestionuniversidad.materia (idmateria, nombre, anio, estado)
VALUES 
    (null, 'Programación orientada a objetos II', '2', '1'),
    (null, 'Bases de Datos Avanzadas', '3', '1'),
    (null, 'Estructuras de Datos', '2', '1'),
    (null, 'Redes de Computadoras', '3', '1'),
    (null, 'Inteligencia Artificial', '4', '1'),
    (null, 'Análisis y Diseño de Sistemas', '3', '1'),
    (null, 'Programación Web Avanzada', '4', '1'),
    (null, 'Seguridad Informática', '4', '1'),
    (null, 'Sistemas Operativos', '2', '1'),
    (null, 'Matemáticas Discretas', '1', '1');


INSERT INTO gestionuniversidad.inscripcion (idinscripcion, nota, idalumno, idmateria)
VALUES 
    (null, '7.8', '5', '3'),
    (null, null, '12', '7'),
    (null, '6.5', '20', '1'),
    (null, null, '15', '9'),
    (null, '8.2', '8', '6'),
    (null, '9.0', '25', '10'),
    (null, null, '18', '4'),
    (null, '7.0', '3', '5'),
    (null, '5.6', '28', '2'),
    (null, '6.8', '9', '8'),
    (null, null, '22', '4'),
    (null, '8.5', '11', '3'),
    (null, null, '16', '9'),
    (null, '7.7', '6', '7'),
    (null, '9.2', '21', '10'),
    (null, null, '19', '1'),
    (null, '6.3', '4', '5'),
    (null, '8.0', '29', '2'),
    (null, null, '10', '6'),
    (null, '7.1', '23', '8'),
    (null, '5.4', '14', '4'),
    (null, null, '7', '9'),
    (null, '8.9', '2', '7'),
    (null, '9.7', '17', '10'),
    (null, null, '30', '1'),
    (null, '7.2', '13', '5'),
    (null, '6.9', '27', '2'),
    (null, null, '1', '6'),
    (null, '8.8', '24', '8'),
    (null, '5.2', '26', '4');


-- -------------------------------------------------------------------------
-- ----			ALGUNAS CONSULTAS										----
-- -------------------------------------------------------------------------
SELECT * FROM gestionuniversidad.alumno;

SELECT * FROM gestionuniversidad.alumno ORDER BY alumno.apellido ASC, alumno.nombre ASC;

SELECT * FROM gestionuniversidad.materia;

SELECT * FROM gestionuniversidad.inscripcion 
join alumno on inscripcion.idalumno = alumno.idalumno 
join materia on inscripcion.idmateria = materia.idmateria;




