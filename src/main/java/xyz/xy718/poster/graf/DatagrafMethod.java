package xyz.xy718.poster.graf;

import java.util.Timer;

import org.spongepowered.api.scheduler.Task;

public interface DatagrafMethod {
	
	void startPoster() ;
    Timer getPoster();
    void endPost();
    String getInfluxPostData();
}
