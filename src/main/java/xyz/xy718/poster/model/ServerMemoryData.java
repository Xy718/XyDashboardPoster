package xyz.xy718.poster.model;

public class ServerMemoryData extends Grafdata{

	public ServerMemoryData(long total, long free, long max, double percent, String measurement) {
		super(measurement);
		this.tagMap.put("type", "memory");
		this.fieldMap.put("memory_percent", percent);
		this.fieldMap.put("memory_total", total);
		this.fieldMap.put("memory_free", free);
		this.fieldMap.put("memory_max", max);
	}

	public String getServerTPS() {
		return	this.fieldMap.get("tps")+"";
	}
}
