package xyz.xy718.poster.graf;

import java.util.List;

import lombok.Getter;

public class Datagraf {
	boolean runFlag;
	@Getter public List<String> dataList;
	@Getter String grafname;
	public void clearData() {
		this.dataList.clear();
	}
}
