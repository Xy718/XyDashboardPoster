package xyz.xy718.poster.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class Grafdata {
	String rfc3339_date_time_string;
	String measurement;
	Map<String, String> tagMap=new HashMap<>();
	Map<String, String> fieldMap=new HashMap<>();
	
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
		for(Entry<String, String> field:fieldMap.entrySet()) {
			retS+=field.getKey()+"="+field.getValue()+",";
		}
		retS=retS.substring(0,retS.length()-1);
		retS+=" '"+this.rfc3339_date_time_string+"'";
		return retS;
	}
}
