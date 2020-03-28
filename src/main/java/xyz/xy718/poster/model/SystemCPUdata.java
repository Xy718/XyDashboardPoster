package xyz.xy718.poster.model;

public class SystemCPUdata extends Grafdata {

	public SystemCPUdata(String measurement, double[] cpus) {
		super(measurement);
		this.tagMap.put("type", "cpu");
		for(int i=0;i<cpus.length;i++) {
			this.fieldMap.put("cpu"+i, cpus[i]);
		}
	}
	
}
