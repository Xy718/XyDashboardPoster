package xyz.xy718.poster.graf;


import xyz.xy718.poster.XyDashboardPosterPlugin;
import xyz.xy718.poster.config.XyDashboardPosterConfig;
import xyz.xy718.poster.model.PlayerCountData;
import xyz.xy718.poster.model.ServerTPSData;
import xyz.xy718.poster.service.PlayerDataService;
import xyz.xy718.poster.service.ServerDataService;

/**
 * 用于收集玩家相关的监测内容
 * @author Xy718
 *
 */
public class PlayerDatagraf extends Datagraf{

	private static XyDashboardPosterConfig config=XyDashboardPosterPlugin.getMainConfig();
	
	public PlayerDatagraf(XyDashboardPosterPlugin plugin) {
		this.measurement="player";
		//死亡if她又来了
		if(config.isUsePlayerCount()) {
			buildTask("player-count"
					, new PlayerCount()
					,(long)(config.getGrafPlayerCountInternal()*1000)
					,(long)(config.getGrafPlayerCountInternal()*1000));
		}
	}

	class PlayerCount implements Work{
		@Override
		public void work() {
			PlayerCountData pcd=PlayerDataService.getPlayerCountInfo(measurement);
			dataList.add(pcd);
		}

		@Override
		public String workName() {
			return "player-count";
		}
	}

}
