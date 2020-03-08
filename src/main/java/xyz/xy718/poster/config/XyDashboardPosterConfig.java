package xyz.xy718.poster.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import org.slf4j.Logger;
import org.spongepowered.api.util.TypeTokens;
import org.spongepowered.api.world.World;


import lombok.Getter;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import xyz.xy718.poster.XyDashbosrdPosterPlugin;

public class XyDashboardPosterConfig {
	final private String mainConfName = "config.conf";

	private ConfigurationLoader<CommentedConfigurationNode> configLoader;
	private static Logger LOGGER=XyDashbosrdPosterPlugin.LOGGER;
	// formmater:off
	/** 主节点 */
	private CommentedConfigurationNode mainNode = null;
	/** 追踪上限*/
	@Getter private int workCount;
	
	/** 追踪击杀是否启用 */
	@Getter private boolean trackKill;
	/** 追踪破坏是否启用 */
	@Getter private boolean trackBreak;

	// formmater:on
	public XyDashboardPosterConfig(XyDashbosrdPosterPlugin gdp, Path configDir) {
        Path path = configDir.resolve(mainConfName);
        this.configLoader = HoconConfigurationLoader.builder().setPath(path).build();
        //看看配置文件是否存在，不存在就新建，存在就更新
        try {
            if (Files.exists(path)) {
                upgradeConf();
            } else {
                gdp.getContainer().getAsset(mainConfName).get().copyToFile(path);
            }
            //重载配置文件
            reload();
        } catch (Exception ioe) {
            ioe.printStackTrace();
        }
    }

	/**
	 * 重载配置文件
	 */
	public void reload() {
		//最后写
        try {
        	//主节点(总配置)
            this.mainNode = this.configLoader.load();
            this.workCount = this.mainNode.getNode("general").getNode("work-count").getInt(0);
            
            this.trackKill =this.mainNode.getNode("modules").getNode("track-killing").getBoolean(false);
            this.trackBreak =this.mainNode.getNode("modules").getNode("track-breaking").getBoolean(false);
        } catch (IOException e) {
            e.printStackTrace();
            LOGGER.warn("reload failed: {}", mainConfName);
        }
    }
    private void upgradeConf() {
    	LOGGER.info("更新配置文件（假的）");
        //TODO 从新插件更新新版配置文件
    }

    public CommentedConfigurationNode getNode(@Nonnull final Object... keys) {
        return this.mainNode.getNode(keys);
    }

}
