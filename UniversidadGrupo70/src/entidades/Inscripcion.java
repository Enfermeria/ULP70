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
public class Inscripcion {
	private int idinscripcion;
	private double nota;
	private Alumno alumno;
	private Materia materia;

	public Inscripcion() {
	}

	public Inscripcion(double nota, Alumno alumno, Materia materia) {
		this.nota = nota;
		this.alumno = alumno;
		this.materia = materia;
	}

	public Inscripcion(int idinscripcion, double nota, Alumno alumno, Materia materia) {
		this.idinscripcion = idinscripcion;
		this.nota = nota;
		this.alumno = alumno;
		this.materia = materia;
	}

	public int getIdinscripcion() {
		return idinscripcion;
	}

	public double getNota() {
		return nota;
	}

	public Alumno getAlumno() {
		return alumno;
	}

	public Materia getMateria() {
		return materia;
	}

	public void setIdinscripcion(int idinscripcion) {
		this.idinscripcion = idinscripcion;
	}

	public void setNota(double nota) {
		this.nota = nota;
	}

	public void setAlumno(Alumno alumno) {
		this.alumno = alumno;
	}

	public void setMateria(Materia materia) {
		this.materia = materia;
	}

	@Override
	public String toString() {
		return "idinscripcion=" + idinscripcion + ", nota=" + nota + ", alumno=(" + alumno + "), materia=(" + materia + '}';
	}
	
	
}
