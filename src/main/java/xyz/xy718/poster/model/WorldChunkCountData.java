package xyz.xy718.poster.model;

import java.util.UUID;

public class WorldChunkCountData extends Grafdata{

	public WorldChunkCountData(UUID wUuid,String wName,int cCount, String measurement) {
		super(measurement);
		this.tagMap.put("type", "chunk_count");
		this.tagMap.put("world_name", wName);
		this.tagMap.put("world_UUID", wUuid.toString());
		this.fieldMap.put("chunk_count", cCount);
	}

	public String getWorldName() {
		return	this.tagMap.get("world_name");
	}

	public String getChunkCount() {
		return	this.fieldMap.get("chunk_count")+"";
	}
}
