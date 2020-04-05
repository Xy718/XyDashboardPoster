package xyz.xy718.poster.graf;

import java.util.List;

import xyz.xy718.poster.XyDashboardPosterPlugin;
import xyz.xy718.poster.config.I18N;
import xyz.xy718.poster.config.XyDashboardPosterConfig;
import xyz.xy718.poster.model.WorldChunkCountData;
import xyz.xy718.poster.model.WorldEntityData;
import xyz.xy718.poster.model.WorldTileEntityData;
import xyz.xy718.poster.service.WorldDataService;

/**
 * 用于收集世界相关的监测内容
 * @author Xy718
 *
 */
public class WorldDatagraf extends Datagraf{
	private static XyDashboardPosterConfig config=XyDashboardPosterPlugin.getMainConfig();
	public WorldDatagraf(XyDashboardPosterPlugin plugin) {
		this.measurement="world";
		//死亡if她又来了
		if(config.isUseWorldGraf()) {
			buildTask("world-chunk"
					, new ChunkCount()
					,(long)(config.getGrafChunkCountInternal()*1000)
					,(long)(config.getGrafChunkCountInternal()*1000));
		}
		if(config.isUseEntityCount()) {
			buildTask("world-entity"
					, new EntityCount()
					,(long)(config.getGrafEntityCountInternal()*1000)
					,(long)(config.getGrafEntityCountInternal()*1000));
		}
		if(config.isUseTECount()) {
			buildTask("world-tile-entity"
					, new TileEntityCount()
					,(long)(config.getGrafTECountInternal()*1000)
					,(long)(config.getGrafTECountInternal()*1000));
		}
	}

	class ChunkCount implements Work{
		@Override
		public void work() {
			List<WorldChunkCountData> data=WorldDataService.getWorldInfo(config.getTbNameWorld());
			if(dataList.contains(null)) {
				XyDashboardPosterPlugin.LOGGER.info("ChunkCount-NULL");
			}
			dataList.addAll(data);
			String logs="";
			for(WorldChunkCountData w:data){
				logs+="-"+w.getWorldName()+":"+w.getChunkCount();
			}
			XyDashboardPosterPlugin.configLogger(I18N.getString("debug.log.chunk")+logs);
		}

		@Override
		public String workName() {
			return "world-chunk-count";
		}
	}
	class EntityCount implements Work{
		@Override
		public void work() {
			List<WorldEntityData> data=WorldDataService.getWorldEntityInfo(config.getTbNameWorld());
			if(dataList.contains(null)) {
				XyDashboardPosterPlugin.LOGGER.info("EntityCount-NULL");
			}
			dataList.addAll(data);
			String logs="";
			for(WorldEntityData w:data){
				logs+="-"+w.getWorldName()+":"+w.getEntityCount();
			}
			XyDashboardPosterPlugin.configLogger(I18N.getString("debug.log.entity")+logs);
		}

		@Override
		public String workName() {
			return "world-entity-count";
		}
	}
	class TileEntityCount implements Work{
		@Override
		public void work() {
			List<WorldTileEntityData> data=WorldDataService.getWorldTileEntityInfo(config.getTbNameWorld());
			if(dataList.contains(null)) {
				XyDashboardPosterPlugin.LOGGER.info("TileEntityCount-NULL");
			}
			dataList.addAll(data);
			String logs="";
			for(WorldTileEntityData w:data){
				logs+="-"+w.getWorldName()+":"+w.getEntityCount();
			}
			XyDashboardPosterPlugin.configLogger(I18N.getString("debug.log.tileentity")+logs);
		}

		@Override
		public String workName() {
			return "world-tile-entity-count";
		}
	}
}
