package context;

import java.io.IOException;

public class Daemon {

	public static void main(String[] args) throws IOException {
		System.out.println("Daemon up...");
		
		
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
						_data.update(msg.getSmartData());
					}
				}.start();
				
				break;

			case COMMAND:

				new Thread() {
					public void run() {
						_control.update(msg.getSmartData());
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
		_data = new DataCacheController();
		_control = new ControlCacheController();
	}
	
	static private CacheController _data;
	static private CacheController _control;

}
