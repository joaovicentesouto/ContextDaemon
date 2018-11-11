package context;

public class ControlRunnable implements Runnable
{
//! ================== Attributes ==================

	static private LearningModel 	 _learning		   = null;
	static private CacheController 	 _cache_controller = null;
	static private SynchronizedQueue _control_queue    = null;
	
	private boolean stop = false;

//! ================== Constructor ==================

	public ControlRunnable(CacheController cache_controller, SynchronizedQueue control_queue, LearningModel learning) throws Exception {
		super();
		_cache_controller = cache_controller;
		_control_queue = control_queue;
		_learning = learning;
	}

//! ================== Main Function ==================

	@Override
	public void run()
	{
		setup();
		
		while (true)
		{
			//! Bloqueia se não tiver mensagens para processar
			Message message;

			try {
				message = _control_queue.dequeue();
			}
			catch (Exception e) {
				if (stop) //! Shutdown?
					break;

				e.printStackTrace();
				continue;
			}

			//! Shutdown?
			synchronized (this) {
				if (stop)
					break;
			}

			_cache_controller.update_control(message.getSmartData(), true);
			
			//! Se mandou comando é porque modelo errou!
			try {
				_learning.relearning(_cache_controller.persistente_instances());
			}
			catch (Exception e) {
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
