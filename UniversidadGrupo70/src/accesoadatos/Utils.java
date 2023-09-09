/*
Rutinas utiles
 */
package accesoadatos;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

/**
 *
 * @author John David Molina
 */
public class Utils {
	
	public static void mensajeError(String mensaje){ // para mandar mensaje de erro con sout o JOptionPane
		System.out.println(mensaje);
	}
	
	public static void mensaje(String queMensaje){
		System.out.println(queMensaje);
	}
	
	public static Date localDate2Date(LocalDate localfecha){
		ZoneId defaultZoneId = ZoneId.systemDefault(); //default time zone
		// para pasar de LocalDate a Date: Date = local date + atStartOfDay() + default time zone + toInstant() 
		Date fecha = Date.from(localfecha.atStartOfDay(defaultZoneId).toInstant());
		return fecha;
	}
	
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
	}
}
