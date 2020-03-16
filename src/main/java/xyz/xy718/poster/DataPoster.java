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
				//装载数据
				Map<String, Map<Integer, Grafdata>> grafdatas=Xydatagraf.putData();
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
				InfluxDBConnection influxDBConnection = XyDashbosrdPosterPlugin.getInfluxDB();
				
				List<String> records = new ArrayList<String>();
				//每个数据表
				for(Entry<String, Map<Integer, Grafdata>> graf:grafdatas.entrySet()) {
					//该数据表下的所有数据
					BatchPoints batchPoints = BatchPoints.database("mcserver")
							.retentionPolicy("1d").consistency(ConsistencyLevel.ALL).build();
					for(Entry<Integer, Grafdata> data:graf.getValue().entrySet()) {
						Map<String, String> tags1 = data.getValue().getTagMap();
						Map<String, Object> fields1 = data.getValue().getFieldMap();
						// 一条记录值
						Point point = influxDBConnection.pointBuilder(
								data.getValue().getMeasurement(),data.getValue().getTime(), tags1, fields1);
						// 将记录添加到batchPoints中
						batchPoints.point(point);
					}
					
					// 将不同的batchPoints序列化后，一次性写入数据库，提高写入速度
					records.add(batchPoints.lineProtocol());
					
				}
				// 将数据批量插入到数据库中
				influxDBConnection.batchInsert("mcserver", "autogen", ConsistencyLevel.ALL, records);
			}
		},1000,10000);
	}

	public void putMessage(String msgName,Object msgData) {
		
	}
}
