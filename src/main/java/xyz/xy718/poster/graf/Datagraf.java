package xyz.xy718.poster.graf;

import java.util.List;

import lombok.Getter;
import xyz.xy718.poster.model.Grafdata;

public class Datagraf {
	boolean runFlag;
	@Getter String measurement;
	@Getter public List<Grafdata> dataList;
	@Getter String grafname;
	public void clearData() {
		this.dataList.clear();
	}
}
