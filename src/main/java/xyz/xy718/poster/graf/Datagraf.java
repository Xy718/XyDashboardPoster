package xyz.xy718.poster.graf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import lombok.Getter;
import xyz.xy718.poster.graf.Datagraf.Work;
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

	interface Work{
		void work();
	}
	
	protected static TimerTask getTask(Work w) {
		TimerTask task=new TimerTask() {
            @Override
            public void run() {
            	w.work();
            }
        };
        return task;
	}
}
