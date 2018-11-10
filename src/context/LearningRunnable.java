package context;

import weka.core.Instance;
import weka.core.Instances;

public class LearningRunnable implements Runnable
{
	static private MachineLearning _learning;
	static private CacheController _cache_controller;
	static private SynchronizedQueue _data_queue;
	private boolean stop = false;

	static private Thread _worker = null;

	public LearningRunnable(CacheController cache_controller, SynchronizedQueue data_queue) {
		super();

		_data_queue = data_queue;
		_cache_controller = cache_controller;
		_learning = new MachineLearning();
	}

	@Override
	public void run()
	{
		System.out.println(" + Initiating Learning Thread ...");

		//! Update model
		update_model(_cache_controller.persistente_instances());

		while (true)
		{
			//! Bloqueia se não tiver mensagens para processar
			Message message;

			try {
				message = _data_queue.dequeue();
			}
			catch (Exception e) {
				if (stop)	//! Shutdown?
					break;

				e.printStackTrace();
				continue;
			}

			//! Shutdown?
			synchronized (this) {
				if (stop)
					break;
			}

			_cache_controller.update_data(message.getSmartData());

			//! Caso o tamanho da cache atinga 1000 instâncias,
			//! é executado o retreinamento da rede neural
			if(_cache_controller.current_size() >= 1000) {
				update_model(_cache_controller.current_instances());
				_cache_controller.persist_instances();
			}
		}

		exiting();
		System.out.println(" + Learning Thread exiting ...");
	}

	private void update_model(Instances instances)
	{
		if (_worker != null) {
			try {
				_worker.join();
			}
			catch (Exception e) {
				System.err.println("LearningRunnable: Error join on worker");
			}
		}

		//! Not necessary wait for model update
		_worker = new Thread()
		{
			public void run()
			{
				try {
					synchronized (_learning)
					{
						_learning.update(instances);
					}
				}
				catch (Exception e) {
					System.err.println("LearningRunnable: Error on periodic update");
					e.printStackTrace();
				}
			}
		};

		_worker.start();
	}

	public Instance predict()
	{
		Instance context = _cache_controller.current_context();

		try {
			synchronized (_learning)
			{
				_learning.predict(context);
			}
		}
		catch (Exception e) {
			System.out.println("LearningRunnable: Error on predict");
			e.printStackTrace();
			
			context.setValue(context.numAttributes()-1, -127);
		}

		return context;
	}

	public void shutdown()
	{
		synchronized (this) {
			stop = true;
		}
	}

	private void exiting() {
		//! Reset current cache
		_cache_controller.persist_instances();
	}
}
