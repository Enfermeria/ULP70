/*
	Trabajo práctico trasversal de la Guía 5 del curso Desarrollo de Apps
	Universidad de La Punta en el marco del proyecto Argentina Programa 4.0

	Integrantes:
		John David Molina Velarde
		Leticia Mores
		Enrique Germán Martínez
		Carlos Eduardo Beltrán

	Rutinas utiles
 */
package accesoadatos;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import javax.swing.JOptionPane;

/**
 *
 * @author John David Molina Velarde, Leticia Mores, Enrique Germán Martínez, Carlos Eduardo Beltrán
 */
public class Utils {
	/**
	 * Para mandar un mensaje de error con sout o JOptionPane
	 * @param mensaje 
	 */
	public static void mensajeError(String mensaje){ 
		//System.out.println(mensaje);
		JOptionPane.showMessageDialog(null, mensaje);
	} //mensajeError
	
	
	
	
	/**
	 * Para mandar un mensaje con sout o JOptionPane
	 * @param queMensaje 
	 */
	public static void mensaje(String queMensaje){
		System.out.println(queMensaje);
	} //mensaje
	
	
	
	
	/**
	 * Conversor de formato LocalDate a Date.
	 * Dado un LocalDate devuelve el correspondiente Date
	 * @param localfecha el localDate
	 * @return el Date correspondiente
	 */
	public static Date localDate2Date(LocalDate localfecha){
		ZoneId defaultZoneId = ZoneId.systemDefault(); //default time zone
		// para pasar de LocalDate a Date: Date = local date + atStartOfDay() + default time zone + toInstant() 
		Date fecha = Date.from(localfecha.atStartOfDay(defaultZoneId).toInstant());
		return fecha;
	} // localDate2Date
	
	
	
	
	/**
	 * Conversor de formato Date a  LocalDate.
	 * Dado un Date devuelve el correspondiente LocalDate
	 * @param fecha el Date
	 * @return el LocalDate correspondiente
	 */
	public static LocalDate date2LocalDate(Date fecha){
		//The java.util.Date represents date, time of the day in UTC timezone 
		//		java.util.Date = date + time of the day + UTC time zone
		//and java.time.LocalDate represents only the date, without time and timezone.
		//		java.time.LocalDate = only date
		
		//How to convert Date to LocalDate. 
		//Keeping these points in mind, we can convert the Date to Local in the following steps:
		// 1. Convert Date to Instant – because we do not want the time in the LocalDate
		// 2. Get the default timezone – because there is no timezone in LocalDate
		// 3. Convert the date to local date – Instant + default time zone + toLocalDate() = LocalDate
		
		//Getting the default zone id
		ZoneId defaultZoneId = ZoneId.systemDefault();

		//Converting the date to Instant
		Instant instant = fecha.toInstant();

		//Converting the Date to LocalDate
		LocalDate localDate = instant.atZone(defaultZoneId).toLocalDate();
		return localDate;
	} //date2LocalDate
}
