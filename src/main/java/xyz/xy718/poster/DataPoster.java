package xyz.xy718.poster;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;

import org.influxdb.InfluxDB.ConsistencyLevel;
import org.influxdb.dto.BatchPoints;
import org.influxdb.dto.Point;
import org.slf4j.Logger;

import xyz.xy718.poster.config.XyDashboardPosterConfig;
import xyz.xy718.poster.graf.XydatagrafManager;
import xyz.xy718.poster.model.Grafdata;
import xyz.xy718.poster.util.InfluxDBConnection;


public class DataPoster {

	private static Map<String, Object> inSendMessage;
	private Timer datagrafTask;
	private static final Logger LOGGER=XyDashboardPosterPlugin.LOGGER;
	private static XyDashboardPosterConfig config=XyDashboardPosterPlugin.getMainConfig();
	
	public DataPoster(XyDashboardPosterPlugin plugin) {
		inSendMessage=new LinkedHashMap<String, Object>();
		datagrafTask=new Timer();
		datagrafTask.schedule(new TimerTask() {
			
			@Override
			public void run() {
				// TODO DataPoster发送数据
				long startTime=System.currentTimeMillis();
				//从graf管理器装载数据
				Map<String, Map<Integer, Grafdata>> grafdatas=XydatagrafManager.putData();
				int i=0;
				for(Entry<String, Map<Integer, Grafdata>> v:grafdatas.entrySet()) {
					for(Entry<Integer, Grafdata> v2:v.getValue().entrySet()) {
						i++;
						LOGGER.info("===="+v2.getKey()+"---"+v2.getValue().toInfluxDB());
					}
				}
				LOGGER.info("====发送{}了条数据:",i);
				//如果是空的
				if(grafdatas.isEmpty()) {
					return;
				}
				//分采集器写入TSDB
				InfluxDBConnection influxDBConnection = XyDashboardPosterPlugin.getInfluxDB();
				
				List<String> records = new ArrayList<String>();
				//每个数据表
				for(Entry<String, Map<Integer, Grafdata>> graf:grafdatas.entrySet()) {
					BatchPoints batchPoints = BatchPoints.database(config.getDatabase())
							.retentionPolicy(config.getRetention_policy()).consistency(ConsistencyLevel.ALL).build();
					//该数据表下的所有数据
					for(Entry<Integer, Grafdata> data:graf.getValue().entrySet()) {
						Map<String, String> tags = data.getValue().getTagMap();
						Map<String, Object> fields = data.getValue().getFieldMap();
						// 一条记录值
						Point point = influxDBConnection.pointBuilder(
								data.getValue().getMeasurement(),data.getValue().getTime(), tags, fields);
						// 将记录添加到batchPoints中
						batchPoints.point(point);
					}
					
					// 将不同的batchPoints序列化后，一次性写入数据库，提高写入速度
					records.add(batchPoints.lineProtocol());
					
				}
				// 将数据批量插入到数据库中
				influxDBConnection.batchInsert(config.getDatabase(),config.getRetention_policy(), ConsistencyLevel.ALL, records);
				LOGGER.info("耗时:{}ms",(System.currentTimeMillis()-startTime));
			}
		},config.getPost_internal()*1000,config.getPost_internal()*1000);
	}

}
