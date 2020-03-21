package xyz.xy718.poster.service;

import org.spongepowered.api.Sponge;

import xyz.xy718.poster.model.ServerMemoryData;
import xyz.xy718.poster.model.ServerTPSData;

public class ServerDataService {

	/**
	 * 获取服务器TPS
	 * @param measurement
	 * @return
	 */
	public static ServerTPSData getServerTPS(String measurement) {
		return new ServerTPSData(Sponge.getServer().getTicksPerSecond(), measurement);
	}
	//TODO 获取服务器内存相关
	public static ServerMemoryData getServerMemory(String measurement) {
		long max 	= Runtime.getRuntime().maxMemory() / 1024 / 1024;	//设定的最大内存
        long total 	= Runtime.getRuntime().totalMemory() / 1024 / 1024;	//使用的内存
        long free 	= Runtime.getRuntime().freeMemory() / 1024 / 1024;	//空余的内存
		return new ServerMemoryData(total,free,max,(total/(double)max), measurement);
	}
	
	//TODO 获取服务器运行时间及JVM运行时间
}
