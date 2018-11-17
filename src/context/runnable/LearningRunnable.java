package context.runnable;

import java.util.Calendar;
import java.util.TimeZone;

import context.cache.CacheController;
import context.learning.LearningModel;
import context.learning.SGDModel;
import context.statistics.Timer;
import context.component.*;
import weka.core.Instance;
import weka.core.Instances;

public class LearningRunnable implements Runnable
{
//! ================== Attributes ==================
	
	private static LearningModel     _learning;
	private static CacheController 	 _cache_controller;
	private static SynchronizedQueue _data_queue;
	private boolean _stop = false;

	private static Thread _worker = null;
	private static Thread _watchmaker = null;

	//! Measure performance file
	private Timer _stats = null;
	
//! ================== Constructor ==================

	public LearningRunnable(CacheController cache_controller, SynchronizedQueue data_queue) throws Exception {
		super();

		_data_queue = data_queue;
		_cache_controller = cache_controller;
		
		_learning = new SGDModel(_cache_controller.persistente_instances());
		
		_stats = new Timer("./measurements/learning.log", 100000);
	}
	
//! ================== Main Function ==================

	@Override
	public void run() {		
		setup();

		while (!_stop)
		{
			//! Bloqueia se não tiver mensagens para processar
			Message message;

			try {
				message = _data_queue.dequeue();
				
				//! Start measurements
				_stats.start();
			}
			catch (Exception e) {
				e.printStackTrace();
				continue;
			}

			//! Shutdown?
			synchronized (this) {
				if (_stop) {
					_stats.end();
					break;
				}
			}

			_cache_controller.update_data(message.getSmartData());
			
			//! Caso o tamanho da cache atinga 60 instâncias (30 min de instancias),
			//! é executado o retreinamento da rede neural
			if(_cache_controller.current_size() >= 60) {
				update_model(_cache_controller.current_instances());
				_cache_controller.persist_instances();
			}
			
			//! End measurements
			_stats.end();
		}
		
		exiting();
	}
	
//! ================== Learning Functions ==================

	private void update_model(Instances instances) {
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
			public void run() {
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

	public Instance predict() {
		Instance context = _cache_controller.current_context();
		
		if (_cache_controller.user_mode())
			return context;

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
		
	private void setup() {
		System.out.println(" + Initiating Learning Thread ...");
		
		// ! Update model
		update_model(_cache_controller.persistente_instances());
		
		_watchmaker = new Thread() {
			public void run() {
				while (!_stop) {
					try {
						Thread.sleep(30000); // ! 30 segundos (melhoria fazendo dormir o que for preciso até 30min)

						// ! 30 minutos desde o último controle 
						if ((System.currentTimeMillis() - _cache_controller.last_command_time())/1000L > 1800) {
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

	private void exiting() {
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
	
	public void shutdown() {
		synchronized (this) {
			_stop = true;
		}
	}
}
