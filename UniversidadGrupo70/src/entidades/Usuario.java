/*
	Trabajo práctico trasversal de la Guía 5 del curso Desarrollo de Apps
	Universidad de La Punta en el marco del proyecto Argentina Programa 4.0

	Integrantes:
		John David Molina Velarde
		Leticia Mores
		Enrique Germán Martínez
		Carlos Eduardo Beltrán
 */
package entidades;

/**
 *
 * @author John David Molina Velarde, Leticia Mores, Enrique Germán Martínez, Carlos Eduardo Beltrán
 */
public class Usuario {
	private int idusuario;
	private String nombre;
	private String apellido;
	private int dni;

	public Usuario() {
	}

	public Usuario(String nombre, String apellido, int dni) {
		this.dni = dni;
		this.apellido = apellido;
		this.nombre = nombre;
	}

	public Usuario(int idusuario, String nombre, String apellido, int dni) {
		this.idusuario = idusuario;
		this.dni = dni;
		this.apellido = apellido;
		this.nombre = nombre;
	}

	public int getIdusuario() {
		return idusuario;
	}

	public void setIdusuario(int idusuario) {
		this.idusuario = idusuario;
	}

	public int getDni() {
		return dni;
	}

	public void setDni(int dni) {
		this.dni = dni;
	}

	public String getApellido() {
		return apellido;
	}

	public void setApellido(String apellido) {
		this.apellido = apellido;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	@Override
	public String toString() {
		return "idusuario=" + idusuario + ", dni=" + dni + ", " + apellido + " " + nombre;
	}
	
	
	
}
