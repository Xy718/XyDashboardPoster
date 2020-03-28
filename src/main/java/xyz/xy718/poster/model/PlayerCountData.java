package xyz.xy718.poster.model;

import java.util.Map;

public class PlayerCountData extends Grafdata{

	public PlayerCountData(Map<String, Integer> worldP, int size, int max, String measurement) {
		super(measurement);
		this.tagMap.put("type", "p_count");
		worldP.forEach((name,count)->{
			this.fieldMap.put(name, count);
		});
		this.fieldMap.put("total", size);
		this.fieldMap.put("max", max);
	}
}
