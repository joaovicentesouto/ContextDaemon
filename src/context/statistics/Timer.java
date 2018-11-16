package context.statistics;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Timer {
	private long count = 0, _max = 0;
	private BufferedWriter _performance = null;
	
	private long _t1 = 0, _t2 = 0;
	
	public Timer(String filename, long max_measurements) throws IOException {
		_performance = new BufferedWriter(new FileWriter(new File(filename)));
		_max = max_measurements;
	}
	
	public void start() {
		if (count <= _max)
			_t1 = System.nanoTime();
	}
	
	public void end() {
		if (count > _max)
			return;
		
		_t2 = System.nanoTime();
		
		try {
			_performance.write(Long.toString(_t2 - _t1) + "\n");
			_performance.flush();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
}
