package xyz.xy718.poster;

import java.util.HashMap;
import java.util.Map;

import xyz.xy718.poster.schedule.Poster;
import xyz.xy718.poster.schedule.WorldDataPoster;

public class PosterManager {
	private static Map<String, Poster> posterList=new HashMap<String, Poster>();
	public PosterManager(XyDashbosrdPosterPlugin plugin) {
		posterList.put("123", new WorldDataPoster(plugin));
	}
}
