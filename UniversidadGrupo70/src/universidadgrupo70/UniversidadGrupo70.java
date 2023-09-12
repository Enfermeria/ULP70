/*
	Trabajo práctico trasversal de la Guía 5 del curso Desarrollo de Apps
	Universidad de La Punta en el marco del proyecto Argentina Programa 4.0

	Integrantes:
		John David Molina Velarde
		Leticia Mores
		Enrique Germán Martínez
		Carlos Eduardo Beltrán
 */

package universidadgrupo70;

import vistas.Login;
import vistas.PantallaPpal;

/**
 *
 * @author John David Molina Velarde, Leticia Mores, Enrique Germán Martínez, Carlos Eduardo Beltrán
 */
public class UniversidadGrupo70 {

	public static void main(String[] args) {
		//Entrada al sistema de gestión administrativa directamente
		//PantallaPpal pantallaPpal = new PantallaPpal(); //creo una pantallaPpal
		//pantallaPpal.setVisible(true); // lo hago visible
		//pantallaPpal.setLocationRelativeTo(null); // abrirlo en el centro		
		
		//Entrada a la pantalla de login
		Login login = new Login(); //creo una pantalla de login
		login.setVisible(true); // lo hago visible
		login.setLocationRelativeTo(null); // abrirlo en el centro		
	}
	
}



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
