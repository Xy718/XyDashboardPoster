package xyz.xy718.poster.schedule;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.world.Chunk;
import org.spongepowered.api.world.World;

import xyz.xy718.poster.XyDashbosrdPosterPlugin;
import xyz.xy718.poster.model.WorldData;
import xyz.xy718.poster.service.SpongeDataService;

public class WorldDataPoster implements Poster{

	private final Logger LOGGER=XyDashbosrdPosterPlugin.LOGGER;
	//private Timer taskTimer;
	private Task posterTask;
	
	public WorldDataPoster(XyDashbosrdPosterPlugin plugin) {
		posterTask= Task.builder()
				.execute(() -> {
					long startTime=System.currentTimeMillis();
					LOGGER.info("记录世界区块数量");
					//Field[] aaa = Sponge.getServer().getClass().getFields();
					for(World w:Sponge.getServer().getWorlds()){
						List<Chunk> testL=(List<Chunk>) w.getLoadedChunks();
						/*
						int sum=0;
						Iterator<Chunk> iter = w.getLoadedChunks().iterator();
						while(iter.hasNext()){
							sum++;
							iter.next();
							}
						*/
						LOGGER.info(new WorldData(w.getUniqueId(), w.getName(), testL.size()).toString());
					};
					LOGGER.info("耗时："+(System.currentTimeMillis()-startTime)+"ms");
				})
				.async()
			    .delay(3000, TimeUnit.MILLISECONDS)
			    .interval(3, TimeUnit.SECONDS)
			    .name("WorldDataPoster")
			    .submit(plugin);
		/*
		Timer timer=new Timer();
		timer.schedule(new TimerTask() {
            @Override
            public void run() {
            	LOGGER.info("1");
            	SpongeDataService.getWorldInfo().forEach(w->
            	LOGGER.info(w.toString())
            			);
            }
        }, 1000, 3000);

		this.taskTimer=timer;
		*/
	}
	

	public void startPoster() {
		// TODO Auto-generated method stub
		
	}

	public void endPost() {
		// TODO Auto-generated method stub
		
	}

	public Task getPoster() {
		// TODO Auto-generated method stub
		return this.posterTask;
	}
	
}
