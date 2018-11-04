package context;

import java.io.BufferedWriter;
import java.io.FileWriter;

import weka.core.Instance;

public class Daemon {
	
	static private MachineLearning _learning;
	static private CacheController _cache_controller;
	static private NamedPipeReader _pipe_reader;

	public static void main(String[] args) throws Exception
	{
		System.out.println("Daemon up ...");		
		
		System.out.println("Configuring ...");
		configure();
		
		_pipe_reader = null;
		
		try {
			_pipe_reader = new NamedPipeReader("myfifo");
		} catch (Exception excp) {
			System.out.println("Cannot open the pipe!");
		}
		
		System.out.println("Services up ...");
		
		while (_pipe_reader != null)
		{
			System.out.println("Waiting requisitions ...");
			
			Message msg = _pipe_reader.receive();
			
			switch (msg.getType())
			{
			case START:
				break;

			case RESTART:
				//! Recarrega a cache
				reload();
				break;

			case FINISH:
				//! Fecha o pipe
				_pipe_reader.close();
				break;

			case DATA:
				
				//! Executa a atualização da cache em paralelo
				new Thread() {
					public void run()
					{
						try {
							_cache_controller.updateData(msg.getSmartData());
						} catch (Exception e) {
							System.out.println("Data cache error: " + e.getMessage());
						}
					}
				}.start();
				
				break;

			case COMMAND:
				
				//! Executa a atualização da cache em paralelo
				new Thread() {
					public void run()
					{
						try {
							_cache_controller.updateControl(msg.getSmartData());
						} catch (Exception e) {
							System.out.println("Control cache error: " + e.getMessage());
						}
					}
				}.start();
				
				break;
			
			case PREDICT:

				new Thread() {
					public void run()
					{
						try {
							Instance context = _cache_controller.current_context();
							_learning.predict(context);
							System.out.println("Predict: " + context.toString());
						} catch (Exception e) {
							System.out.println("Predict cache error: " + e.getMessage());
						}
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
	
	public static void configure() throws Exception
	{		
		BufferedWriter out = null;
		long pid = ProcessHandle.current().pid();
		
		try {
			out = new BufferedWriter(new FileWriter(".pid", false));
		    out.write((int) pid);
		}
		catch (Exception e) {
		    System.err.println("Error: " + e.getMessage());
		}
		finally {
		    if(out != null) {
		        out.close();
		    }
		}
		
		reload();
	}
	
	public static void reload()
	{	
		try {
			_learning = new MachineLearning();
			_cache_controller = new CacheController(_learning);
		} catch (Exception e) {
			System.out.println("Reload error: " + e.getMessage());
		}
	}
}
