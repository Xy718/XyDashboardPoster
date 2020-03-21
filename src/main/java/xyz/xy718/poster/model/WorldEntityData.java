package xyz.xy718.poster.model;

import java.util.UUID;

public class WorldEntityData extends Grafdata{

	public WorldEntityData(UUID wUuid,String wName,int cCount, String measurement) {
		super(measurement);
		this.tagMap.put("type", "entity_count");
		this.tagMap.put("world_name", wName);
		this.tagMap.put("world_UUID", wUuid.toString());
		this.fieldMap.put("entity_count", cCount);
	}

	public String getWorldName() {
		return	this.tagMap.get("world_name");
	}

	public String getEntityCount() {
		return	this.fieldMap.get("entity_count")+"";
	}
}
