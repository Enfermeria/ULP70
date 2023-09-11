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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author John David Molina Velarde, Leticia Mores, Enrique Germán Martínez, Carlos Eduardo Beltrán
 */

public class ConexionMySQL { //
	//constantes usadas
	private static final String DB = "gestionuniversidad";
	// datos de mysql de john
	private static final String DRIVERmysql = "com.mysql.cj.jdbc.Driver";     // este es el driver para MySQL que usa John
	private static final String URLmysql = "jdbc:mysql://localhost/"+DB;      // esta es la ruta que usa John para MySQL
	private static final String USERmysql = "john";
	private static final String PASSmysql = "molina";						  
	// datos de mariadb de los chicos
	private static final String DRIVERmariadb = "org.mariadb.jdbc.Driver";    // este es el driver para mariaDB que usan los chicos
	private static final String URLmariadb = "jdbc:mariadb://localhost/"+DB;  // esta es la ruta que usan los chicos para mariaDB
	private static final String USERmariadb = "root";
	private static final String PASSmariadb = "";							  
	
	//variables usadas
	public static Connection conexion = null;
	public PreparedStatement preparedStatement = null;

	
	
	public ConexionMySQL() { // constructor
	}
	
	
	
	/**
	 * Intenta conecta a alguna de las BD MySQL o MariaDB. Si pudo almacena la conexión en "conexión"
	 * @return  devuelve true si pudo conectar. 
	 */
	public static boolean conectar() { 
		// intenta conectar primero con MySQL
		if ( IntentaConectarBD(DRIVERmysql, URLmysql, USERmysql, PASSmysql) )
			return true;
		
		// no pudo conectar con MySQL, entonce ahora intenta con MariaDB
		if ( IntentaConectarBD(DRIVERmariadb, URLmariadb, USERmariadb, PASSmariadb ) )
			return true;

		//si llegó hasta acá, no pudo conectar ni con MySQL ni con MariaDB
		JOptionPane.showMessageDialog(null, "Error de conexión con la BD. No pudo conectar ni con MySQL ni con MariaDB");
		return false; // no pudo conectar
	} // conectar
	
	
	
	private static boolean IntentaConectarBD(String driver, String url, String user, String pass){ //devuelve true si pudo conectar. Almacena conexión en conexión
		try {
			if (conexion == null) {// si nunca fue llamado antes
				Class.forName(driver);
				conexion = DriverManager.getConnection(url, user, pass);
				//la profesora Saez usa este:
				// conexion = DriverManager.getConnection(URL + "?useLegacyDatetimeCode=false&serverTimezone=UTC&user=" + USER + "&password=" + PASS);
			
			}
			return true; // devuelve true ya sea que sea la primera conexión (era null) o ya estaba conectado de antes.
		} catch (ClassNotFoundException ex) {
			//Logger.getLogger(BDG3Prueba.class.getName()).log(Level.SEVERE, null, ex);
			//JOptionPane.showMessageDialog(null, "Error al cargar el Driver JDBC: " + ex.getMessage());
			System.out.println("Error al cargar el Driver JDBC: " + ex.getMessage());
			return false;
		} catch (SQLException ex) {
			JOptionPane.showMessageDialog(null, "Error de conexión sql: " + ex.getMessage());
			return false;
		}
	} //conectar MySQL


	
	/**
	 * Se desconecta de la bd usando la variable conexion donde está almacenada la Connection
	 * (eso si había una conexión previa)
	 */
	public static void desconectar() {
		try {
			if (conexion != null)
				conexion.close();
		} catch (SQLException ex) {
			//Logger.getLogger(ConexionMySQL.class.getName()).log(Level.SEVERE, null, ex);
			JOptionPane.showMessageDialog(null, "Error al desconectar sql: " + ex.getMessage());
		}
	} //desconectar
	
	
	
	/**
	 * Prepara la sentencia SQL para ser luego ejecutada. Es de uso interno por sqlUdate y sqlSelect
	 * @param sql es el string con el comando SQL a ejecutar
	 * @return devuelve el PreparedStatement para poder ejecutarla luego
	 */
	private PreparedStatement prepararSentencia(String sql) { // devuelve null si falló
		try {
			PreparedStatement ps = conexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			return ps; // tuvo exito
		} catch (SQLException ex) {
			//Logger.getLogger(ConexionMySQL.class.getName()).log(Level.SEVERE, null, ex);
			JOptionPane.showMessageDialog(null, "Error al preparar sentencia sql: " + ex.getMessage());
			return null;
		}
	} //prepararSentencia
	

	
	/**
	 * cierra la preparedStatement previamente armada con prepararSentencia
	 */
	public void cerrarSentencia(){
		try {
			if (preparedStatement != null)
				preparedStatement.close();
		} catch (SQLException ex) {
			// Logger.getLogger(ConexionMySQL.class.getName()).log(Level.SEVERE, null, ex);
			JOptionPane.showMessageDialog(null, "Error al cerrar sentencia SQL: " + ex.getMessage());
		}
	} // cerrarSentencia
	
	
	
	/**
	 * Recibe un string con el comando SQL a ejecutar (update, insert, delete)
	 * y se ocupa de ejecutarlo. Devuelve true si pudo ejecutarlo
	 * @param sql es el string con la sentencia sql a ejecutar
	 * @return true si tuvo éxito
	 */
	public boolean sqlUpdate(String sql){
		preparedStatement = prepararSentencia(sql);
		if (preparedStatement == null) 
			return false;
		else {
			try {
				int retornoUpdate = preparedStatement.executeUpdate();
				if (retornoUpdate > 0) // un 1 o mayor es que fue exitoso
					return true;
				else
					return false;
			} catch (SQLException ex) {
				JOptionPane.showMessageDialog(null, "Error al ejecutar Update: " + ex.getMessage());
				return false;
			}
		}
	} //sqlUpdate
	
	
	/**
	 * Recibe un string con el comando SQL SELECT a ejecutar 
	 * y se ocupa de ejecutarlo. Devuelve el ResultSet con los resultados
	 * @param sql es el string con la sentencia sql select a ejecutar
	 * @return ResultSet con los resultados
	 */
	public ResultSet sqlSelect(String sql){ // retorna null si falló
		preparedStatement = prepararSentencia(sql);
		if (preparedStatement == null) 
			return null;
		else {
			try {
				ResultSet resultado = preparedStatement.executeQuery();
				return resultado;
			} catch (SQLException ex) {
				JOptionPane.showMessageDialog(null, "Error al ejecutar Query Select: " + ex.getMessage());
				return null;
			}
		}
	} //sqlSelect
	
	
	/**
	 * Devuelve el key generado en el último alta. Devuelve un -1 si no pudo hacer el alta.
	 * @return el key generado en el último alta
	 */
	public int getKeyGenerado(){ 
		ResultSet rs;
		try {
			rs = preparedStatement.getGeneratedKeys();
			if (rs.next()){ // si hay keys devuelvo el primero
				int keyGenerada = rs.getInt(1);
				rs.close();
				return(keyGenerada);
			}
			else {
				JOptionPane.showMessageDialog(null, "No se pudo obtener la key generada");
				return -1;
			}	
		} catch (SQLException ex) {
			//Logger.getLogger(ConexionMySQL.class.getName()).log(Level.SEVERE, null, ex);
				JOptionPane.showMessageDialog(null, "Error al obtener la key generada");
				return -1;
		}
	} // getKeyGenerado
	
} //ConexionMySQL
