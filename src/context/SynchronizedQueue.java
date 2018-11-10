package context;

import java.util.LinkedList;
import java.util.Queue;

public class SynchronizedQueue
{	
	Queue<Message> _queue = new LinkedList<Message>();
	
	void enqueue(Message message)
	{
		synchronized (this)
		{
			_queue.add(message);
			
			notify(); 
		}
	}
	
	Message dequeue() throws InterruptedException
	{
		synchronized (this)
		{
			//! Needs wait?
            if (_queue.isEmpty()) {
            	wait();
            }
            	
            return _queue.remove();
		}
	}
}
