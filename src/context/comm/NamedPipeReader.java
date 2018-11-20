package context.comm;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import context.component.Message;

import com.google.gson.Gson;

public class NamedPipeReader
{	
	private BufferedReader _pipe_reader;
	private String filename;
	
	public NamedPipeReader(String filename) throws IOException, InterruptedException {
		super();
		
		this.filename = filename;
		
		String[] cmds = {"/bin/sh", "-c", "mkfifo " + filename};
		
		try {
			Process p = Runtime.getRuntime().exec(cmds);
			p.waitFor();
		} catch (Exception e) {
			File file = new File(filename);
			file.delete();

			Process p = Runtime.getRuntime().exec(cmds);
			p.waitFor();

			System.out.println("File exist: old pipe deleted and new created!");
		}
	}
	
	public Message receive() throws IOException {
		Gson gson = new Gson();
		
		_pipe_reader = new BufferedReader(new FileReader(new File(filename)));
		
		String msg = _pipe_reader.readLine();
		
		_pipe_reader.close();

		return gson.fromJson(msg, Message.class);
	}
}
