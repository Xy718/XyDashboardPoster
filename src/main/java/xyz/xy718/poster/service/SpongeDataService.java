package xyz.xy718.poster.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.world.Chunk;
import org.spongepowered.api.world.World;

import xyz.xy718.poster.model.WorldData;

public class SpongeDataService {

	/**
	 * 获取世界地图信息
	 * @return
	 */
	public static List<WorldData> getWorldInfo() {
		List<WorldData> wd=new ArrayList<WorldData>();
		for(World w:Sponge.getServer().getWorlds()){
			List<Chunk> testL=(List<Chunk>) w.getLoadedChunks();
			wd.add(new WorldData(w.getUniqueId(), w.getName(), testL.size()));
		};
		return wd;
	}
}
