package xyz.xy718.poster;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;

import org.influxdb.InfluxDB.ConsistencyLevel;
import org.influxdb.dto.BatchPoints;
import org.influxdb.dto.Point;
import org.slf4j.Logger;

import xyz.xy718.poster.graf.Xydatagraf;
import xyz.xy718.poster.model.Grafdata;
import xyz.xy718.poster.util.InfluxDBConnection;


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
				Map<String, Map<Integer, Grafdata>> grafdatas=Xydatagraf.putData();
				int i=0;
				for(Entry<String, Map<Integer, Grafdata>> v:grafdatas.entrySet()) {
					for(Entry<Integer, Grafdata> v2:v.getValue().entrySet()) {
						i++;
						LOGGER.info("===="+v2.getKey()+"---"+v2.getValue().toInfluxDB());
					}
				}
				LOGGER.info("====发送{}了条数据:",i);
				
				//分采集器写入TSDB
				InfluxDBConnection influxDBConnection = XyDashbosrdPosterPlugin.getInfluxDB();
				for(Entry<String, Map<Integer, Grafdata>> graf:grafdatas.entrySet()) {

					Map<String, String> tags = new HashMap<String, String>();
					tags.put("tag1", "标签值");
					
					Map<String, Object> fields1 = new HashMap<String, Object>();
					fields1.put("field1", "abc");
					// 数值型，InfluxDB的字段类型，由第一天插入的值得类型决定
					fields1.put("field2", 123456);
					
					Map<String, Object> fields2 = new HashMap<String, Object>();
					fields2.put("field1", "String类型");
					fields2.put("field2", 3.141592657);
					
					// 一条记录值
					Point point1 = influxDBConnection.pointBuilder("表名", System.currentTimeMillis(), tags, fields1);
					Point point2 = influxDBConnection.pointBuilder("表名", System.currentTimeMillis(), tags, fields2);
					// 将两条记录添加到batchPoints中
					//因为标签是不一样的
					BatchPoints batchPoints1 = BatchPoints.database("db-test").tag("tag1", "标签值1").retentionPolicy("hour")
							.consistency(ConsistencyLevel.ALL).build();
					BatchPoints batchPoints2 = BatchPoints.database("db-test").tag("tag2", "标签值2").retentionPolicy("hour")
							.consistency(ConsistencyLevel.ALL).build();
					batchPoints1.point(point1);
					batchPoints2.point(point2);
					// 将两条数据批量插入到数据库中
					influxDBConnection.batchInsert(batchPoints1);
					influxDBConnection.batchInsert(batchPoints2);
				}
			}
		},1000,10000);
	}

	public void putMessage(String msgName,Object msgData) {
		
	}
}
