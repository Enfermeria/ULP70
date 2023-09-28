/*
	Trabajo práctico trasversal de la Guía 5 del curso Desarrollo de Apps
	Universidad de La Punta en el marco del proyecto Argentina Programa 4.0

	Integrantes:
		John David Molina Velarde
		Leticia Mores
		Enrique Germán Martínez
		Carlos Eduardo Beltrán
		
	Controlador de inscripciones. Permite almacenar y recuperar inscripciones de la bd.
 */
package accesoadatos;

import static accesoadatos.Utils.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import entidades.Alumno;
import entidades.Inscripcion;
import entidades.Materia;

/**
 *
 * @author John David Molina Velarde, Leticia Mores, Enrique Germán Martínez, Carlos Eduardo Beltrán
 */
public class InscripcionData {
	ConexionMySQL conexion; //gestiona la conexión con la bd
	
	
	/**
	 * Constructor. Gestiona la conexión con la bd.
	 */
	public InscripcionData() {
		conexion = new ConexionMySQL();
		conexion.conectar(); //esto es opcional. Podría ponerse en el main.
	} //constructor
	
	
	
	
	/**
	 * Agrega la inscripción que se le pasa como parámetro a la bd. Devuelve
	 * true si pudo hacer el alta.
	 * @param inscripcion Es la inscripción a agregar. Estoy presuponiendo 
	 *					  que viene sin idInscripcion, porque se autogenera.
	 * @return true si pudo darlo de alta
	 */
	public boolean altaInscripcion(Inscripcion inscripcion){
		//Estoy presuponiendo que al alumno y materia que trae la inscripcion YA ESTAN EN LA BD.
		// una alternativa es usar ?,?,? y luego insertarlo con preparedStatement.setInt(1, dato) // o setString, setBoolean, setData
		String sql = "Insert into inscripcion (idinscripcion, nota, idalumno, idmateria) " +
			"VALUES " + "(null,'" + 
				((inscripcion.getNota()==0.0)?"null":inscripcion.getNota()) +  "','" + 
				inscripcion.getAlumno().getIdalumno()+ "','" +
				inscripcion.getMateria().getIdmateria() + ")";
		if (conexion.sqlUpdate(sql)) {
			mensaje("Alta de inscripcion exitosa");
			inscripcion.setIdinscripcion(conexion.getKeyGenerado()); //asigno el id generado
			conexion.cerrarSentencia(); //cierra PreparedStatement y como consecuencia tambien el reultSet
			return true;
		} else {
			mensajeError("Falló el alta de inscripcion");
			return false;
		}
	} //AltaInscripcion
	
	
	
	/**
	 * Agrega la inscipción a la BD. Devuelve true si pudo agregarlo.
	 * @param nota     la nota de la materia
	 * @param idAlumno
	 * @param idMateria
	 * @return true si pudo agregarlo
	 */
	public boolean altaInscripcion(double nota, int idAlumno, int idMateria){
		//Estoy presuponiendo que al alumno y materia que trae la inscripcion YA ESTAN EN LA BD.
		// una alternativa es usar ?,?,? y luego insertarlo con preparedStatement.setInt(1, dato) // o setString, setBoolean, setData
		String sql = "Insert into inscripcion (idinscripcion, nota, idalumno, idmateria) " +
			"VALUES " + "(null," + 
				((nota==0.0)?"null":nota) +  ", " + 
				idAlumno + ", " +
				idMateria + ")";
		System.out.println("SQL:" + sql);
		if (conexion.sqlUpdate(sql)) {
			mensaje("Alta de inscripcion exitosa");
			conexion.cerrarSentencia(); //cierra PreparedStatement y como consecuencia tambien el reultSet
			return true;
		} else {
			mensajeError("Falló el alta de inscripcion");
			return false;
		}
	} //altaInscripcion
	
	
	
	
	
	
	
	/**
	 * da de baja al inscripcion de la BD. inscripcion viene con idinscripcion
	 * @param inscripcion
	 * @return true si pudo borrarlo
	 */
	public boolean bajaInscripcion(Inscripcion inscripcion){
		return bajaInscripcion(inscripcion.getIdinscripcion()); // llama a la baja usando el idinscripcion
	} //bajaInscripcion
	
	
	
	
	/**
	 * da de baja al inscripcion de la BD en base al idInscripcion.
	 * @param idInscripcion
	 * @return true si pudo borrarlo
	 */
	public boolean bajaInscripcion(int idInscripcion){
		String sql = "Delete from inscripcion where idinscripcion=" + idInscripcion;
		if (conexion.sqlUpdate(sql)){
			mensaje("Baja de inscripcion exitosa");
			conexion.cerrarSentencia();
			return true;
		} 
		else {
			mensajeError("Falló la baja de la inscripcion");
			return false;
		}
	} //bajaInscripcion
	
	
	
	/**
	 * da de baja al inscripcion de la BD en base al idAlumno y id Materia 
	 * @param idAlumno
	 * @param idMateria
	 * @return Devuelve true si pudo
	 */
	public boolean bajaInscripcion(int idAlumno, int idMateria){
		String sql = "Delete from inscripcion where idAlumno=" + idAlumno + " and idMateria=" + idMateria;
		if (conexion.sqlUpdate(sql)){
			mensaje("Baja de inscripcion exitosa");
			conexion.cerrarSentencia();
			return true;
		} 
		else {
			mensajeError("Falló la baja de la inscripcion");
			return false;
		}
	} //bajaInscripcion
	
	
	
	
	// 
	/**
	 * modifica al inscripcion en la BD. Usa idinscripcion para ubicar el
	 * registro en la bd.
	 * @param inscripcion
	 * @return 
	 */
	public boolean modificarInscripcion(Inscripcion inscripcion){//Devuelve true si pudo
		//Estoy presuponiendo que el alumno y materia que viene en la inscripcion YA ESTAN EN LA BD
		String sql = 
				"Update inscripcion set " + 
				"nota=" + ((inscripcion.getNota()==0.0)?"null":"'"+inscripcion.getNota()+"'") + "," + 
				"idalumno='" + inscripcion.getAlumno().getIdalumno()+ "'," +
				"idmateria='" + inscripcion.getMateria().getIdmateria()+ "'" + " " +
				"where idinscripcion='" + inscripcion.getIdinscripcion() + "'";
		if (conexion.sqlUpdate(sql)) {
			mensaje("Modificación de inscripcion exitosa");
			conexion.cerrarSentencia();
			return true;
		} 
		else {
			mensajeError("Falló la modificación de inscripcion");
			return false;
		}
	} //modificarInscripcion
	
	
	
	
	/**
	 * Dado un resultSet lo convierte en una Inscripcion (el primero del resultSet)
	 * @param rs es el ResultSet que se pasa para convertirlo en el objeto Inscripcion
	 * @return la inscripcion con los datos del resultSet
	 */
	public Inscripcion resultSet2Inscripcion(ResultSet rs){
		Inscripcion inscripcion = new Inscripcion();
		AlumnoData alumnoData = new AlumnoData();
		MateriaData materiaData = new MateriaData();
		try {
			inscripcion.setIdinscripcion(rs.getInt("idinscripcion"));
			inscripcion.setNota(rs.getDouble("nota"));
			inscripcion.setAlumno(alumnoData.getAlumno(rs.getInt("idalumno")));
			inscripcion.setMateria(materiaData.getMateria(rs.getInt("idmateria")));
		} catch (SQLException ex) {
			//Logger.getLogger(InscripcionData.class.getName()).log(Level.SEVERE, null, ex);
			mensajeError("Error al pasar de ResultSet a Inscripcion"+ex.getMessage());
		}
		return inscripcion;
	}
	
	
	
	
	/**
	 * Devuelve la lista completa de inscripciones de la base de datos
	 * @return la lista de inscripciones
	 */
	public List<Inscripcion> getListaInscripciones(){ 
		ArrayList<Inscripcion> lista = new ArrayList();
		String sql = "Select * from inscripcion";
		ResultSet rs = conexion.sqlSelect(sql);
		try {
			while (rs.next()) {
				Inscripcion inscripcion = resultSet2Inscripcion(rs);
				lista.add(inscripcion);
			}
			conexion.cerrarSentencia(); // cierra el PreparedStatement y tambien cierra automaticamente el ResultSet
		} catch (SQLException ex) {
			mensajeError("Error al obtener lista de inscripciones" + ex.getMessage());
		}
		return lista;
	} //getListaInscripciones
	
	
	
	
	
	/**
	 * dado un idAlumno devuelve la lista de inscripciones de ese alumno de la bd
	 * @param idAlumno
	 * @return lista de inscripciones de ese idAlumno
	 */
	public List<Inscripcion> getListaInscripcionesDelAlumno(int idAlumno){ 
		ArrayList<Inscripcion> lista = new ArrayList();
		String sql = "Select * from inscripcion where idalumno=" + idAlumno;
		ResultSet rs = conexion.sqlSelect(sql);
		try {
			while (rs.next()) {
				Inscripcion inscripcion = resultSet2Inscripcion(rs);
				lista.add(inscripcion);
			}
			conexion.cerrarSentencia(); // cierra el PreparedStatement y tambien cierra automaticamente el ResultSet
		} catch (SQLException ex) {
			mensajeError("Error al obtener lista de inscripciones" + ex.getMessage());
		}
		return lista;
	} //getListaInscripcionesDelAlumno
	
	
	
	
	
	/**
	 * dado un idMateria devuelve la lista de inscripciones de esa materia de la bd
	 * @param idMateria
	 * @return lista de inscripciones de esa idMateria
	 */
	public List<Inscripcion> getListaInscripcionesDeLaMateria(int idMateria){ 
		ArrayList<Inscripcion> lista = new ArrayList();
		String sql = "Select * from inscripcion where idmateria=" + idMateria;
		ResultSet rs = conexion.sqlSelect(sql);
		try {
			while (rs.next()) {
				Inscripcion inscripcion = resultSet2Inscripcion(rs);
				lista.add(inscripcion);
			}
			conexion.cerrarSentencia(); // cierra el PreparedStatement y tambien cierra automaticamente el ResultSet
		} catch (SQLException ex) {
			mensajeError("Error al obtener lista de inscripciones" + ex.getMessage());
		}
		return lista;
	} //getListaInscripcionesDeLaMateria
	
	

	

	/**
	 * dado un idmateria, devuelve la lista de alumnos que cursan esa materia
	 * @param idmateria
	 * @return lista de alumnos que cursan esa idmateria
	 */
	public List<Alumno> getListaAlumnosXMateria(int idmateria) { // dada un idMateria, devuelve la lista de alumnos que cursan dicha materia
		AlumnoData alumnoData = new AlumnoData();
		ArrayList<Alumno> listaAlumnos = new ArrayList();
		String sql = "Select a.idalumno, a.dni, a.apellido, a.nombre, a.fechaNacimiento, a.estado from inscripcion i, alumno a " +
				"where i.idalumno = a.idalumno and i.idmateria=" + idmateria;
		ResultSet rs = conexion.sqlSelect(sql);
		try {
			while (rs.next()) {
				Alumno alumno = alumnoData.resultSet2Alumno(rs);
				listaAlumnos.add(alumno);
			}
			conexion.cerrarSentencia(); // cierra el PreparedStatement y tambien cierra automaticamente el ResultSet
		} catch (SQLException ex) {
			mensajeError("Error al obtener lista de alumnos x materia" + ex.getMessage());
		}

		return listaAlumnos;
	} //getListaAlumnosXMateria
	
	
	
	
	
	/**
	 * dao un idAlumno, devuelve la lista de materias que cursa
	 * @param idalumno
	 * @return Lista de materias que cursa el idalumno
	 */
	public List<Materia> getListaMateriasXAlumno(int idalumno) {
		MateriaData materiaData = new MateriaData();
		ArrayList<Materia> listaMaterias = new ArrayList();
		String sql = "Select m.idmateria, m.nombre, m.anio, m.estado from inscripcion i, materia m " +
				"where i.idmateria = m.idmateria and i.idalumno=" + idalumno;
		ResultSet rs = conexion.sqlSelect(sql);
		try {
			while (rs.next()) {
				Materia materia = materiaData.resultSet2Materia(rs);
				listaMaterias.add(materia);
			}
			conexion.cerrarSentencia(); // cierra el PreparedStatement y tambien cierra automaticamente el ResultSet
		} catch (SQLException ex) {
			mensajeError("Error al obtener lista de materias x alumno" + ex.getMessage());
		}

		return listaMaterias;
	} // getListaMateriasXAlumno
	
	
	
	
	
	/**
	 * // dado un idAlumno, devuelve la lista de materias disponibles para 
	 * inscribirse (que no cursa)
	 * @param idalumno el alumnos usado en la consulta
	 * @return lista de materias disponibles para inscribirse
	 */
	public List<Materia> getListaMateriasDisponiblesXAlumno(int idalumno) { 
		MateriaData materiaData = new MateriaData();
		ArrayList<Materia> listaMaterias = new ArrayList();
		//String sql = 
		//	"select distinct m.idmateria, m.nombre, m.anio, m.estado from materia m, inscripcion i " +
		//	"where m.idmateria = i.idmateria and i.idmateria not in " +
		//	"(select m.idmateria from materia m, inscripcion i " +
		//	"where i.idmateria = m.idmateria and i.idalumno=" + idalumno + ")";
		String sql = 
			"select m.idmateria, m.nombre, m.anio, m.estado from materia m " +
			"where m.idmateria not in " + // "where m.estado and m.idmateria not in " + 
			"(select i.idmateria from inscripcion i where i.idalumno=" + idalumno + ")";
		ResultSet rs = conexion.sqlSelect(sql);
		try {
			while (rs.next()) {
				Materia materia = materiaData.resultSet2Materia(rs);
				listaMaterias.add(materia);
			}
			conexion.cerrarSentencia(); // cierra el PreparedStatement y tambien cierra automaticamente el ResultSet
		} catch (SQLException ex) {
			mensajeError("Error al obtener lista de materias disponibles x alumno" + ex.getMessage());
		}

		return listaMaterias;
	} // getListaMateriasDisponiblesXAlumno
	
	
	
	
	
	/**
	 * // dado un idMateria, devuelve la lista de alumnos disponibles para 
	 * inscribirse 
	 * @param idmateria la idmateria usada en la consulta
	 * @return lista de alummnos disponibles para inscribirse
	 */
	public List<Alumno> getListaAlumnosDisponiblesXMateria(int idmateria) { 
		AlumnoData alumnoData = new AlumnoData();
		ArrayList<Alumno> listaAlumnos = new ArrayList();
		String sql = 
			"select a.idalumno, a.dni, a.apellido, a.nombre, a.fechaNacimiento, a.estado from alumno a " +
			"where a.idalumno not in " +
			"(select i.idalumno from inscripcion i where i.idmateria=" + idmateria + ")";
		ResultSet rs = conexion.sqlSelect(sql);
		try {
			while (rs.next()) {
				Alumno alumno = alumnoData.resultSet2Alumno(rs);
				listaAlumnos.add(alumno);
			}
			conexion.cerrarSentencia(); // cierra el PreparedStatement y tambien cierra automaticamente el ResultSet
		} catch (SQLException ex) {
			mensajeError("Error al obtener lista de alumnos disponibles x materia" + ex.getMessage());
		}

		return listaAlumnos;
	} // getListaAlumnosDisponiblesXMateria
	
	
	
	
	/**
	 * dado un idInscripcion devuelve la inscripcion de la bd
	 * @param id el idinscripcion a buscar
	 * @return la Inscripcion. Si no lo encuentra devuelve null.
	 */
	public Inscripcion getInscripcion(int id){
		String sql = "Select * from inscripcion where idinscripcion=" + id;
		ResultSet rs = conexion.sqlSelect(sql);
		Inscripcion inscripcion = null;
		try {
			if (rs.next()) {
				inscripcion = resultSet2Inscripcion(rs);
				conexion.cerrarSentencia();
			} else
				mensajeError("Error al obtener una inscripcion");
		} catch (SQLException ex) {
			//Logger.getLogger(InscripcionData.class.getName()).log(Level.SEVERE, null, ex);
			mensajeError("Error al obtener un Inscripcion " + ex.getMessage());
		}
		return inscripcion;
	} //getInscripcion

}

