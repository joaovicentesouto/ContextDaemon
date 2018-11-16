package context.runnable;

import context.cache.CacheController;
import context.learning.LearningModel;
import context.statistics.Timer;
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
	private Timer _stats = null;

//! ================== Constructor ==================

	public ControlRunnable(CacheController cache_controller, SynchronizedQueue control_queue, LearningModel learning) throws Exception {
		super();
		_cache_controller = cache_controller;
		_control_queue = control_queue;
		_learning = learning;
		
		//! Performance
		_stats = new Timer("./measurements/controller.log", 100000);
	}

//! ================== Main Function ==================

	@Override
	public void run()
	{
		setup();

		
		while (!stop)
		{
			//! Bloqueia se não tiver mensagens para processar
			Message message;

			try {
				message = _control_queue.dequeue();
				
				//! Start measurements
				_stats.start();
			}
			catch (Exception e) {
				e.printStackTrace();
				continue;
			}

			//! Shutdown?
			synchronized (this) {
				if (stop) {
					_stats.end();
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
			_stats.end();
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
