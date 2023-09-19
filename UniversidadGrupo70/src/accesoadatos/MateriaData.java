/*
	Trabajo práctico trasversal de la Guía 5 del curso Desarrollo de Apps
	Universidad de La Punta en el marco del proyecto Argentina Programa 4.0

	Integrantes:
		John David Molina Velarde
		Leticia Mores
		Enrique Germán Martínez
		Carlos Eduardo Beltrán
 */


/*
Controlador de Alumno. Permite almacenar y recuperar alumnos de la BD.
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
public class MateriaData {
	ConexionMySQL conexion; //gestiona la conexión con la bd
	public enum OrdenacionMateria {PORIDMATERIA, PORANIO, PORNOMBRE}; //tipo de ordenamiento
	
	public MateriaData() {
		conexion = new ConexionMySQL();
		conexion.conectar(); //esto es opcional. Podría ponerse en el main.
	} //MateriaData
	
	
	/**
	 * agrega la materia a la BD. 
	 * @param materia La que se dará de alta. Viene sin idmateria (se genera ahora)
	 * @return devuelve true si pudo darlo de alta
	 */
	public boolean altaMateria(Materia materia){
		// una alternativa es usar ?,?,? y luego insertarlo con preparedStatement.setInt(1, dato) // o setString, setBoolean, setData
		String sql = "Insert into materia (idmateria, nombre, anio, estado) " +
			"VALUES " + "(null,'" + materia.getNombre() + "','" + materia.getAnio() + "'," + materia.getEstado() + ")";
		if (conexion.sqlUpdate(sql)) {
			mensaje("Alta de materia exitosa");
			materia.setIdmateria(conexion.getKeyGenerado()); //asigno el id generado
			conexion.cerrarSentencia(); //cierra PreparedStatement y como consecuencia tambien el reultSet
			return true;
		} else {
			mensajeError("Falló el alta de materia");
			return false;
		}
	}//altaMataria
		
	
	
	/**
	 * Da de baja la materia de la BD.
	 * @param materia la materia que se dará debaja 
	 * @return devuelve true si pudo darlo de baja
	 */
	public boolean bajaMateria(Materia materia){// 
		return bajaMateria(materia.getIdmateria()); // llama a la baja usando el idmateria
	} //bajaAlumno
	
	
	/**
	 * Da de baja la materia de la BD.
	 * @param idmateria la materia que se dará debaja (usando su idmateria)
	 * @return devuelve true si pudo darlo de baja
	 */
	public boolean bajaMateria(int id){
		//Averiguo si tiene alumnos inscriptos
		InscripcionData inscripcionData = new InscripcionData();
		List<Alumno> listaalumnos = inscripcionData.getListaAlumnosXMateria(id);
		if (listaalumnos.size()>0) {
			mensajeError("No se puede dar de baja la materia porque tiene alumnos inscriptos. Borre dichas inscripciones antes.");
			return false;
		}
		
		//Doy de baja la materia
		String sql = "Delete from materia where idmateria=" + id;
		if (conexion.sqlUpdate(sql)){
			mensaje("Baja de materia exitosa");
			conexion.cerrarSentencia();
			return true;
		} 
		else {
			mensajeError("Falló la baja de materia");;
			return false;
		}
	}
	
	
	
	/**
	 * Modifica la materia en la BD poniendole estos nuevos datos
	 * @param materia la materia que se modificará (en base a su idmateria)
	 * @return true si pudo modificarlo
	 */
	public boolean modificarMateria(Materia materia){
		String sql = 
				"Update materia set " + 
				"nombre='" + materia.getNombre() + "'," +
				"anio='" + materia.getAnio() + "'," +
				"estado=" + materia.getEstado() + " " +
				"where idmateria='" + materia.getIdmateria() + "'";
		if (conexion.sqlUpdate(sql)) {
			mensaje("Modificación de materia exitosa");
			conexion.cerrarSentencia();
			return true;
		} 
		else {
			mensajeError("Falló la modificación de materia");;
			return false;
		}
	} //modificarMateria
	
	
	/**
	 * Dado un resultSet lo convierte en una Materia
	 * @param rs es el ResultSet que se pasa para convertirlo en el objeto Materia
	 * @return la materia con los datos del resultSet
	 */
	public Materia resultSet2Materia(ResultSet rs){
		Materia materia = new Materia();
		try {
			materia.setIdmateria(rs.getInt("idmateria"));
			materia.setNombre(rs.getString("nombre"));
			materia.setAnio(rs.getInt("anio"));
			materia.setEstado(rs.getBoolean("estado"));
		} catch (SQLException ex) {
			//Logger.getLogger(MateriaData.class.getName()).log(Level.SEVERE, null, ex);
			mensajeError("Error al pasar de ResultSet a Materia"+ex.getMessage());
		}
		return materia;
	}//resultSet2Materia
	
	
	/**
	 * Devuelve una lista con los alumnos de la base de datos ordenados por idalumno
	 * @return la lista de alumnos
	 */
	public List<Materia> getListaMaterias(){ 
		return getListaMaterias(OrdenacionMateria.PORIDMATERIA);
	} // getListaMaterias
	
	
	
	/**
	 * Devuelve una lista ordenada con las materias de la base de datos
	 * @param ordenacion es el orden en el que se devolverán
	 * @return devuelve la lista de materias
	 */
	public List<Materia> getListaMaterias(OrdenacionMateria ordenacion){
		ArrayList<Materia> lista = new ArrayList();
		String sql = "Select * from materia";
		
		//defino orden
		if (ordenacion == OrdenacionMateria.PORIDMATERIA) 
			sql = sql + " Order by idmateria";
		else if (ordenacion == OrdenacionMateria.PORANIO)
			sql = sql + " Order by anio";
		else // solo queda OrdenacionMateria.PORNOMBRE
			sql = sql + " Order by nombre";
		
		//ejecuto
		ResultSet rs = conexion.sqlSelect(sql);
		
		//cargo la lista con los resultados
		try {
			while (rs.next()) {
				Materia materia = resultSet2Materia(rs);
				lista.add(materia);
			}
			conexion.cerrarSentencia(); // cierra el PreparedStatement y tambien cierra automaticamente el ResultSet
		} catch (SQLException ex) {
			mensajeError("Error al obtener lista de materias" + ex.getMessage());
		}
		return lista;
	} //getListaMaterias
	
	
	/**
	 * devuelve una lista con las materias de la base de datos en base al criterio de búsqueda que se le pasa.
	 * Si idMateria no es -1 usa idMateria como criterio de busqueda.Si anio no es -1 usa anio. Si nombre no es "" usa nombre. 
	 * Si hay más de un criterio de búsqueda lo combina con ANDs
	 * Si no hay ningún criterio de búsqueda devuelve toda la tabla
	 * 
	 * @param idMateria si idMateria no es -1 usa idMateria como criterio de búsqueda 
	 * @param anio      si dni no es -1 usa dni como criterio de búsqueda
	 * @param nombre    si nombre no es "" usa nombre como criterio de búsqueda
	 * @param ordenacion es el orden en el que devolverá la lista
	 * @return lista de materias que cumplen con el criterio de búsqueda
	 */
	public List<Materia> getListaMateriasXCriterioDeBusqueda(int idMateria, int anio, String nombre, OrdenacionMateria ordenacion){ 
		ArrayList<Materia> lista = new ArrayList();
		String sql = "Select * from materia";
		if ( idMateria != -1 || anio !=- 1 || ! nombre.isEmpty() ) {
			sql = sql + " Where";
			
			if ( idMateria != -1 )
				sql = sql + " idmateria=" + idMateria;
			
			if ( anio != -1 ) {
				if (idMateria != -1) //Si ya puse el idMateria agrego and
					sql = sql+" AND";
				sql = sql+" anio="+anio;
			}
			
			if ( ! nombre.isEmpty() ){ 
				if (idMateria != -1 || anio !=-1) //si ya puse idmateria o anio agrego and
					sql = sql + " AND";
				sql = sql + " nombre LIKE '" + nombre + "%'";
			}			
		}
		
		//defino orden
		if (ordenacion == OrdenacionMateria.PORIDMATERIA) 
			sql = sql + " Order by idmateria";
		else if (ordenacion == OrdenacionMateria.PORANIO)
			sql = sql + " Order by anio";
		else 
			sql = sql + " Order by nombre";		
	
		// ejecuto
		ResultSet rs = conexion.sqlSelect(sql);
		
		// cargo la lista con los resultados
		try {
			while (rs.next()) {
				Materia materia = resultSet2Materia(rs);
				lista.add(materia);
			}
			conexion.cerrarSentencia(); // cierra el PreparedStatement y tambien cierra automaticamente el ResultSet
		} catch (SQLException ex) {
			mensajeError("Error al obtener lista de materias" + ex.getMessage());
		}
		return lista;
	} // getListaMateriasXCriterioDeBusqueda
	
	
	
	/**
	 * Devuelve la materia con ese idmateria
	 * @param id es el idmateria para identificarlo
	 * @return  la materia retornado
	 */
	public Materia getMateria(int id){
		String sql = "Select * from materia where idmateria=" + id;
		ResultSet rs = conexion.sqlSelect(sql);
		Materia materia = null;
		try {
			if (rs.next()) {
				materia = resultSet2Materia(rs);
				conexion.cerrarSentencia();
			} else
				mensajeError("Error al obtener una materia");
		} catch (SQLException ex) {
			//Logger.getLogger(MateriaData.class.getName()).log(Level.SEVERE, null, ex);
			mensajeError("Error al obtener una Materia " + ex.getMessage());
		}
		return materia;
	} //getMateria
	
}// class MateriaData
