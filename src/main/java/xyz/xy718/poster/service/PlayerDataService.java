package xyz.xy718.poster.service;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;

import xyz.xy718.poster.model.PlayerCountData;

public class PlayerDataService {
	
	/**
	 * 获取每个世界的TE数量和细分数量
	 * @return
	 */
	public static PlayerCountData getPlayerCountInfo(String measurement){
		//time 	type	world1	world2	world3	total	max
		//时间	p_count	12		23		6		41		50
		Collection<Player> players=Sponge.getServer().getOnlinePlayers();
		Map<String, Integer> worldP=new HashMap<>();
		
		players.forEach(player->{
			//装载每个用户
			if(worldP.get(player.getWorld().getName())==null) {
				worldP.put(player.getWorld().getName(), 1);
			}else {
				worldP.put(player.getWorld().getName(), worldP.get(player.getWorld().getName())+1);
			}
		});
		return new PlayerCountData(worldP,players.size(),Sponge.getServer().getMaxPlayers(),measurement);
	}
}
