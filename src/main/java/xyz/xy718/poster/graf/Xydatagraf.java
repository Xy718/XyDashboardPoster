package xyz.xy718.poster.graf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import cn.hutool.core.util.RandomUtil;
import xyz.xy718.poster.XyDashbosrdPosterPlugin;
import xyz.xy718.poster.model.Grafdata;

public class Xydatagraf {
	//数据收集器列表<收集器名,收集器>
	private static Map<String, Datagraf> grafList=new HashMap<String, Datagraf>();
	//<数据表名<测点ID，测点数据>>
	private static Map<String,Map<Integer, Grafdata>> dataList=new HashMap<String , Map<Integer, Grafdata>>();
	
	public Xydatagraf(XyDashbosrdPosterPlugin plugin) {
		grafList.put("123", new WorldDatagraf(plugin)); 
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
			dataList.put(graf.measurement,points);
			graf.clearData();
		});
		return dataList;
	}
}
