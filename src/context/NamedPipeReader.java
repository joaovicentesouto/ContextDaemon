package context;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import com.google.gson.Gson;

public class NamedPipeReader {
	
	public NamedPipeReader(String filename) throws IOException, InterruptedException {
		super();
		
		this.filename = filename;
		
		String[] cmds = {"/bin/sh", "-c", "mkfifo " + filename};
		
		try {
			Process p = Runtime.getRuntime().exec(cmds);
			p.waitFor();
		} catch (Exception e) {
			System.out.println("File exist: pipe reader!");
		}
	}
	
	public Message receive() throws IOException {
		Gson gson = new Gson();

		_pipe_reader = new BufferedReader(new FileReader(new File(filename)));
		String msg = _pipe_reader.readLine();
		_pipe_reader.close();
		
		return gson.fromJson(msg, Message.class);
	}

	private BufferedReader _pipe_reader;
	private String filename;
}
