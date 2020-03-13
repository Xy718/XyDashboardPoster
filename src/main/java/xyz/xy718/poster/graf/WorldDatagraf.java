package xyz.xy718.poster.graf;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.spongepowered.api.scheduler.Task;

import xyz.xy718.poster.XyDashbosrdPosterPlugin;
import xyz.xy718.poster.model.WorldData;
import xyz.xy718.poster.service.SpongeDataService;

/**
 * 用于收集世界相关的监测内容
 * @author Xy718
 *
 */
public class WorldDatagraf extends Datagraf implements DatagrafMethod{

	private final Logger LOGGER=XyDashbosrdPosterPlugin.LOGGER;
	private Timer taskTimer;
	
	public WorldDatagraf(XyDashbosrdPosterPlugin plugin) {
		dataList=new ArrayList<Object>();
		runFlag=false;
		startPoster();
	}
	

	@Override
	public void startPoster() {
		if(this.runFlag) {
			//如果已启动
			return;
		}
		task();
	}

	@Override
	public void endPost() {
		if(!this.runFlag) {
			//如果未启动
			return;
		}
		this.taskTimer.cancel();
		this.runFlag=false;
	}

	@Override
	public Timer getPoster() {
		return this.taskTimer;
	}
	
	private void task() {
		taskTimer.schedule(new TimerTask() {
            @Override
            public void run() {
        		runFlag=true;
            	long startTime=System.currentTimeMillis();
				LOGGER.info("记录世界区块数量");
				SpongeDataService.getWorldInfo().forEach(w->
					LOGGER.info(w.toString())
				);
				LOGGER.info("Timer耗时："+(System.currentTimeMillis()-startTime)+"ms");
            }
        }, 3000, 3000);
	}


	@Override
	public String getInfluxPostData() {
		return null;
	}
	
}
