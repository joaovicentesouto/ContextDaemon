package context;

import java.util.Calendar;
import java.util.TimeZone;

import weka.core.Instance;
import weka.core.Instances;

public class LearningRunnable implements Runnable
{
//! ================== Attributes ==================
	
	static private LearningModel     _learning;
	static private CacheController 	 _cache_controller;
	static private SynchronizedQueue _data_queue;
	private boolean stop = false;

	static private Thread _worker = null;
	static private Thread _watchmaker = null;
	
//! ================== Constructor ==================

	public LearningRunnable(CacheController cache_controller, SynchronizedQueue data_queue) {
		super();

		_data_queue = data_queue;
		_cache_controller = cache_controller;
		
		try {
			_learning = new SGDModel(_cache_controller.persistente_instances());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
				message = _data_queue.dequeue();
			}
			catch (Exception e) {
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
	}
	
//! ================== Learning Functions ==================

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
					_learning.update(instances);
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
			_learning.predict(context);
		}
		catch (Exception e) {
			System.out.println("LearningRunnable: Error on predict");
			e.printStackTrace();
			
			context.setValue(context.numAttributes()-1, -127);
		}

		return context;
	}

//! ================== Init/Finish ==================
		
	private void setup()
	{
		System.out.println(" + Initiating Learning Thread ...");
		
		// ! Update model
		update_model(_cache_controller.persistente_instances());

		_watchmaker = new Thread() {
			private Calendar _calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));

			public void run() {
				while (!stop) {
					try {
						Thread.sleep(30000); // ! 30 segundos

						// ! 30 minutos desde o último controle?
						if (_calendar.getTimeInMillis() - _cache_controller.last_command_time() > 1.8e+6) {
							Instance c = predict();
							SmartData command = new SmartData(c.value(c.numAttributes() - 1));
							_cache_controller.update_control(command, false);
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		};

		_watchmaker.start();
		
		System.out.println(" + Learning Thread running ...");
	}

	private void exiting()
	{
		System.out.println(" + Learning Thread exiting ...");
		
		//! Reset current cache
		_cache_controller.persist_instances();
		
		try {
			_watchmaker.join();
			
			if (_worker != null)
				_worker.join();
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		System.out.println(" + Learning Thread says goodbye ...");
	}
	
//! ================== Getters/Setters ==================
	
	public LearningModel learning() {
		return _learning;
	}
	
	public void shutdown()
	{
		synchronized (this) {
			stop = true;
		}
	}
}
