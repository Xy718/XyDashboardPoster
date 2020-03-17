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
import xyz.xy718.poster.XyDashboardPosterPlugin;

public class XyDashboardPosterConfig {
	final private String mainConfName = "config.conf";

	private ConfigurationLoader<CommentedConfigurationNode> configLoader;
	private static Logger LOGGER=XyDashboardPosterPlugin.LOGGER;
	// formmater:off
	/** 主节点 */
	private CommentedConfigurationNode mainNode = null;
	/** 推送间隔*/
	@Getter private int post_internal;
	/** 数据中心类型*/
	@Getter private String data_center_type;
	
	//数据库地址
	@Getter private String host;
	//数据库端口
	@Getter private String port;
	//连接用户
	@Getter private String user;
	//用户密码
	@Getter private String password;
	//使用哪一个数据库
	@Getter private String database;
	//InfluxDB的保留策略
	@Getter private String retention_policy;

	//世界信息收集是否启用
	@Getter private boolean useWorldGraf;
	//表名
	@Getter private String tbNameWorld;
	//区块数量信息是否收集
	@Getter private boolean useChunkCount;
	
	
	// formmater:on
	public XyDashboardPosterConfig(XyDashboardPosterPlugin plugin, Path configDir) {
        Path path = configDir.resolve(mainConfName);
        this.configLoader = HoconConfigurationLoader.builder().setPath(path).build();
        //看看配置文件是否存在，不存在就新建，存在就更新
        try {
            if (Files.exists(path)) {
                upgradeConf();
            } else {
            	plugin.getContainer().getAsset(mainConfName).get().copyToFile(path);
            }
            //重载配置文件(在构造函数的用意就是读取一遍配置文件)
            reload();
        } catch (Exception ioe) {
            ioe.printStackTrace();
        }
    }

	/**
	 * 重载配置文件
	 */
	public void reload() {
        try {
        	//主节点(总配置)
            this.mainNode = this.configLoader.load();
            this.post_internal=this.mainNode.getNode("general").getNode("post-internal").getInt(10);
            this.data_center_type=this.mainNode.getNode("general").getNode("data-center-type").getString("InfluxDB");
            this.host=this.mainNode.getNode("data-center").getNode("host").getString("http://localhost");
            this.port=this.mainNode.getNode("data-center").getNode("port").getString("8086");
            this.user=this.mainNode.getNode("data-center").getNode("user").getString("root");
            this.password=this.mainNode.getNode("data-center").getNode("password").getString("123456");
            this.database=this.mainNode.getNode("data-center").getNode("database").getString("mcserver");
            this.retention_policy=this.mainNode.getNode("data-center").getNode("retention-policy").getString("default");
            this.useWorldGraf=this.mainNode.getNode("modules").getNode("world").getBoolean(false);
            this.tbNameWorld=this.mainNode.getNode("world").getNode("measurement-name").getString("world");
            this.useChunkCount=this.mainNode.getNode("world").getNode("chunk-count").getBoolean(false);
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
