package xyz.xy718.poster;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;

import xyz.xy718.poster.graf.DatagrafMethod;
import xyz.xy718.poster.graf.WorldDatagraf;
import xyz.xy718.poster.graf.Xydatagraf;

public class PosterManager {
	private DataPoster dataPoster;
	private Xydatagraf datagraf;
	
	public PosterManager(XyDashbosrdPosterPlugin plugin) {
		datagraf=new Xydatagraf(plugin);
		dataPoster=new DataPoster(plugin);
	}
	
	public void stopPost() {
		
	}
	public void startPost() {
	
	}
}
