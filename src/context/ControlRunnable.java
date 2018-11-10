package context;

public class ControlRunnable implements Runnable
{
	static private CacheController _cache_controller = null;
	static private SynchronizedQueue _control_queue = null;
	private boolean stop = false;

	public ControlRunnable(CacheController cache_controller, SynchronizedQueue control_queue) throws Exception {
		super();

		_cache_controller = cache_controller;
		_control_queue = control_queue;
	}

	@Override
	public void run()
	{	
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

			_cache_controller.update_control(message.getSmartData());
		}
	}

	public void shutdown()
	{
		synchronized (this) {
			stop = true;
		}
	}
}
