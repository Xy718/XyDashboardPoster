package xyz.xy718.poster.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.world.Chunk;

import xyz.xy718.poster.model.WorldData;

public class SpongeDataService {

	/**
	 * 获取世界地图信息
	 * @return
	 */
	public static List<WorldData> getWorldInfo() {
		List<WorldData> wd=new ArrayList<WorldData>();
		Sponge.getServer().getWorlds().stream()
				.forEach(w ->{
					int sum=0;
					Iterator<Chunk> iter = w.getLoadedChunks().iterator();
					while(iter.hasNext()){
						sum++;
					}
					wd.add(new WorldData(w.getUniqueId(), w.getName(), sum));
				});
		return wd;
	}
}
