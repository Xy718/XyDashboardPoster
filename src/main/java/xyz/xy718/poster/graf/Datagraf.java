package xyz.xy718.poster.graf;

import java.util.List;
import java.util.Timer;

import lombok.Getter;
import xyz.xy718.poster.model.Grafdata;

public class Datagraf {
	boolean runFlag;
	@Getter String measurement;
	@Getter public List<Grafdata> dataList;
	@Getter String grafname;

	Timer taskTimer;
	public void clearData() {
		this.dataList.clear();
	}
	public void startPoster() {
		if(this.runFlag) {
			//如果已启动
			return;
		}
		task();
	}

	public void endPost() {
		if(!this.runFlag) {
			//如果未启动
			return;
		}
		this.taskTimer.cancel();
		this.runFlag=false;
	}

	public Timer getPoster() {
		return this.taskTimer;
	}
	
	public void task() {
		
	}
}
