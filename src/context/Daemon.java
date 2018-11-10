package context;

import java.io.BufferedWriter;
import java.io.FileWriter;

public class Daemon
{
	static private NamedPipeReader   _pipe_reader 	   = null;
	static private SynchronizedQueue _data_queue   	   = null;
	static private SynchronizedQueue _control_queue    = null;
	static private CacheController   _cache_controller = null;
	
	static private LearningProcess   _learning_process = null;
	static private ControlProcess    _control_process  = null;
	
	public static void main(String[] args) throws Exception
	{
		System.out.println("Initiating Daemon ...");

		services_up();
		
		System.out.println("Services executing ...");
		
		while (_pipe_reader != null)
		{
			System.out.println("Waiting requisitions ...");
			
			//! Blocking receive
			Message message = _pipe_reader.receive();
			
			switch (message.getType())
			{
			
			//! Do we need that option? 
			case START:
				break;

			case RESTART:
				services_down();
				services_up();
				break;

			case FINISH:
				services_down();
				break;

			case DATA:
				System.out.println("Recebimento de dados:");
				System.out.println("Mensagem: " + message.toString());
				
				_data_queue.enqueue(message);
				
				break;

			case COMMAND:
				System.out.println("Recebimento de commando:");
				System.out.println("Mensagem: " + message.toString());
				
				_control_queue.enqueue(message);
				
				break;
			
			case PREDICT:
				System.out.println("Solicitação de predição:");
				System.out.println("Mensagem: " + message.toString());

//				Instance context = _learning.predict(context);
//				System.out.println("Predict: " + context.toString());

				break;

			default:
				System.out.println("Error on parsing the message!");
				break;
			}
		}
		
		services_down();
		System.out.println("Daemon exiting ...");
	}
	
	static void services_up() throws Exception
	{
		//! Initiating
		try {
			_pipe_reader = new NamedPipeReader("myfifo");
		}
		
		catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Cannot open the pipe!");
		}
		
		_data_queue = new SynchronizedQueue();
		_control_queue = new SynchronizedQueue();
		
		//! Configuring
		BufferedWriter out = null;
		long pid = ProcessHandle.current().pid();
		
		try {
			out = new BufferedWriter(new FileWriter(".pid", false));
		    out.write((int) pid);
		}
		
		catch (Exception e) {
			e.printStackTrace();
		    throw new Exception("Cannot write .pid file!");
		}
		
		finally {
		        out.close();
		}
		
		//! Reaload
		_cache_controller = new CacheController();
		
		_learning_process = new LearningProcess(_cache_controller, _data_queue);
		_control_process = new ControlProcess(_cache_controller, _control_queue);
		
//		_learning_process.start();
//		_control_process.start();
	}
	
	static void services_down() throws Exception
	{
		if (_pipe_reader != null)
			_pipe_reader.close();
	}
}
