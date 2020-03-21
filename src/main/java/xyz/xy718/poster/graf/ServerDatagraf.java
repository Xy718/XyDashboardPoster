package xyz.xy718.poster.graf;


import org.slf4j.Logger;

import xyz.xy718.poster.XyDashboardPosterPlugin;
import xyz.xy718.poster.config.XyDashboardPosterConfig;
import xyz.xy718.poster.model.ServerTPSData;
import xyz.xy718.poster.service.ServerDataService;

/**
 * 用于收集服务器相关的监测内容
 * @author Xy718
 *
 */
public class ServerDatagraf extends Datagraf{

	private static XyDashboardPosterConfig config=XyDashboardPosterPlugin.getMainConfig();
	
	public ServerDatagraf(XyDashboardPosterPlugin plugin) {
		this.measurement="server";
		//死亡if她又来了
		if(config.isUseTPS()) {
			buildTask("tps"
					, new TPS()
					,(long)(config.getGrafTPSInternal()*1000)
					,(long)(config.getGrafTPSInternal()*1000));
		}
	}

	class TPS implements Work{
		@Override
		public void work() {
			long startTime=System.currentTimeMillis();
			ServerTPSData data=ServerDataService.getServerTPS(config.getTbNameServer());
			dataList.add(data);
			XyDashboardPosterPlugin.configLogger("TPS:{}",data.getServerTPS());
			XyDashboardPosterPlugin.configLogger("TPS收集耗时："+(System.currentTimeMillis()-startTime)+"ms");
		}

		@Override
		public String workName() {
			return "server-tps";
		}
	}
}
