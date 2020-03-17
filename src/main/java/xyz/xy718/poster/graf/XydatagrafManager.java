package xyz.xy718.poster.graf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import xyz.xy718.poster.XyDashboardPosterPlugin;
import xyz.xy718.poster.config.XyDashboardPosterConfig;
import xyz.xy718.poster.model.Grafdata;

public class XydatagrafManager {
	private static XyDashboardPosterConfig config=XyDashboardPosterPlugin.getMainConfig();
	//数据收集器列表<收集器名,收集器>
	private static Map<String, Datagraf> grafList=new HashMap<String, Datagraf>();
	//数据列表<数据表名<测点ID，测点对象>>
	private static Map<String,Map<Integer, Grafdata>> dataList=new HashMap<String , Map<Integer, Grafdata>>();
	
	public XydatagrafManager(XyDashboardPosterPlugin plugin) {
		//装载数据收集器集合
		//死亡if
		if(config.isUseWorldGraf()) {
			//启用世界数据收集
			grafList.put("WorldDatagraf", new WorldDatagraf(plugin)); 
		}
	}
	
	public static Map<String, Map<Integer, Grafdata>> putData() {
		dataList.clear();
		//根据每个收集器创建测点Map集合
		grafList.forEach((grafName,graf) -> {
			Map<Integer, Grafdata> points=new HashMap<>();//测点集合
			List<Grafdata> grafData=graf.getDataList();
			for(int i=0;i<grafData.size();i++) {
				points.put(i, grafData.get(i));
			}
			if(!points.isEmpty()) {
				dataList.put(graf.measurement,points);
			}
			graf.clearData();
		});
		return dataList;
	}
}
