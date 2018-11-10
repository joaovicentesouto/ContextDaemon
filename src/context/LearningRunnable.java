package context;

import weka.core.Instance;
import weka.core.Instances;

public class LearningRunnable implements Runnable
{
	static private MachineLearning _learning;
	static private CacheController _cache_controller;
	static private SynchronizedQueue _data_queue;
	private boolean stop = false;
	
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
			if(_cache_controller.current_instances().size() >= 1000) {
				update_model(_cache_controller.current_instances());
				_cache_controller.persist_instances();
			}
		}
		
		exiting();
		System.out.println(" + Learning Thread exiting ...");
	}
	
	private synchronized void update_model(Instances instances)
	{
		//! Atualiza o modelo
		try {
			_learning.update(new Instances(instances));			
		} catch (Exception e) {
			System.out.println("LearningRunnable: Error on periodic update");
			e.printStackTrace();
		}
	}
	
	public Instance predict()
	{
		Instance context = _cache_controller.current_context();
		
		try {
			_learning.predict(context);
		}
		catch (Exception e) {
			System.out.println("LearningRunnable: Error on predict");
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
	
	private void exiting() {
		//! Reset current cache
		_cache_controller.persist_instances();
	}
}
