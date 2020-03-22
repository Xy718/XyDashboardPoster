package xyz.xy718.poster.model;

public class ServerMemoryData extends Grafdata{

	public ServerMemoryData(long total, long free, long max, long allocated,double percent, String measurement) {
		super(measurement);
		this.tagMap.put("type", "memory");
		this.fieldMap.put("memory_percent", percent);
		this.fieldMap.put("memory_total", total);
		this.fieldMap.put("memory_allocated", allocated);
		this.fieldMap.put("memory_free", free);
		this.fieldMap.put("memory_max", max);
	}

	public String getMemoryMSG() {
		return 
				"已使用:"+this.fieldMap.get("memory_total")
				+"已分配"+this.fieldMap.get("memory_allocated")
				+"最高:"+this.fieldMap.get("memory_max")
				+"占比:"+this.fieldMap.get("memory_percent");
	}
}
