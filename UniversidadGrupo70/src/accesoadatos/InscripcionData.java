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
import entidades.Inscripcion;
import entidades.Materia;

/**
 *
 * @author John David Molina Velarde, Leticia Mores, Enrique Germán Martínez, Carlos Eduardo Beltrán
 */
public class InscripcionData {
	ConexionMySQL conexion;
	public InscripcionData() {
		conexion = new ConexionMySQL();
		conexion.conectar(); //esto es opcional. Podría ponerse en el main.
	}
	
	public boolean altaInscripcion(Inscripcion inscripcion){// agrega la inscripcion a la BD. inscripcion viene sin idinscripcion. Devuelve true si pudo
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
	}
	
	
	
	
	//da de baja al inscripcion de la BD. inscripcion viene con idinscripcion
	public boolean bajaInscripcion(Inscripcion inscripcion){// devuelve true si pudo
		return bajaInscripcion(inscripcion.getIdinscripcion()); // llama a la baja usando el idinscripcion
	}
	
	
	
	public boolean bajaInscripcion(int idInscripcion){// da de baja al inscripcion de la BD en base al id. Devuelve true si pudo
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
	}
	
	
	
	// modifica al inscripcion en la BD. inscripcion viene con idinscripcion. 
	public boolean modificarInscripcion(Inscripcion inscripcion){//Devuelve true si pudo
		//Estoy presuponiendo que el alumno y materia que viene en la inscripcion YA ESTAN EN LA BD
		String sql = 
				"Update inscripcion set " + 
				"nota='" + ((inscripcion.getNota()==0.0)?"null":inscripcion.getNota()) + "'," + 
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
	}
	
	
	
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
	
	
	
	public List<Inscripcion> getListaInscripciones(){ // devuelve una lista con los inscripciones de la base de datos
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
	}
	
	

	public List<Inscripcion> getListaInscripcionesDelAlumno(int idAlumno){ // devuelve una lista con los inscripciones de ese alumno de la base de datos
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
	}
	
	
        
        public List<Inscripcion> getListaInscripcionesDisponibles(int idAlumno){ // devuelve una lista con los inscripciones de ese alumno de la base de datos
		ArrayList<Inscripcion> lista = new ArrayList();
		String sql = "Select * from inscripcion where idalumno<>" + idAlumno;
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
	}


	
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
	}
	
	
	
	public List<Materia> getListaMateriasXAlumno(int idalumno) { // dada un idAlumno, devuelve la lista de materias que cursa
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
	}
	
	
	
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
	}

}

