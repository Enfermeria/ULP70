/*
	Trabajo práctico trasversal de la Guía 5 del curso Desarrollo de Apps
	Universidad de La Punta en el marco del proyecto Argentina Programa 4.0

	Integrantes:
		John David Molina Velarde
		Leticia Mores
		Enrique Germán Martínez
		Carlos Eduardo Beltrán
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
import entidades.Materia;


/**
 *
 * @author John David Molina Velarde, Leticia Mores, Enrique Germán Martínez, Carlos Eduardo Beltrán
 */
public class MateriaData {
	ConexionMySQL conexion;
	public MateriaData() {
		conexion = new ConexionMySQL();
		conexion.conectar(); //esto es opcional. Podría ponerse en el main.
	}
	
	public boolean altaMateria(Materia materia){// agrega la materia a la BD. materia viene sin idmateria. Devuelve true si pudo
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
	}
	
	
	public boolean bajaMateria(Materia materia){// da de baja la materia de la BD. materia viene con idmateria. Devuelve true si pudo
		return bajaMateria(materia.getIdmateria()); // llama a baja usando el idmateria
	}
	
	
	// da de baja la materia de la BD en base al id (si no tiene alumnos inscriptos)
	public boolean bajaMateria(int id){// devuelve true si pudo
		//Averiguo si tiene alumnos inscriptos
		InscripcionData inscripcionData = new InscripcionData();
		List<Alumno> listaalumnos = inscripcionData.getListaAlumnosXMateria(id);
		if (listaalumnos.size()>0) {
			mensajeError("No se puede dar de baja la materia porque tiene alumnos inscriptos. Borre dichas inscripciones antes.");
			return false;
		}
		
		//Doy de baja al alumno
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
	
	
	public boolean modificarMateria(Materia materia){// modifica la materia en la BD. materia viene con idmateria. Devuelve true si pudo
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
	}
	
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
	}
	
	public List<Materia> getListaMaterias(){ // devuelve una lista con las materias de la base de datos
		ArrayList<Materia> lista = new ArrayList();
		String sql = "Select * from materia";
		ResultSet rs = conexion.sqlSelect(sql);
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
	}
	
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
	}
}
