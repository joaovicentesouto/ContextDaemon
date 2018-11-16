package context.runnable;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import context.cache.CacheController;
import context.learning.LearningModel;
import context.component.SynchronizedQueue;
import context.component.Message;;

public class ControlRunnable implements Runnable
{
//! ================== Attributes ==================

	static private LearningModel 	 _learning		   = null;
	static private CacheController 	 _cache_controller = null;
	static private SynchronizedQueue _control_queue    = null;
	private boolean stop = false;
	

	//! Measure performance file
	static private BufferedWriter _performance = null;

//! ================== Constructor ==================

	public ControlRunnable(CacheController cache_controller, SynchronizedQueue control_queue, LearningModel learning) throws Exception {
		super();
		_cache_controller = cache_controller;
		_control_queue = control_queue;
		_learning = learning;
		
		//! Performance
		_performance = new BufferedWriter(new FileWriter(new File("./measurements/controller.log")));
	}

//! ================== Main Function ==================

	@Override
	public void run()
	{
		setup();
		
		long t1, t2;
		
		while (!stop)
		{
			//! Bloqueia se não tiver mensagens para processar
			Message message;

			try {
				message = _control_queue.dequeue();
				
				//! Start measurements

				t1 = System.nanoTime();
			}
			catch (Exception e) {
				e.printStackTrace();
				continue;
			}

			//! Shutdown?
			synchronized (this) {
				if (stop) {
					//! Start measurements
					try {
						t2 = System.nanoTime();
						_performance.write(Long.toString(t2 -t1) + "\n");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					break;
				}
			}

			_cache_controller.update_control(message.getSmartData(), true);
			
			//! Nao precisa por enquanto (30 min de user mode)
//			//! Se mandou comando é porque modelo errou!
//			try {
//				_learning.relearning(_cache_controller.persistente_instances());
//			}
//			catch (Exception e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
			
			//! End measurements
			try {
				t2 = System.nanoTime();
				_performance.write(Long.toString(t2 - t1) + "\n");
				_performance.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		exiting();
	}

//! ================== Init/Finish ==================

	private void setup() {
		System.out.println(" * Initiating Control Thread ...");

		System.out.println(" * Control Thread running ...");
	}
	
	private void exiting() {
		System.out.println(" * Control Thread exiting ...");

		System.out.println(" * Control Thread says goodbye ...");
	}

//! ================== Getters/Setters ==================

	public void shutdown()
	{
		synchronized (this) {
			stop = true;
		}
	}
}
