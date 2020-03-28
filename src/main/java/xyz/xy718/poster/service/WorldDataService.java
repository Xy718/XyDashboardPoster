package xyz.xy718.poster.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.world.Chunk;
import org.spongepowered.api.world.World;

import xyz.xy718.poster.XyDashboardPosterPlugin;
import xyz.xy718.poster.config.I18N;
import xyz.xy718.poster.model.WorldChunkCountData;
import xyz.xy718.poster.model.WorldEntityData;
import xyz.xy718.poster.model.WorldTileEntityData;

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
				XyDashboardPosterPlugin.LOGGER.error(I18N.getString("error.graf.async", "WorldDataService.getWorldInfo()"));
			}
		};
		return wd;
	}
	
	/**
	 * 获取每个世界的实体数量和细分数量
	 * @return
	 */
	public static List<WorldEntityData> getWorldEntityInfo(String measurement){
		List<WorldEntityData> wed=new ArrayList<WorldEntityData>();
		for(World w:Sponge.getServer().getWorlds()){
			Map<String, Integer> classificationEntities=new HashMap<String, Integer>();
			w.getEntities().forEach(entity->{
				if(classificationEntities.get(entity.getType().getId())==null) {
					//新的分类
					classificationEntities.put(entity.getType().getId(), 1);
				}else {
					classificationEntities.put(entity.getType().getId(), classificationEntities.get(entity.getType().getId())+1);
				}
			});
			wed.add(new WorldEntityData(w.getUniqueId(), w.getName(), w.getEntities().size(),classificationEntities,measurement));
		};
		return wed;
	}
	/**
	 * 获取每个世界的TE数量和细分数量
	 * @return
	 */
	public static List<WorldTileEntityData> getWorldTileEntityInfo(String measurement){
		List<WorldTileEntityData> wed=new ArrayList<WorldTileEntityData>();
		for(World w:Sponge.getServer().getWorlds()){
			Map<String, Integer> classificationEntities=new HashMap<String, Integer>();
			w.getTileEntities().forEach(entity->{
				if(classificationEntities.get(entity.getType().getId())==null) {
					//新的分类
					classificationEntities.put(entity.getType().getId(), 1);
				}else {
					classificationEntities.put(entity.getType().getId(), classificationEntities.get(entity.getType().getId())+1);
				}
			});
			wed.add(new WorldTileEntityData(w.getUniqueId(), w.getName(), w.getTileEntities().size(),classificationEntities,measurement));
		};
		return wed;
	}
}
