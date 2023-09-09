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
public class Materia {
	private int idmateria;
	private String nombre;
	private int anio;
	private boolean estado;

	public Materia() {
	}

	public Materia(String nombre, int anio, boolean estado) {
		this.nombre = nombre;
		this.anio = anio;
		this.estado = estado;
	}

	public Materia(int idmateria, String nombre, int anio, boolean estado) {
		this.idmateria = idmateria;
		this.nombre = nombre;
		this.anio = anio;
		this.estado = estado;
	}

	public int getIdmateria() {
		return idmateria;
	}

	public void setIdmateria(int idmateria) {
		this.idmateria = idmateria;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public int getAnio() {
		return anio;
	}

	public void setAnio(int anio) {
		this.anio = anio;
	}

	public boolean getEstado() {
		return estado;
	}

	public void setEstado(boolean estado) {
		this.estado = estado;
	}

	@Override
	public String toString() {
		return "idmateria=" + idmateria + ", nombre=" + nombre + ", año=" + anio + ", estado=" + estado;
	}
	
	
}
