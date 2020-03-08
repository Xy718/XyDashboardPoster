package xyz.xy718.poster.model;

import java.util.UUID;

import lombok.Data;

@Data
public class WorldData {

	String 	worldName;
	UUID 	worldUUID;
	int 	chunkCount;
	public WorldData(UUID wUuid,String wName,int cCount) {
		this.worldName=wName;
		this.worldUUID=wUuid;
		this.chunkCount=cCount;
	}
	
	public String toString() {
		return worldName+":"+worldUUID.toString()+":"+chunkCount;
	}
}
