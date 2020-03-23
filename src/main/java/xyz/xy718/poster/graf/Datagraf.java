package xyz.xy718.poster.graf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.spongepowered.api.Sponge;

import lombok.Getter;
import xyz.xy718.poster.XyDashboardPosterPlugin;
import xyz.xy718.poster.graf.WorldDatagraf.EntityCount;
import xyz.xy718.poster.model.Grafdata;

public class Datagraf {
	@Getter String measurement;
	@Getter public List<Grafdata> dataList=new ArrayList<Grafdata>();
	@Getter String grafname;

	Map<String, Timer> taskTimers=new HashMap<String, Timer>();
	Map<String, Boolean> taskRunStatus=new HashMap<>();
	
	/**
	 * 清空已收集的数据
	 */
	public void clearData() {
		this.dataList.clear();
	}
	
	/**
	 * 创建，并开始一个收集任务
	 * @param name 收集器名称
	 * @param work
	 * @param delay
	 * @param period
	 */
	public void buildTask(String name,Work work,long delay,long period) {
		Timer taskTimer=new Timer();
		taskTimer.schedule(
				Datagraf.getTask(work)
				, delay,period);
		startTask(name, taskTimer);
	}
	
	/**
	 * 开始一个收集任务
	 * @param taskName
	 * @param taskBody
	 */
	public void startTask(String taskName,Timer taskBody) {
		if(this.taskRunStatus.get(taskName)==null) {
			return;
		}
		if(this.taskRunStatus.get(taskName)) {
			return;
		}
		this.taskTimers.put(taskName, taskBody);
		this.taskRunStatus.put(taskName, true);
	}
	/**
	 * 停止一个收集任务
	 * @param taskName
	 */
	public void endTask(String taskName) {
		if(this.taskRunStatus.get(taskName)==null) {
			return;
		}
		if(!this.taskRunStatus.get(taskName)) {
			return;
		}
		this.taskRunStatus.put(taskName, false);
		taskTimers.get(taskName).cancel();
	}
	/**
	 * 输出该graf的数据的Influxdb格式<br>
	 *<数据表名< tag对象，grafdata列表>>
	 * @return
	 */
	public Map<Map<String, String>, List<Grafdata>> getInfluxData() {
		if(this.dataList.isEmpty()) {
			return null;
		}
		Map<Map<String, String>, List<Grafdata>> data=new HashMap<>();
		List<Grafdata> dataList=this.dataList;
		for(int i=0;i<dataList.size();i++) {
			if(data.get(dataList.get(i).getTagMap())==null) {
				//如果不存在这个tag
				data.put(dataList.get(i).getTagMap(), new ArrayList<>());
			}
			//在这个tag下放入数据
			data.get(dataList.get(i).getTagMap()).add(dataList.get(i));
		}
		return data;
	}
	
	interface Work{
		String workName();
		void work();
	}
	
	protected static TimerTask getTask(Work w) {
		XyDashboardPosterPlugin.LOGGER.info("数据收集器{}开始工作!",w.workName());
		TimerTask task=new TimerTask() {
            @Override
            public void run() {
            	if(!Sponge.isServerAvailable()) {
            		this.cancel();
            		XyDashboardPosterPlugin.LOGGER.info("由于服务器已关闭，收集器:{}已停止收集工作",w.workName());
            	}
    			long startTime=System.currentTimeMillis();
            	w.work();
            	XyDashboardPosterPlugin.configLogger("UpTime:{} 收集耗时：{}ms",2.,(System.currentTimeMillis()-startTime));
            }
        };
        return task;
	}
}
