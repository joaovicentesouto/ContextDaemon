package context;

import java.io.BufferedWriter;
import java.io.FileWriter;

import weka.core.Instance;

public class Daemon
{
	static private NamedPipeReader   _pipe_reader 	   = null;
	static private SynchronizedQueue _data_queue   	   = null;
	static private SynchronizedQueue _control_queue    = null;
	static private CacheController   _cache_controller = null;
	
	static private LearningRunnable  _learning = null;
	static private ControlRunnable   _controlling  = null;
	
	static private Thread _learning_thread = null;
	static private Thread _control_thread  = null;
	
	public static void main(String[] args) throws Exception
	{
		System.out.println("Initiating Daemon ...");

		setup();
		
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
				shutdown();
				setup();
				break;

			case FINISH:
				shutdown();
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

				Instance context = _learning.predict();
				System.out.println("Predict: " + context.toString());

				break;

			default:
				System.out.println("Error on parsing the message!");
				break;
			}
		}
		
		shutdown();
		System.out.println("Daemon exiting ...");
	}
	
	static void setup() throws Exception
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
		
		//! Runnables 
		_learning = new LearningRunnable(_cache_controller, _data_queue);
		_controlling = new ControlRunnable(_cache_controller, _control_queue);
		
		_learning_thread = new Thread(_learning);
		_control_thread = new Thread(_controlling);
		
		_learning_thread.start();
		_control_thread.start();
	}
	
	static void shutdown() throws Exception
	{
		if (_pipe_reader != null)
			_pipe_reader.close();
		
		_learning.shutdown();
		_controlling.shutdown();
		
		_learning_thread.interrupt();
		_control_thread.interrupt();
		
		_learning_thread.join();
		_control_thread.join();
	}
}
