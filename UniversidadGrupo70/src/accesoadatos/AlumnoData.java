/*
	Trabajo práctico trasversal de la Guía 5 del curso Desarrollo de Apps
	Universidad de La Punta en el marco del proyecto Argentina Programa 4.0

	Integrantes:
		John David Molina Velarde
		Leticia Mores
		Enrique Germán Martínez
		Carlos Eduardo Beltrán

	Controlador de Alumno. Permite almacenar y recuperar alumnos de la bd.
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
public class AlumnoData {
	ConexionMySQL conexion; //gestiona la conexión con la bd
	public enum OrdenacionAlumno {PORIDALUMNO, PORDNI, PORAPYNO}; //tipo de ordenamiento
	
	
	/**
	 * constructor. Gestiona la conexión con la bd.
	 */
	public AlumnoData() {
		conexion = new ConexionMySQL();
		conexion.conectar(); //esto es opcional. Podría ponerse en el main.
	} //AlumnoData

	

	/**
	 * agrega el alumno a la BD. 
	 * @param alumno El que se dará de alta. Viene sin idalumno (se genera ahora)
	 * @return devuelve true si pudo darlo de alta
	 */
	public boolean altaAlumno(Alumno alumno){// 
		// una alternativa es usar ?,?,? y luego insertarlo con preparedStatement.setInt(1, dato) // o setString, setBoolean, setData
		String sql = "Insert into alumno (idalumno, dni, apellido, nombre, fechaNacimiento, estado) " +
			"VALUES " + "(null,'" + alumno.getDni() +  "','" + alumno.getApellido() + "','" +
			alumno.getNombre() + "','" + alumno.getFechaNacimiento() + "'," + alumno.getEstado() + ")";
		if (conexion.sqlUpdate(sql)) {
			mensaje("Alta de alumno exitosa");
			alumno.setIdalumno(conexion.getKeyGenerado()); //asigno el id generado
			conexion.cerrarSentencia(); //cierra PreparedStatement y como consecuencia tambien el reultSet
			return true;
		} else {
			mensajeError("Falló el alta de alumno");
			return false;
		}
	} //altaAlumno
	
	
	
	
	/**
	 * Da de baja al alumno de la BD.
	 * @param alumno el alumno que se dará debaja (usando su idalumno)
	 * @return devuelve true si pudo darlo de baja
	 */
	public boolean bajaAlumno(Alumno alumno){// 
		return bajaAlumno(alumno.getIdalumno()); // llama a la baja usando el idalumno
	} //bajaAlumno
	
	
	
	
	/**
	 * Da de baja al alumno de la BD en base al id (si no está inscripto en materias)
	 * @param id es el idalumno del alumno que se dará de baja
	 * @return  true si pudo darlo de baja
	 */
	public boolean bajaAlumno(int id){// devuelve true si pudo darlo de baja
		//Averiguo si esta inscripto en alguna materia
		InscripcionData inscripcionData = new InscripcionData();
		List<Materia> listamaterias = inscripcionData.getListaMateriasXAlumno(id);
		if (listamaterias.size()>0) {
			mensajeError("No se puede dar de baja al alumno porque está inscripto en materias. Borre dichas inscripciones antes.");
			return false;
		}
		
		//Doy de baja al alumno
		String sql = "Delete from alumno where idalumno=" + id;
		if (conexion.sqlUpdate(sql)){
			mensaje("Baja de alumno exitosa");
			conexion.cerrarSentencia();
			return true;
		} 
		else {
			mensajeError("Falló la baja del alumno");
			return false;
		}
	} //bajaAlumno
	
	
	
	
	
	/**
	 * Da de baja al alumno de la BD en base al id. Si está con inscripciones, 
	 * también las da de baja.
	 * @param idAlumno es el idalumno del alumno que se dará de baja
	 * @return  true si pudo darlo de baja
	 */
	public boolean bajaAlumnoconInscripcionesEnCascada(int idAlumno){// devuelve true si pudo darlo de baja
		//Borro todas las inscripciones de ese alumno
		InscripcionData inscripcionData = new InscripcionData();
		List<Inscripcion> listaInscripciones = inscripcionData.getListaInscripcionesDelAlumno(idAlumno);
		for (Inscripcion inscripcion : listaInscripciones)
			inscripcionData.bajaInscripcion(inscripcion);
		
		
		//Doy de baja al alumno
		String sql = "Delete from alumno where idalumno=" + idAlumno;
		if (conexion.sqlUpdate(sql)){
			mensaje("Baja de alumno exitosa");
			conexion.cerrarSentencia();
			return true;
		} 
		else {
			mensajeError("Falló la baja del alumno");
			return false;
		}
	} //bajaAlumnoInscripcionesEnCascada
	
	
	
	
	
	
	/**
	 * Modifica al alumno en la BD poniendole estos nuevos datos
	 * @param alumno el alumno que se modificará (en base a su idalumno)
	 * @return true si pudo modificarlo
	 */
	public boolean modificarAlumno(Alumno alumno){
		String sql = 
				"Update alumno set " + 
				"dni='" + alumno.getDni() + "'," + 
				"apellido='" + alumno.getApellido() + "'," +
				"nombre='" + alumno.getNombre() + "'," +
				"fechaNacimiento='" + alumno.getFechaNacimiento() + "'," +
				"estado=" + alumno.getEstado() + " " +
				"where idalumno='" + alumno.getIdalumno() + "'";
		if (conexion.sqlUpdate(sql)) {
			mensaje("Modificación de alumno exitosa");
			conexion.cerrarSentencia();
			return true;
		} 
		else {
			mensajeError("Falló la modificación de alumno");;
			return false;
		}
	} //modificarAlumno
	
	
	
	
	
	/**
	 * Dado un resultSet lo convierte en un Alumno
	 * @param rs es el ResultSet que se pasa para convertirlo en el objeto Alumno
	 * @return el alumno con los datos del resultSet
	 */
	public Alumno resultSet2Alumno(ResultSet rs){
		Alumno alumno = new Alumno();
		try {
			alumno.setIdalumno(rs.getInt("idalumno"));
			alumno.setDni(rs.getInt("dni"));
			alumno.setApellido(rs.getString("apellido"));
			alumno.setNombre(rs.getString("nombre"));
			alumno.setFechaNacimiento(rs.getDate("fechaNacimiento").toLocalDate());
			alumno.setEstado(rs.getBoolean("estado"));
		} catch (SQLException ex) {
			//Logger.getLogger(AlumnoData.class.getName()).log(Level.SEVERE, null, ex);
			mensajeError("Error al pasar de ResultSet a Alumno"+ex.getMessage());
		}
		return alumno;
	} // resultSet2Alumno
	
	
	
	
	
	/**
	 * Devuelve una lista con los alumnos de la base de datos ordenados por idalumno
	 * @return la lista de alumnos
	 */
	public List<Alumno> getListaAlumnos(){ 
		return getListaAlumnos(OrdenacionAlumno.PORIDALUMNO);
	} // getListaAlumnos
	
	
	
	
	
	/**
	 * Devuelve una lista ordenada con los alumnos de la base de datos
	 * @param ordenacion es el orden en el que se devolverán
	 * @return devuelve la lista de alumnos
	 */
	public List<Alumno> getListaAlumnos(OrdenacionAlumno ordenacion){
		ArrayList<Alumno> lista = new ArrayList();
		String sql = "Select * from alumno";
		
		//defino orden
		if (ordenacion == OrdenacionAlumno.PORIDALUMNO) 
			sql = sql + " Order by idalumno";
		else if (ordenacion == OrdenacionAlumno.PORDNI)
			sql = sql + " Order by dni";
		else 
			sql = sql + " Order by apellido, nombre";
		
		//ejecuto
		ResultSet rs = conexion.sqlSelect(sql);
		
		//cargo la lista con los resultados
		try {
			while (rs.next()) {
				Alumno alumno = resultSet2Alumno(rs);
				lista.add(alumno);
			}
			conexion.cerrarSentencia(); // cierra el PreparedStatement y tambien cierra automaticamente el ResultSet
		} catch (SQLException ex) {
			mensajeError("Error al obtener lista de alumnos" + ex.getMessage());
		}
		return lista;
	} //getListaAlumnos
	
	
	
	
	
	/**
	 * devuelve una lista con los alumnos de la base de datos en base al criterio de búsqueda que se le pasa.
	 * Si dni no es -1 usa dni. Si apellido no es "" usa apellido. Si nombre no es "" usa nombre
	 * Si hay más de un criterio de búsqueda lo combina con ANDs
	 * Si no hay ningún criterio de búsqueda devuelve toda la tabla
	 * 
	 * @param idAlumno si idAlumno no es -1 usa idAlumno como criterio de búsqueda 
	 * @param dni      si dni no es -1 usa dni como criterio de búsqueda
	 * @param apellido si apellido no es "" usa apellido como criterio de búsqueda
	 * @param nombre   si nombre no es "" usa nombre como criterio de búsqueda
	 * @param ordenacion es el orden en el que devolverá la lista
	 * @return lista de alumnos que cumplen con el criterio de búsqueda
	 */
	public List<Alumno> getListaAlumnosXCriterioDeBusqueda(int idAlumno, int dni, String apellido, String nombre, OrdenacionAlumno ordenacion){ 
		ArrayList<Alumno> lista = new ArrayList();
		String sql = "Select * from alumno";
		if ( idAlumno != -1 || dni !=- 1 || ! apellido.isEmpty() || ! nombre.isEmpty() ) {
			sql = sql + " Where";
			
			if ( idAlumno != -1 )
				sql = sql + " idalumno=" + idAlumno;
			
			if ( dni != -1 ) {
				if (idAlumno != -1) //Si ya puse el idAlunno agrego and
					sql = sql+" AND";
				sql = sql+" dni="+dni;
			}
			
			if ( ! apellido.isEmpty() ){ 
				if (idAlumno != -1 || dni !=-1) //si ya puse idalumno o dni agrego and
					sql = sql + " AND";
				sql = sql + " apellido LIKE '" + apellido + "%'";
			}
			
			if ( ! nombre.isEmpty() ){
				if ( idAlumno != -1 || dni !=-1 || ! apellido.isEmpty() ) // si ya puse otro criterio agrego and
					sql = sql + " AND";
				sql = sql + " nombre LIKE '" + nombre + "%'";
			}
			
		}
		
		//defino orden
		if (ordenacion == OrdenacionAlumno.PORIDALUMNO) 
			sql = sql + " Order by idalumno";
		else if (ordenacion == OrdenacionAlumno.PORDNI)
			sql = sql + " Order by dni";
		else 
			sql = sql + " Order by apellido, nombre";		
	
		// ejecuto
		ResultSet rs = conexion.sqlSelect(sql);
		
		// cargo la lista con los resultados
		try {
			while (rs.next()) {
				Alumno alumno = resultSet2Alumno(rs);
				lista.add(alumno);
			}
			conexion.cerrarSentencia(); // cierra el PreparedStatement y tambien cierra automaticamente el ResultSet
		} catch (SQLException ex) {
			mensajeError("Error al obtener lista de alumnos" + ex.getMessage());
		}
		return lista;
	} // getListaAlumnosXCriterioDeBusqueda
	
	
	
	
	
	/**
	 * Devuelve el alumno con ese idalumno
	 * @param id es el idalumno para identificarlo
	 * @return  el alumno retornado
	 */
	public Alumno getAlumno(int id){
		String sql = "Select * from alumno where idalumno=" + id;
		ResultSet rs = conexion.sqlSelect(sql);
		Alumno alumno = null;
		try {
			if (rs.next()) {
				alumno = resultSet2Alumno(rs);
				conexion.cerrarSentencia();
			} else
				mensaje("Error al obtener un alumno");
		} catch (SQLException ex) {
			//Logger.getLogger(AlumnoData.class.getName()).log(Level.SEVERE, null, ex);
			mensajeError("Error al obtener un Alumno " + ex.getMessage());
		}
		return alumno;
	} //getAlumno
	
	
	
	
	
	/**
	 * Devuelve el alumno con ese apellido y nombre y con ese dni
	 * @param id es el idalumno para identificarlo
	 * @return  el alumno retornado. Si no lo encuentra devuelve null.
	 */
	public Alumno getAlumno(String apellido, String nombre, int dni){
		String sql = "Select * from alumno where nombre='" + nombre + "' " +
				"and apellido='" + apellido + "' " +
				"and dni='" + dni + "'";
		ResultSet rs = conexion.sqlSelect(sql);
		Alumno alumno = null;
		try {
			if (rs.next()) {
				alumno = resultSet2Alumno(rs);
				conexion.cerrarSentencia();
			} else
				mensaje("Error al obtener un alumno");
		} catch (SQLException ex) {
			//Logger.getLogger(AlumnoData.class.getName()).log(Level.SEVERE, null, ex);
			mensajeError("Error al obtener un Alumno " + ex.getMessage());
		}
		return alumno;
	} //getAlumno
	
} //class AlumnoData
