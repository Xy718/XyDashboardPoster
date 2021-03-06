package xyz.xy718.poster.model;

import java.util.Map;
import java.util.UUID;

public class WorldTileEntityData extends Grafdata{

	public WorldTileEntityData(UUID wUuid,String wName,int cCount, Map<String, Integer> classificationEntities, String measurement) {
		super(measurement);
		this.tagMap.put("type", "tileentity_count");
		this.tagMap.put("world_name", wName);
		this.tagMap.put("world_UUID", wUuid.toString());
		this.fieldMap.put("tile_count", cCount);
		this.fieldMap.putAll(classificationEntities);
	}

	public String getWorldName() {
		return	this.tagMap.get("world_name");
	}

	public String getEntityCount() {
		return	this.fieldMap.get("tile_count")+"";
	}
}
