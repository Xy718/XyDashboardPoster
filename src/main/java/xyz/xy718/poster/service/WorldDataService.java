package xyz.xy718.poster.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.world.Chunk;
import org.spongepowered.api.world.World;

import xyz.xy718.poster.XyDashboardPosterPlugin;
import xyz.xy718.poster.config.XyDashboardPosterConfig;
import xyz.xy718.poster.model.WorldData;
import xyz.xy718.poster.model.WorldEntityData;

public class WorldDataService {

	/**
	 * 获取世界加载区块信息
	 * @return
	 */
	public static List<WorldData> getWorldInfo() {
		XyDashboardPosterConfig config=XyDashboardPosterPlugin.getMainConfig();
		List<WorldData> wd=new ArrayList<WorldData>();
		for(World w:Sponge.getServer().getWorlds()){
			List<Chunk> testL=(List<Chunk>) w.getLoadedChunks();
			wd.add(new WorldData(w.getUniqueId(), w.getName(), testL.size(),config.getTbNameWorld()));
			XyDashboardPosterPlugin.LOGGER.info("world:{}:{}",w.getName(),testL.size());
		};
		return wd;
	}
	
	/**
	 * 获取每个世界的实体数量
	 * @return
	 */
	public static List<WorldEntityData> getWorldEntityInfo(){
		XyDashboardPosterConfig config=XyDashboardPosterPlugin.getMainConfig();
		List<WorldEntityData> wed=new ArrayList<WorldEntityData>();
		for(World w:Sponge.getServer().getWorlds()){
			wed.add(new WorldEntityData(w.getUniqueId(), w.getName(), w.getEntities().size(), config.getTbNameWorld()));
			XyDashboardPosterPlugin.LOGGER.info("entity:{}:{}",w.getName(),w.getEntities().size());
		};
		return wed;
	}
	//TODO 获取每个世界的玩家数量
}