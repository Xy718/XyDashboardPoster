package xyz.xy718.poster.model;

public class ServerTimeData extends Grafdata{

	public ServerTimeData(long total, long free, long max, long allocated,double percent, String measurement) {
		super(measurement);
		this.tagMap.put("type", "time");
		this.fieldMap.put("serveruptime", percent);
		this.fieldMap.put("jvmuptime", percent);
	}

	public ServerTimeData(String measurement) {
		super(measurement);
	}

	public void addData(String sUptime,String jUptime) {
		this.tagMap.put("type", "time");
		this.fieldMap.put("serveruptime", sUptime);
		this.fieldMap.put("jvmuptime", jUptime);
	}
	
	public String getServerTime() {
		return "服务器运行时长:"+this.fieldMap.get("serveruptime")
				+" JVM运行时长:"+this.fieldMap.get("jvmuptime");
	}
}
