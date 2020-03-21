package xyz.xy718.poster.model;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import lombok.Getter;

public class Grafdata {
	@Getter long time;
	@Getter String measurement;
	@Getter Map<String, String> tagMap=new LinkedHashMap<>();
	@Getter Map<String, Object> fieldMap=new HashMap<>();
	
	public Grafdata(String measurement) {
		this.measurement=measurement;
		this.time=System.currentTimeMillis();
	}
	/**
	 * 将收集的数据转换为influxDB写入字符串<br>
	 * 每个对象对应单个Point
	 * @return
	 */
	public String toInfluxDB() {
		String retS="";
		retS+=measurement;
		for(Entry<String, String> tag:tagMap.entrySet()) {
			retS+=","+tag.getKey()+"="+tag.getValue();
		}
		retS+=" ";      
		for(Entry<String, Object> field:fieldMap.entrySet()) {
			retS+=field.getKey()+"="+field.getValue()+",";
		}
		retS=retS.substring(0,retS.length()-1);
		retS+=" '"+this.time+"'";
		return retS;
	}
}
