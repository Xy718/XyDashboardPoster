package xyz.xy718.poster.graf;

import java.util.List;

import org.slf4j.Logger;

import xyz.xy718.poster.XyDashboardPosterPlugin;
import xyz.xy718.poster.config.XyDashboardPosterConfig;
import xyz.xy718.poster.model.WorldChunkCountData;
import xyz.xy718.poster.model.WorldEntityData;
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
			buildTask("chunk-count"
					, new ChunkCount()
					,(long)(config.getGrafChunkCountInternal()*1000)
					,(long)(config.getGrafChunkCountInternal()*1000));
		}
		if(config.isUseEntityCount()) {
			buildTask("entity-count"
					, new EntityCount()
					,(long)(config.getGrafEntityCountInternal()*1000)
					,(long)(config.getGrafEntityCountInternal()*1000));
		}
	}

	class ChunkCount implements Work{
		@Override
		public void work() {
			long startTime=System.currentTimeMillis();
			List<WorldChunkCountData> data=WorldDataService.getWorldInfo(config.getTbNameWorld());
			dataList.addAll(data);
			String logs="区块数量";
			for(WorldChunkCountData w:data){
				logs+="-"+w.getWorldName()+":"+w.getChunkCount();
			}
			XyDashboardPosterPlugin.configLogger(logs);
			XyDashboardPosterPlugin.configLogger("区块数量收集耗时："+(System.currentTimeMillis()-startTime)+"ms");
		}

		@Override
		public String workName() {
			return "world-chunk-count";
		}
	}
	class EntityCount implements Work{
		@Override
		public void work() {
			long startTime=System.currentTimeMillis();
			List<WorldEntityData> data=WorldDataService.getWorldEntityInfo(config.getTbNameWorld());
			dataList.addAll(data);
			String logs="实体数量";
			for(WorldEntityData w:data){
				logs+="-"+w.getWorldName()+":"+w.getEntityCount();
			}
			XyDashboardPosterPlugin.configLogger(logs);
			XyDashboardPosterPlugin.configLogger("实体数量收集耗时："+(System.currentTimeMillis()-startTime)+"ms");
		}

		@Override
		public String workName() {
			return "world-entity-count";
		}
	}
}
