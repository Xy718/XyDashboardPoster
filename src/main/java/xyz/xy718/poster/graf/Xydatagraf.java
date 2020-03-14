package xyz.xy718.poster.graf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import cn.hutool.core.util.RandomUtil;
import xyz.xy718.poster.XyDashbosrdPosterPlugin;

public class Xydatagraf {

	private static Map<String, Datagraf> grafList=new HashMap<String, Datagraf>();
	private static Map<String, String> dataList=new LinkedHashMap<String, String>();
	
	public Xydatagraf(XyDashbosrdPosterPlugin plugin) {
		grafList.put("123", new WorldDatagraf(plugin)); 
	}
	
	public static Map<String, String> putData() {
		dataList.clear();
		grafList.forEach((grafName,graf) -> {
			List<String> grafData=graf.getDataList();
			for(int i=0;i<grafData.size();i++) {
				dataList.put(grafName+RandomUtil.randomNumbers(5), grafData.get(i));
			}
			graf.dataList=new ArrayList<String>();
		});
		return dataList;
	}
}
