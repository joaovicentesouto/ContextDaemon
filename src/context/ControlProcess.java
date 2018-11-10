package context;

public class ControlProcess implements Runnable
{
	static private CacheController _cache_controller = null;
	static private SynchronizedQueue _control_queue = null;
	private boolean stop = false;
	
	public ControlProcess(CacheController cache_controller, SynchronizedQueue control_queue) throws Exception {
		super();
		
		_cache_controller = cache_controller;
		_control_queue = control_queue;
	}

	@Override
	public void run()
	{	
		synchronized (this)
		{
			while (!stop)
			{
				//! Bloqueia se não tiver mensagens para processar
				Message message = _control_queue.dequeue();

				try {
					//! Unica thread que irá executar update_data
					_cache_controller.update_control(message.getSmartData());
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public void shutdown()
	{
		synchronized (this) {
			stop = true;
		}
	}
}
