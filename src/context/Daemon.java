package context;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class Daemon {

	public static void main(String[] args) throws IOException {
		System.out.println("Daemon up...");
		

		Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		
		// Get the current time
		Date x = calendar.getTime();
		System.out.println("x1: " + x);
		
		long seconds = System.currentTimeMillis() / 1000l;
		System.out.println("T: " + seconds);
		
		
		calendar.setTimeInMillis(seconds * 1000l);
		x = calendar.getTime();
		
		System.out.println("x2: " + x);
	
		
		
		System.out.println("Configuring...");
		
		reload();
		
		NamedPipeReader pipe_reader = null;
		
		try {
			pipe_reader = new NamedPipeReader("myfifo");
		} catch (Exception excp) {
			System.out.println("Não deu para abrir o pipe reader!");
		}
		
		System.out.println("Services up...");
		while (pipe_reader != null) {
			System.out.println("Waiting...");
			
			Message msg = pipe_reader.receive();
			
//			System.out.println("Message receive:");
//			System.out.println(msg);
			
			switch (msg.getType())
			{
			case START:
				break;

			case RESTART:
				//! Reload caches
				break;

			case FINISH:
				//! Close caches file on disk
				break;

			case DATA:
				
				new Thread() {
					public void run() {
						_cache_controller.updateData(msg.getSmartData());
					}
				}.start();
				
				break;

			case COMMAND:

				new Thread() {
					public void run() {
						_cache_controller.updateControl(msg.getSmartData());
					}
				}.start();
				
				break;

			default:
				System.out.println("Error on parsing the message!");
				break;
			}
		}
		
		System.out.println("Daemon exit");
	}
	
	public static void reload() {
		_cache_controller = new CacheController();
	}
	
	static private CacheController _cache_controller;

}
