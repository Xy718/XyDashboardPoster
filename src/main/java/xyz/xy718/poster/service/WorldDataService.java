package xyz.xy718.poster.service;

import java.util.ArrayList;
import java.util.List;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.world.Chunk;
import org.spongepowered.api.world.World;

import xyz.xy718.poster.XyDashboardPosterPlugin;
import xyz.xy718.poster.model.WorldChunkCountData;
import xyz.xy718.poster.model.WorldEntityData;

public class WorldDataService {

	/**
	 * 获取世界加载区块信息
	 * @return
	 */
	public static List<WorldChunkCountData> getWorldInfo(String measurement) {
		List<WorldChunkCountData> wd=new ArrayList<WorldChunkCountData>();
		for(World w:Sponge.getServer().getWorlds()){
			try {
				List<Chunk> testL=(List<Chunk>) w.getLoadedChunks();
				wd.add(new WorldChunkCountData(w.getUniqueId(), w.getName(), testL.size(),measurement));
			}catch (Exception e) {
				e.printStackTrace();
				XyDashboardPosterPlugin.LOGGER.error("{}出了一点小问题,跳过了一次数据收集","WorldDataService.getWorldInfo()");
			}
		};
		return wd;
	}
	
	/**
	 * 获取每个世界的实体数量
	 * @return
	 */
	public static List<WorldEntityData> getWorldEntityInfo(String measurement){
		List<WorldEntityData> wed=new ArrayList<WorldEntityData>();
		for(World w:Sponge.getServer().getWorlds()){
			wed.add(new WorldEntityData(w.getUniqueId(), w.getName(), w.getEntities().size(),measurement));
		};
		return wed;
	}
	//TODO 获取每个世界的玩家数量
}
