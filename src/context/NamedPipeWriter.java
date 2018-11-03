package context;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

//import com.google.gson.Gson;

public class NamedPipeWriter {
	
	
	public NamedPipeWriter(String filename) throws IOException {
		super();
		_pipe_writer = new BufferedWriter(new FileWriter(new File(filename)));
	}
	
	public void send(String msg) throws IOException {
//		Gson gson = new Gson();
		_pipe_writer.write(msg);
	}
	
	private BufferedWriter _pipe_writer;
}
