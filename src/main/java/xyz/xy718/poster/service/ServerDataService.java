package xyz.xy718.poster.service;

import org.spongepowered.api.Sponge;

import xyz.xy718.poster.model.ServerData;

public class ServerDataService {

	//获取服务器TPS
	public static ServerData getServerTPS(String measurement) {
		return new ServerData(Sponge.getServer().getTicksPerSecond(), measurement);
	}
	//TODO 获取服务器内存相关
	//TODO 获取服务器运行时间及JVM运行时间
}
