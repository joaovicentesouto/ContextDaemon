package context;

public class Message {
	
	public enum Type {
		START, FINISH, RESTART, DATA, COMMAND
	}
	
	public Message(Type type, String data) {
		super();
		this.type = type;
		this.data = data;
	}
	
	public Type getType() {
		return type;
	}
	public void setType(Type type) {
		this.type = type;
	}
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}

	private Type type;
	private String data;
}
