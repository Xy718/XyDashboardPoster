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
		};
		return wd;
	}
	
	//TODO 获取每个世界的实体数量
	//TODO 获取每个世界的玩家数量
}
