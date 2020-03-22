package xyz.xy718.poster.service;

import java.lang.management.ManagementFactory;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

import org.spongepowered.api.Sponge;

import xyz.xy718.poster.XyDashboardPosterPlugin;
import xyz.xy718.poster.model.ServerMemoryData;
import xyz.xy718.poster.model.ServerTPSData;
import xyz.xy718.poster.model.ServerTimeData;
import xyz.xy718.poster.util.Util;

public class ServerDataService {

	/**
	 * 获取服务器TPS
	 * @param measurement
	 * @return
	 */
	public static ServerTPSData getServerTPS(String measurement) {
		return new ServerTPSData(Sponge.getServer().getTicksPerSecond(), measurement);
	}
	/**
	 * 获取服务器内存相关
	 * @param measurement
	 * @return
	 */
	public static ServerMemoryData getServerMemory(String measurement) {
		long max 	= Runtime.getRuntime().maxMemory() / 1024 / 1024;	//设定的最大内存
        long total 	= Runtime.getRuntime().totalMemory() / 1024 / 1024;	//使用的内存
        long free 	= Runtime.getRuntime().freeMemory() / 1024 / 1024;	//空余的内存
		return new ServerMemoryData(total,free,max,total-free,((total-free)/(double)max), measurement);
	}
	
	/**
	 * 获取服务器运行时间及JVM运行时间
	 * @param measurement
	 * @return
	 */
	public static ServerTimeData getServerTime(String measurement) {
		Optional<Instant> oi = XyDashboardPosterPlugin.getGameStartedTime();
		ServerTimeData std=new ServerTimeData(measurement);
        Duration uptime = Duration.ofMillis(ManagementFactory.getRuntimeMXBean().getUptime());
		
		oi.ifPresent(instant -> {
            Duration duration = Duration.between(instant, Instant.now());
            std.addData(Util.getTimeStringFromSeconds(duration.getSeconds())
            		,Util.getTimeStringFromSeconds(uptime.getSeconds()));
        });
		return std;
	}
}
