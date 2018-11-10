package context;

import weka.core.Instance;

public class LearningProcess implements Runnable
{
	static private MachineLearning _learning;
	static private CacheController _cache_controller;
	static private SynchronizedQueue _data_queue;
	private boolean stop = false;
	
	public LearningProcess(CacheController cache_controller, SynchronizedQueue data_queue) {
		super();
		
		_data_queue = data_queue;
		_cache_controller = cache_controller;
		_learning = new MachineLearning();
	}

	@Override
	public void run()
	{		
		synchronized (this)
		{
			while (!stop)
			{
				//! Bloqueia se não tiver mensagens para processar
				Message message = _data_queue.dequeue();

				try {
					//! Unica thread que irá executar update_data
					_cache_controller.update_data(message.getSmartData());
				}
				catch (Exception e) {
					e.printStackTrace();
				}
				
				//! Caso o tamanho da cache atinga 1000 instâncias,
				//! é executado o retreinamento da rede neural
				if(_cache_controller.current_instances().size() >= 1) {
					update_model();
				}
			}
		}
	}
	
	private synchronized void update_model()
	{
		//! Atualiza o modelo
		try {
			_learning.update(_cache_controller.current_instances());			
		} catch (Exception e) {
			System.out.println("LearningProcess: Error on periodic update");
			e.printStackTrace();
		}
		
		//! Reset current cache
		_cache_controller.persist_instances();
	}
	
	public Instance predict()
	{
		Instance context = _cache_controller.current_context();
		
		try {
			_learning.predict(context);
		}
		catch (Exception e) {
			System.out.println("LearningProcess: Error on predict");
			e.printStackTrace();
		}

		return context;
	}
	
	public void shutdown()
	{
		synchronized (this) {
			stop = true;
		}
	}
}
