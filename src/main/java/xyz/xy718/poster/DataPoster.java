package xyz.xy718.poster;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;

import org.influxdb.InfluxDB.ConsistencyLevel;
import org.influxdb.dto.BatchPoints;
import org.influxdb.dto.BatchPoints.Builder;
import org.influxdb.dto.Point;
import org.slf4j.Logger;

import xyz.xy718.poster.config.I18N;
import xyz.xy718.poster.config.XyDashboardPosterConfig;
import xyz.xy718.poster.model.Grafdata;
import xyz.xy718.poster.util.InfluxDBConnection;


public class DataPoster {

	private Timer posterTask;
	private static Logger LOGGER=XyDashboardPosterPlugin.LOGGER;
	private static XyDashboardPosterConfig config=XyDashboardPosterPlugin.getMainConfig();
	
	public DataPoster(XyDashboardPosterPlugin plugin) {
		posterTask=new Timer();
		posterTask.schedule(new TimerTask() {
			
			@Override
			public void run() {
				// TODO DataPoster发送数据
				long startTime=System.currentTimeMillis();
				//从graf管理器装载数据
				Map<String,Map<Map<String,String>, List<Grafdata>>> grafdatas=XyDashboardPosterPlugin.getPosterManager().getDatagraf().putData();
				int i=0;
				for(Entry<String, Map<Map<String, String>, List<Grafdata>>> v:grafdatas.entrySet()) {
					for(Entry<Map<String, String>, List<Grafdata>> v2:v.getValue().entrySet()) {
						i+=v2.getValue().size();
					}
				}
				//如果是空的
				if(grafdatas.isEmpty()) {
					return;
				}
				//分采集器写入TSDB
				InfluxDBConnection influxDBConnection = XyDashboardPosterPlugin.getInfluxDB();
				List<String> records = new ArrayList<String>();
				
				//每个measurement
				for(Entry<String, Map<Map<String, String>, List<Grafdata>>> measurement:grafdatas.entrySet()) {
					//每个tags
					for(Entry<Map<String, String>, List<Grafdata>> tags:measurement.getValue().entrySet()) {
						Builder batchbuilder = BatchPoints.database(config.getDatabase());
						//装载Tags
						tags.getKey().forEach((k,v)->batchbuilder.tag(k, v));
						BatchPoints batchPoints = batchbuilder
								.retentionPolicy(config.getRetention_policy()).consistency(ConsistencyLevel.ALL).build();
						
						//grafdatas不为空
						if(tags.getValue().isEmpty()) {
							continue;
						}
						//装载所有fields
						for(Grafdata data:tags.getValue()) {
							Map<String, String> t = data.getTagMap();
							Map<String, Object> f = data.getFieldMap();
							// 一条记录值
							try {
								Point point = influxDBConnection.pointBuilder(
										data.getMeasurement(),data.getTime(), t, f);
								// 将记录添加到batchPoints中
								batchPoints.point(point);
							} catch (Exception e) {
								LOGGER.error(I18N.getString("error.post.pointbuilder", e.getMessage()));
								LOGGER.debug("", e);
							}
						}
						
						// 将不同的batchPoints序列化后，一次性写入数据库，提高写入速度
						records.add(batchPoints.lineProtocol());
					}
				}
				
				// 将数据批量插入到数据库中
				try {
	 				influxDBConnection.batchInsert(config.getDatabase(),config.getRetention_policy(), ConsistencyLevel.ALL, records);
				} catch (Exception e) {
					LOGGER.error(I18N.getString("error.post.influxdb", e.getMessage()));
					e.printStackTrace();
				}
 				XyDashboardPosterPlugin.configLogger(I18N.getString("data.post.msg",i,(System.currentTimeMillis()-startTime)));
				
			}
		},(long)(config.getPost_internal()*1000),(long)(config.getPost_internal()*1000));
	}

	public void endPost() {
		posterTask.cancel();
	}

}
