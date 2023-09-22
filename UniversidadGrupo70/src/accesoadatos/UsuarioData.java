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
Controlador de Usuario. Permite almacenar y recuperar usuarios de la BD.
 */
package accesoadatos;

import static accesoadatos.Utils.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import entidades.Usuario;


/**
 *
 * @author John David Molina Velarde, Leticia Mores, Enrique Germán Martínez, Carlos Eduardo Beltrán
 */
public class UsuarioData {
	ConexionMySQL conexion; //gestiona la conexión con la bd
	public enum OrdenacionUsuario {PORIDUSUARIO, PORDNI, PORAPYNO}; //tipo de ordenamiento
	
	public UsuarioData() {
		conexion = new ConexionMySQL();
		conexion.conectar(); //esto es opcional. Podría ponerse en el main.
	} //UsuarioData


	/**
	 * agrega el usuario a la BD. 
	 * @param usuario El que se dará de alta. Viene sin idusuario (se genera ahora)
	 * @return devuelve true si pudo darlo de alta
	 */
	public boolean altaUsuario(Usuario usuario){// 
		// una alternativa es usar ?,?,? y luego insertarlo con preparedStatement.setInt(1, dato) // o setString, setBoolean, setData
		String sql = "Insert into usuario (idusuario, dni, apellido, nombre) " +
			"VALUES " + "(null,'" + usuario.getDni() +  "','" + usuario.getApellido() + "','" +
			usuario.getNombre() + "')";
		if (conexion.sqlUpdate(sql)) {
			mensaje("Alta de usuario exitosa");
			usuario.setIdusuario(conexion.getKeyGenerado()); //asigno el id generado
			conexion.cerrarSentencia(); //cierra PreparedStatement y como consecuencia tambien el reultSet
			return true;
		} else {
			mensajeError("Falló el alta de usuario");
			return false;
		}
	} //altaUsuario
	
	
	/**
	 * Da de baja al usuario de la BD.
	 * @param usuario el usuario que se dará debaja (usando su idusuario)
	 * @return devuelve true si pudo darlo de baja
	 */
	public boolean bajaUsuario(Usuario usuario){// 
		return bajaUsuario(usuario.getIdusuario()); // llama a la baja usando el idusuario
	} //bajaUsuario
	
	
	/**
	 * Da de baja al usuario de la BD en base al id 
	 * @param id es el idusuario del usuario que se dará de baja
	 * @return  true si pudo darlo de baja
	 */
	public boolean bajaUsuario(int id){// devuelve true si pudo darlo de baja
		//Doy de baja al usuario
		String sql = "Delete from usuario where idusuario=" + id;
		if (conexion.sqlUpdate(sql)){
			mensaje("Baja de usuario exitosa");
			conexion.cerrarSentencia();
			return true;
		} 
		else {
			mensajeError("Falló la baja del usuario");
			return false;
		}
	} //bajaUsuario
	
	
	
	
	/**
	 * Modifica al usuario en la BD poniendole estos nuevos datos
	 * @param usuario el usuario que se modificará (en base a su idusuario)
	 * @return true si pudo modificarlo
	 */
	public boolean modificarUsuario(Usuario usuario){
		String sql = 
				"Update usuario set " + 
				"dni='" + usuario.getDni() + "'," + 
				"apellido='" + usuario.getApellido() + "'," +
				"nombre='" + usuario.getNombre() + "'," +
				"where idusuario='" + usuario.getIdusuario() + "'";
		if (conexion.sqlUpdate(sql)) {
			mensaje("Modificación de usuario exitosa");
			conexion.cerrarSentencia();
			return true;
		} 
		else {
			mensajeError("Falló la modificación de usuario");;
			return false;
		}
	} //modificarUsuario
	
	
	/**
	 * Dado un resultSet lo convierte en un Usuario
	 * @param rs es el ResultSet que se pasa para convertirlo en el objeto Usuario
	 * @return el usuario con los datos del resultSet
	 */
	public Usuario resultSet2Usuario(ResultSet rs){
		Usuario usuario = new Usuario();
		try {
			usuario.setIdusuario(rs.getInt("idusuario"));
			usuario.setDni(rs.getInt("dni"));
			usuario.setApellido(rs.getString("apellido"));
			usuario.setNombre(rs.getString("nombre"));
		} catch (SQLException ex) {
			//Logger.getLogger(UsuarioData.class.getName()).log(Level.SEVERE, null, ex);
			mensajeError("Error al pasar de ResultSet a Usuario"+ex.getMessage());
		}
		return usuario;
	} // resultSet2Usuario
	
	
	/**
	 * Devuelve una lista con los usuarios de la base de datos ordenados por idusuario
	 * @return la lista de usuarios
	 */
	public List<Usuario> getListaUsuarios(){ 
		return getListaUsuarios(OrdenacionUsuario.PORIDUSUARIO);
	} // getListaUsuarios
	
	
	/**
	 * Devuelve una lista ordenada con los usuarios de la base de datos
	 * @param ordenacion es el orden en el que se devolverán
	 * @return devuelve la lista de usuarios
	 */
	public List<Usuario> getListaUsuarios(OrdenacionUsuario ordenacion){
		ArrayList<Usuario> lista = new ArrayList();
		String sql = "Select * from usuario";
		
		//defino orden
		if (ordenacion == OrdenacionUsuario.PORIDUSUARIO) 
			sql = sql + " Order by idusuario";
		else if (ordenacion == OrdenacionUsuario.PORDNI)
			sql = sql + " Order by dni";
		else 
			sql = sql + " Order by apellido, nombre";
		
		//ejecuto
		ResultSet rs = conexion.sqlSelect(sql);
		
		//cargo la lista con los resultados
		try {
			while (rs.next()) {
				Usuario usuario = resultSet2Usuario(rs);
				lista.add(usuario);
			}
			conexion.cerrarSentencia(); // cierra el PreparedStatement y tambien cierra automaticamente el ResultSet
		} catch (SQLException ex) {
			mensajeError("Error al obtener lista de usuarios" + ex.getMessage());
		}
		return lista;
	} //getListaUsuarios
	
	
	
	/**
	 * devuelve una lista con los usuarios de la base de datos en base al criterio de búsqueda que se le pasa.
	 * Si dni no es -1 usa dni. Si apellido no es "" usa apellido. Si nombre no es "" usa nombre
	 * Si hay más de un criterio de búsqueda lo combina con ANDs
	 * Si no hay ningún criterio de búsqueda devuelve toda la tabla
	 * 
	 * @param idUsuario si idUsuario no es -1 usa idUsuario como criterio de búsqueda 
	 * @param dni      si dni no es -1 usa dni como criterio de búsqueda
	 * @param apellido si apellido no es "" usa apellido como criterio de búsqueda
	 * @param nombre   si nombre no es "" usa nombre como criterio de búsqueda
	 * @param ordenacion es el orden en el que devolverá la lista
	 * @return lista de usuarios que cumplen con el criterio de búsqueda
	 */
	public List<Usuario> getListaUsuariosXCriterioDeBusqueda(int idUsuario, int dni, String apellido, String nombre, OrdenacionUsuario ordenacion){ 
		ArrayList<Usuario> lista = new ArrayList();
		String sql = "Select * from usuario";
		if ( idUsuario != -1 || dni !=- 1 || ! apellido.isEmpty() || ! nombre.isEmpty() ) {
			sql = sql + " Where";
			
			if ( idUsuario != -1 )
				sql = sql + " idusuario=" + idUsuario;
			
			if ( dni != -1 ) {
				if (idUsuario != -1) //Si ya puse el idAlunno agrego and
					sql = sql+" AND";
				sql = sql+" dni="+dni;
			}
			
			if ( ! apellido.isEmpty() ){ 
				if (idUsuario != -1 || dni !=-1) //si ya puse idusuario o dni agrego and
					sql = sql + " AND";
				sql = sql + " apellido LIKE '" + apellido + "%'";
			}
			
			if ( ! nombre.isEmpty() ){
				if ( idUsuario != -1 || dni !=-1 || ! apellido.isEmpty() ) // si ya puse otro criterio agrego and
					sql = sql + " AND";
				sql = sql + " nombre LIKE '" + nombre + "%'";
			}
			
		}
		
		//defino orden
		if (ordenacion == OrdenacionUsuario.PORIDUSUARIO) 
			sql = sql + " Order by idusuario";
		else if (ordenacion == OrdenacionUsuario.PORDNI)
			sql = sql + " Order by dni";
		else 
			sql = sql + " Order by apellido, nombre";		
	
		// ejecuto
		ResultSet rs = conexion.sqlSelect(sql);
		
		// cargo la lista con los resultados
		try {
			while (rs.next()) {
				Usuario usuario = resultSet2Usuario(rs);
				lista.add(usuario);
			}
			conexion.cerrarSentencia(); // cierra el PreparedStatement y tambien cierra automaticamente el ResultSet
		} catch (SQLException ex) {
			mensajeError("Error al obtener lista de usuarios" + ex.getMessage());
		}
		return lista;
	} // getListaUsuariosXCriterioDeBusqueda
	
	
	
	/**
	 * Devuelve el usuario con ese idusuario
	 * @param id es el idusuario para identificarlo
	 * @return  el usuario retornado
	 */
	public Usuario getUsuario(int id){
		String sql = "Select * from usuario where idusuario=" + id;
		ResultSet rs = conexion.sqlSelect(sql);
		Usuario usuario = null;
		try {
			if (rs.next()) {
				usuario = resultSet2Usuario(rs);
				conexion.cerrarSentencia();
			} else
				mensajeError("Error al obtener un usuario");
		} catch (SQLException ex) {
			//Logger.getLogger(UsuarioData.class.getName()).log(Level.SEVERE, null, ex);
			mensajeError("Error al obtener un Usuario " + ex.getMessage());
		}
		return usuario;
	} //getUsuario
	
	
	/**
	 * Devuelve el usuario con ese apellido y nombre y con ese dni
	 * @param id es el idusuario para identificarlo
	 * @return  el usuario retornado. Si no lo encuentra devuelve null.
	 */
	public Usuario getUsuario(String apellido, String nombre, int dni){
		String sql = "Select * from usuario where nombre='" + nombre + "' " +
				"and apellido='" + apellido + "' " +
				"and dni='" + dni + "'";
		ResultSet rs = conexion.sqlSelect(sql);
		Usuario usuario = null;
		try {
			if (rs.next()) {
				usuario = resultSet2Usuario(rs);
				conexion.cerrarSentencia();
			} else
				mensajeError("Error al obtener un usuario");
		} catch (SQLException ex) {
			//Logger.getLogger(UsuarioData.class.getName()).log(Level.SEVERE, null, ex);
			mensajeError("Error al obtener un Usuario " + ex.getMessage());
		}
		return usuario;
	} //getUsuario
	
} //class UsuarioData
