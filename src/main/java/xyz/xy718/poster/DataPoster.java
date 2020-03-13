package xyz.xy718.poster;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.slf4j.Logger;


public class DataPoster {

	private static Map<String, Object> inSendMessage;
	private Timer datagrafTask;
	private static final Logger LOGGER=XyDashbosrdPosterPlugin.LOGGER;
	
	public DataPoster(XyDashbosrdPosterPlugin plugin) {
		inSendMessage=new LinkedHashMap<String, Object>();
		datagrafTask.schedule(new TimerTask() {
			
			@Override
			public void run() {
				// TODO DataPoster发送数据
				LOGGER.info("发送了一条数据");
			}
		},1000,1000);
	}

	public void putMessage(String msgName,Object msgData) {
		InfluxDBFactory
	}
}
