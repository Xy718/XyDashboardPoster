package xyz.xy718.poster.graf;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.spongepowered.api.scheduler.Task;

import xyz.xy718.poster.XyDashboardPosterPlugin;
import xyz.xy718.poster.model.Grafdata;
import xyz.xy718.poster.model.WorldData;
import xyz.xy718.poster.service.WorldDataService;

/**
 * 用于收集世界相关的监测内容
 * @author Xy718
 *
 */
public class WorldDatagraf extends Datagraf{

	private final Logger LOGGER=XyDashboardPosterPlugin.LOGGER;
	
	public WorldDatagraf(XyDashboardPosterPlugin plugin) {
		dataList=new ArrayList<Grafdata>();
		runFlag=false;
		measurement="world";
		startPoster();
	}
	
	
	public void task() {
		taskTimer=new Timer();
		taskTimer.schedule(new TimerTask() {
            @Override
            public void run() {
        		runFlag=true;
            	long startTime=System.currentTimeMillis();
				WorldDataService.getWorldInfo().forEach(w->{
					LOGGER.info("记录世界{}的区块数量:{}",w.getWorldName(),w.getChunkCount());
					//在收集器中放入数据
					dataList.add(w);
				});
				LOGGER.info("Timer耗时："+(System.currentTimeMillis()-startTime)+"ms");
            }
        }, 3000, 700);
	}
}
