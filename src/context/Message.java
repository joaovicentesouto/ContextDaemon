package context;

import com.google.gson.annotations.SerializedName;

public class Message
{
	
	public enum Type {
		@SerializedName("0")
		START (0),
		
		@SerializedName("1")
		RESTART (1),
		
		@SerializedName("2")
		STOP (2),
		
		@SerializedName("3")
		DATA (3),
		
		@SerializedName("4")
		COMMAND (4),
		
		@SerializedName("5")
		PREDICT (5);
		
		private final int value;
		
	    public int getValue() {
	        return value;
	    }

	    private Type(int value) {
	        this.value = value;
	    }
	}
	
	public Message(Type type, SmartData data) {
		super();
		this.type = type;
		this.smartdata = data;
	}
	
	public Type getType() {
		return type;
	}
	public void setType(Type type) {
		this.type = type;
	}
	public SmartData getSmartData() {
		return smartdata;
	}
	public void setSmartData(SmartData data) {
		this.smartdata = data;
	}

	@Override
	public String toString() {
		return "Message [type=" + type + ", smartdata=" + smartdata.toString() + "]";
	}

	private Type type;
	private SmartData smartdata;
}
