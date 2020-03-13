package xyz.xy718.poster.graf;

import java.util.HashMap;
import java.util.Map;

import xyz.xy718.poster.XyDashbosrdPosterPlugin;

public class Xydatagraf {

	private static Map<String, Object> grafList=new HashMap<String, Object>();
	
	public Xydatagraf(XyDashbosrdPosterPlugin plugin) {
		grafList.put("123", new WorldDatagraf(plugin)); 
	}
}
