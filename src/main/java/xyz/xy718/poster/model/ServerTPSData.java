package xyz.xy718.poster.model;

public class ServerTPSData extends Grafdata{

	public ServerTPSData(double ticksPerSecond, String measurement) {
		super(measurement);
		this.tagMap.put("type", "tps");
		this.fieldMap.put("tps", ticksPerSecond);
	}

	public String getServerTPS() {
		return	this.fieldMap.get("tps")+"";
	}
}
