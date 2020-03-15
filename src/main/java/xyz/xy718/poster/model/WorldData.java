package xyz.xy718.poster.model;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import lombok.Data;

public class WorldData extends Grafdata{

	public WorldData(UUID wUuid,String wName,int cCount) {
		this.measurement="world";
		this.tagMap.put("world_name", wName);
		this.tagMap.put("world_UUID", wUuid.toString());
		this.fieldMap.put("chunk_count", cCount+"");
		Date d=new Date();
		//this.rfc3339_date_time_string=new SimpleDateFormat("yyyy-MM-dd").format(d)+"T"+new SimpleDateFormat("HH:mm:ss").format(d)+"Z";
		this.rfc3339_date_time_string=System.currentTimeMillis()+"";
	}

	public String getWorldName() {
		return	this.tagMap.get("world_name");
	}

	public String getChunkCount() {
		return	this.fieldMap.get("chunk_count");
	}
}
