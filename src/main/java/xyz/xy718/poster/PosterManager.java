package xyz.xy718.poster;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;

import xyz.xy718.poster.graf.WorldDatagraf;
import xyz.xy718.poster.graf.XydatagrafManager;

public class PosterManager {
	private DataPoster dataPoster;
	private XydatagrafManager datagraf;
	
	public PosterManager(XyDashboardPosterPlugin plugin) {
		datagraf=new XydatagrafManager(plugin);
		dataPoster=new DataPoster(plugin);
	}
	
	public static void stopAllPoster() {
		
	}
	public static void stopAllGraf() {
		
	}
}
