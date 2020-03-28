package xyz.xy718.poster;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;

import lombok.Getter;
import xyz.xy718.poster.graf.WorldDatagraf;
import xyz.xy718.poster.graf.XydatagrafManager;

public class PosterManager {
	private DataPoster dataPoster;
	@Getter private XydatagrafManager datagraf;
	
	public PosterManager(XyDashboardPosterPlugin plugin) {
		this.datagraf=new XydatagrafManager(plugin);
		this.dataPoster=new DataPoster(plugin);
	}
	
	public void stopAllPoster() {
		this.dataPoster.endPost();
	}
	public void stopAllGraf() {
		this.datagraf.StopAllGraf();
	}
}
