package xyz.xy718.poster.schedule;

import java.util.Timer;

import org.spongepowered.api.scheduler.Task;

public interface Poster {
	void startPoster() ;
    Task getPoster();
    void endPost();
}
