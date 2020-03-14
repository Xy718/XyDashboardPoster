package xyz.xy718.poster;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;

import org.slf4j.Logger;

import xyz.xy718.poster.graf.Xydatagraf;


public class DataPoster {

	private static Map<String, Object> inSendMessage;
	private Timer datagrafTask;
	private static final Logger LOGGER=XyDashbosrdPosterPlugin.LOGGER;
	
	public DataPoster(XyDashbosrdPosterPlugin plugin) {
		inSendMessage=new LinkedHashMap<String, Object>();
		datagrafTask=new Timer();
		datagrafTask.schedule(new TimerTask() {
			
			@Override
			public void run() {
				// TODO DataPoster发送数据
				Map<String, String> grafdatas=Xydatagraf.putData();
				LOGGER.info("====发送{}了条数据:",grafdatas.size());
				for(Entry<String, String> v:grafdatas.entrySet()) {
					LOGGER.info("====v:"+v);
				}
				
				/*
				 * Xydatagraf.putData().forEach((k,v)->{ LOGGER.info("====k:"+k+"-v:"+v); });
				 */
			}
		},1000,5000);
	}

	public void putMessage(String msgName,Object msgData) {
		
	}
}
