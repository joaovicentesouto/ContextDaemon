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
	
	Message dequeue()
	{
		synchronized (this)
		{
			//! Needs wait?
            if (_queue.isEmpty())
            {
            	try {
            		wait();
            	}
            	catch (Exception e) {
            		e.printStackTrace();
            		
            		//! Try again
            		return dequeue();
            	}
            }
            	
            return _queue.remove();
		}
	}
}
