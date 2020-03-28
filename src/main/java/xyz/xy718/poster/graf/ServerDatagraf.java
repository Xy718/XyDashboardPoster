package xyz.xy718.poster.graf;


import org.hyperic.sigar.SigarException;

import xyz.xy718.poster.XyDashboardPosterPlugin;
import xyz.xy718.poster.config.XyDashboardPosterConfig;
import xyz.xy718.poster.model.ServerMemoryData;
import xyz.xy718.poster.model.ServerTPSData;
import xyz.xy718.poster.model.ServerTimeData;
import xyz.xy718.poster.model.SystemCPUdata;
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
		if(config.isUseMemory()) {
			buildTask("memory"
					, new Memory()
					,(long)(config.getGrafMemoryInternal()*1000)
					,(long)(config.getGrafMemoryInternal()*1000));
		}
		if(config.isUseUptime()) {
			buildTask("uptime"
					, new UpTime()
					,(long)(config.getGrafUptimeInternal()*1000)
					,(long)(config.getGrafUptimeInternal()*1000));
		}
		if(config.isUseCPU()) {
			buildTask("cpu"
					, new CPU()
					,(long)(config.getGrafCPUInternal()*1000)
					,(long)(config.getGrafCPUInternal()*1000));
		}
	}

	class TPS implements Work{
		@Override
		public void work() {
			ServerTPSData data=ServerDataService.getServerTPS(config.getTbNameServer());
			dataList.add(data);
		}

		@Override
		public String workName() {
			return "server-tps";
		}
	}
	class Memory implements Work{
		@Override
		public void work() {
			ServerMemoryData data=ServerDataService.getServerMemory(config.getTbNameServer());
			dataList.add(data);
		}

		@Override
		public String workName() {
			return "server-memory";
		}
	}
	class UpTime implements Work{
		@Override
		public void work() {
			ServerTimeData data=ServerDataService.getServerTime(config.getTbNameServer());
			dataList.add(data);
		}

		@Override
		public String workName() {
			return "server-uptime";
		}
	}
	class CPU implements Work{
		@Override
		public void work() {
			SystemCPUdata data = null;
			try {
				data = ServerDataService.getSysCPU(config.getTbNameServer());
			} catch (SigarException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			dataList.add(data);
		}

		@Override
		public String workName() {
			return "server-cpu";
		}
	}
	
}
