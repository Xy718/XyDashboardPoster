package xyz.xy718.poster.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import lombok.Getter;
import xyz.xy718.poster.XyDashboardPosterPlugin;
import xyz.xy718.poster.config.XyDashboardPosterConfig;

public class Grafdata {
	@Getter long time;
	@Getter String measurement;
	@Getter Map<String, String> tagMap=new HashMap<>();
	@Getter Map<String, Object> fieldMap=new HashMap<>();
	
	
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
