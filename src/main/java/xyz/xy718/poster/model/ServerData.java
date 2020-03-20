package xyz.xy718.poster.model;

public class ServerData extends Grafdata{

	public ServerData(double ticksPerSecond, String measurement) {
		this.measurement=measurement;
		this.tagMap.put("type", "tps");
		this.fieldMap.put("tps", ticksPerSecond);
		this.time=System.currentTimeMillis();
	}

	public String getServerTPS() {
		return	this.fieldMap.get("tps")+"";
	}
}
