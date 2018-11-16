package context.component;

public class SmartData
{
//! ================== Attributes ==================	

	private String version;
	private long unit;
	private double value;
	private long error;
	private long confidence;
	private int x;
	private int y;
	private int z;
	private long t;
	private long dev;
	private String mac;

//! ================== Constructor ==================

	public SmartData(String version, long unit, double value, int x, int y, int z, long t, long error, long confidence, long dev, String mac) {
		super();
		this.version = version;
		this.unit = unit;
		this.value = value;
		this.x = x;
		this.y = y;
		this.z = z;
		this.t = t;
		this.error = error;
		this.confidence = confidence;
		this.dev = dev;
		this.mac = mac;
	}
	
	public SmartData(double value) {
		super();
		this.value = value;
	}

//! ================== Getters/Setters ==================

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}
	
	public long getUnit() {
		return unit;
	}

	public void setUnit(long unit) {
		this.unit = unit;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getZ() {
		return z;
	}

	public void setZ(int z) {
		this.z = z;
	}

	public long getT() {
		return t;
	}

	public void setT(long t) {
		this.t = t;
	}

	public long getError() {
		return error;
	}

	public void setError(long error) {
		this.error = error;
	}

	public long getConfidence() {
		return confidence;
	}

	public void setConfidence(long confidence) {
		this.confidence = confidence;
	}

	public long getDev() {
		return dev;
	}

	public void setDev(long dev) {
		this.dev = dev;
	}
	
	public String getMac() {
		return mac;
	}

	public void setMac(String mac) {
		this.mac = mac;
	}

//! ================== Auxiliary ==================

	@Override
	public String toString() {
		return "SmartData [unit=" + unit + ", value=" + value + ", x=" + x + ", y=" + y + ", z=" + z + ", t=" + t
				+ ", error=" + error + ", confidence=" + confidence + ", dev=" + dev + "mac=" + mac + "]";
	}
}