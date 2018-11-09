package context;

public class LearningProcess implements Runnable
{
	private MachineLearning _learning;
	private CacheController _cache_controller;
	static private SynchronizedQueue _message_queue = new SynchronizedQueue();;
	private boolean stop = false;
	
	public LearningProcess(MachineLearning _learning, CacheController _cache_controller) {
		super();
		this._learning = _learning;
		this._cache_controller = _cache_controller;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
		while (!stop)
		{
			Message message = _message_queue.dequeue();
			
			
		}
	}

}
