package xyz.xy718.poster.graf;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import xyz.xy718.poster.XyDashboardPosterPlugin;
import xyz.xy718.poster.config.XyDashboardPosterConfig;
import xyz.xy718.poster.model.Grafdata;

public class XydatagrafManager {
	private static XyDashboardPosterConfig config=XyDashboardPosterPlugin.getMainConfig();
	//数据收集器列表<收集器名,收集器>
	private static Map<String, Datagraf> grafList=new HashMap<String, Datagraf>();
	//数据列表<数据表名<tag对象，grafdata列表>>
	private static Map<String,Map<Map<String,String>, List<Grafdata>>> postDataList=new HashMap<>();
	
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
	}
	/*
world
	tags1
		List<data>1
	tags2
		List<data>2
player

server
	 */
	/**
	 * 装载数据！<br>
	 * Map< 数据表名< tag对象，grafdata列表>>
	 * @return
	 */
	public static Map<String,Map<Map<String,String>, List<Grafdata>>> putData() {
		postDataList.clear();
		//根据每个收集器创建测点Map集合
		grafList.forEach((grafName,graf) -> {
			//先拿到这个graf的所有数据集合
			if(config.getData_center_type().equals("InfluxDB")) {
				//如果是InfluxDB
				postDataList.put(graf.measurement, graf.getInfluxData());
			}
			graf.clearData();
		});
		return postDataList;
	}
}
