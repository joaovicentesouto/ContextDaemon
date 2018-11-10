package context;

//! Normalize between [-1, 1] = y = 2 * (x - min) / (max - min) - 1
//! 						  = x =	(y + 1) * (max - min) / 2 + min
public class Normalizer {
	
	enum Type {
		TEMPERATURE, HUMIDITY, MINUTE, HOUR, WEEK_DAY
	}
	
	//! Temperature
	static double min_temp = -20;
	static double max_temp = 50;
	
	//! Humidity
	static double min_hum = -20;
	static double max_hum = 50;
	
	//! Minute
	static double min_minute = 0;
	static double max_minute = 59;
		
	//! Hour
	static double min_hour = 0;
	static double max_hour = 23;
	
	//! Week day
	static double min_week_day = 0;
	static double max_week_day = 7;
	
	static private double generic_normalize(double x, double min, double max) {
		return 2 * (x - min) / (max - min) - 1;
	}
	
	static private double generic_denormalize(double y, double min, double max) {
		return (y + 1) * (max - min) / 2 + min;
	}
	
	static public double normalize(Type type, double x)
	{
		switch (type) {
		case TEMPERATURE:
			return generic_normalize(x, min_temp, max_temp);
		case HUMIDITY:
			return generic_normalize(x, min_hum, max_hum);
		case MINUTE:
			return generic_normalize(x, min_minute, max_minute);
		case HOUR:
			return generic_normalize(x, min_hour, max_hour);
		case WEEK_DAY:
			return generic_normalize(x, min_week_day, max_week_day);
		default:
			return 0.0;
		}
	}
	
	static public double denormalize(Type type, double y)
	{
		switch (type) {
		case TEMPERATURE:
			return generic_denormalize(y, min_temp, max_temp);
		case HUMIDITY:
			return generic_denormalize(y, min_hum, max_hum);
		case MINUTE:
			return generic_denormalize(y, min_minute, max_minute);
		case HOUR:
			return generic_denormalize(y, min_hour, max_hour);
		case WEEK_DAY:
			return generic_denormalize(y, min_week_day, max_week_day);
		default:
			return 0.0;
		}
	}
}
