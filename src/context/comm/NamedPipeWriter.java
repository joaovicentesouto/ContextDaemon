package context.comm;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

//import com.google.gson.Gson;

public class NamedPipeWriter
{
	private BufferedWriter _pipe_writer;
	private String filename;
	
	public NamedPipeWriter(String filename) throws IOException {
		super();
		this.filename = filename;
	}
	
	public void send(String msg) throws IOException {
//		Gson gson = new Gson();
		_pipe_writer = new BufferedWriter(new FileWriter(new File(filename)));
		_pipe_writer.write(msg);
		_pipe_writer.flush();
		_pipe_writer.close();
	}
}
