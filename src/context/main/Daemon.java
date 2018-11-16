package context.main;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.lang.management.ManagementFactory;

import context.cache.CacheController;
import context.comm.NamedPipeReader;
import context.comm.NamedPipeWriter;
import context.component.Message;
import context.component.SynchronizedQueue;
import context.runnable.ControlRunnable;
import context.runnable.LearningRunnable;
import context.statistics.Timer;
import weka.core.Instance;

public class Daemon
{

	public enum State {
		STOPPED   (0),
		ACTIVATED (1),
		IDLE 	  (2);
		
		private final int value;
		
	    public int getValue() {
	        return value;
	    }

	    private State(int value) {
	        this.value = value;
	    }
	}

	static private State _state = State.STOPPED;

	static private NamedPipeReader   _pipe_reader 	   = null;
	static private NamedPipeWriter   _pipe_writer 	   = null;
	static private SynchronizedQueue _data_queue   	   = null;
	static private SynchronizedQueue _control_queue    = null;
	static private CacheController   _cache_controller = null;
	
	static private LearningRunnable  _learning 		   = null;
	static private ControlRunnable   _controlling  	   = null;
	static private Thread 			 _learning_thread  = null;
	static private Thread 			 _control_thread   = null;
	
	//! Measure performance file
	static private Timer _stats = null;
	
	public static void main(String[] args) throws Exception {
		System.out.println("Initiating Daemon ...");

		setup();
		
		System.out.println("Services executing ...");
		
		while (_pipe_reader != null)
		{
			System.out.println("Waiting requisitions ...");
			
			//! Blocking receive
			Message message = _pipe_reader.receive();
			

			if (_state != State.ACTIVATED && message.getSmartData().getMac() == null) {
				System.out.println("User not detected. Nothing to do.");
				continue;
			}
			
//			|| (_state == State.IDLE && message.getType() != Message.Type.PREDICT) isso da problema eu acho porque o pipe vai ficar esperando a resposta
			
			//! Start measurements
			_stats.start();
			
			switch (message.getType())
			{
			
			//! Do we need that option? 
			case START:
				break;

			case RESTART:
				shutdown();
				setup();
				break;
				
			case STOP:
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


				Instance predict_context = _learning.predict();
				System.out.println("Temperatura ideal: " + predict_context.value(predict_context.numAttributes()-1));

				//! Need send a complex json
				_pipe_writer.send("{ \"temp_ideal\" : " + predict_context.value(predict_context.numAttributes()-1) + " }");

				break;
			
			case DISCOVERED:
				System.out.println("Usuário localizado:");
				System.out.println(message.toString());
				
				//! User detect
				if (_state != State.ACTIVATED) {
					Instance disc_context = _learning.predict();
					System.out.println("Temperatura ideal: " + disc_context.value(disc_context.numAttributes()-1));
					
					_state = State.ACTIVATED;
				}
				
				//! User left
				else
					_state = State.IDLE;			

				break;

			default:
				System.out.println("Error on parsing the message!");
				break;
			}
			
			//! End measurements
			_stats.end();
		}
		
		System.out.println("Daemon exiting ...");
	}
	
	static void setup() throws Exception
	{
		//! Initiating
		try {
			_pipe_reader = new NamedPipeReader(".input");
			_pipe_writer = new NamedPipeWriter(".output");
			
			//! Performance
			_stats = new Timer("./measurements/daemon.log", 100000);
		}
		
		catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Cannot open the pipe!");
		}
		
		_data_queue = new SynchronizedQueue();
		_control_queue = new SynchronizedQueue();
		
		//! Configuring (Save the pid in a file)
		BufferedWriter out = null;
		long pid = ManagementFactory.getRuntimeMXBean().getPid();
		
		try {
			out = new BufferedWriter(new FileWriter(".pid"));
		    out.write(Long.toString(pid));
			out.flush();
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
		_controlling = new ControlRunnable(_cache_controller, _control_queue, _learning.learning());
		
		_learning_thread = new Thread(_learning);
		_control_thread = new Thread(_controlling);
		
		_learning_thread.start();
		_control_thread.start();

		_state = State.IDLE;
	}
	
	static void shutdown() throws Exception
	{	
		_state = State.STOPPED;

		_pipe_reader = null;
		_pipe_writer = null;
		
		_learning.shutdown();
		_controlling.shutdown();
		
		_learning_thread.interrupt();
		_control_thread.interrupt();
		
		_learning_thread.join();
		_control_thread.join();
	}
}
