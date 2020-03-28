package xyz.xy718.poster.graf;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import xyz.xy718.poster.XyDashboardPosterPlugin;
import xyz.xy718.poster.config.XyDashboardPosterConfig;
import xyz.xy718.poster.model.Grafdata;

public class XydatagrafManager {
	private static XyDashboardPosterConfig config=XyDashboardPosterPlugin.getMainConfig();
	//数据收集器列表<收集器名,收集器>
	private Map<String, Datagraf> grafList=new HashMap<String, Datagraf>();
	//数据列表<数据表名<tag对象，grafdata列表>>
	//private Map<String,Map<Map<String,String>, List<Grafdata>>> postDataList=new HashMap<>();
	
	public XydatagrafManager(XyDashboardPosterPlugin plugin) {
		//装载数据收集器集合
		//死亡if
		if(config.isUseWorldGraf()) {
			//启用世界数据收集
			grafList.put("WorldDatagraf", new WorldDatagraf(plugin)); 
		}
		if(config.isUseServerGraf()) {
			//启用服务器数据收集
			grafList.put("ServerDatagraf", new ServerDatagraf(plugin)); 
		}
		if(config.isUsePlayerGraf()) {
			//启用玩家数据收集
			grafList.put("PlayerDatagraf", new PlayerDatagraf(plugin)); 
		}
		//log显示一下
		XyDashboardPosterPlugin.LOGGER.info("总计{}个收集器",this.workedGraf());
	}

	/**
	 * 装载数据！<br>
	 * Map< 数据表名< tag对象，grafdata列表>>
	 * @return
	 */
	public Map<String,Map<Map<String,String>, List<Grafdata>>> putData() {
		Map<String,Map<Map<String,String>, List<Grafdata>>> retDataList=new HashMap<>();
		//postDataList.clear();
		//根据每个收集器创建测点Map集合
		grafList.forEach((grafName,graf) -> {
			//先拿到这个graf的所有数据集合
			if(config.getData_center_type().equals("InfluxDB")) {
				//如果是InfluxDB
				retDataList.put(graf.measurement, graf.getInfluxData());
			}
			graf.clearData();
		});
		return retDataList;
	}
	/**
	 * 停止所有的收集器
	 */
	public void StopAllGraf() {
		grafList.forEach((name,graf)->{
			graf.endAllTask();
		});
	}
	public int workedGraf() {
		int c=0;
		for(Entry<String, Datagraf> graf:this.grafList.entrySet()) {
			c+=graf.getValue().getWorkedCount();
		}
		return c;
	}
}
