package xyz.xy718.poster.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.slf4j.Logger;
import org.spongepowered.api.data.type.ComparatorTypes;
import org.spongepowered.api.util.TypeTokens;
import org.spongepowered.api.world.World;

import com.google.common.collect.Comparators;
import com.typesafe.config.ConfigParseOptions;

import lombok.Getter;
import ninja.leaping.configurate.ConfigurationOptions;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.loader.HeaderMode;
import ninja.leaping.configurate.objectmapping.DefaultObjectMapperFactory;
import ninja.leaping.configurate.objectmapping.GuiceObjectMapperFactory;
import ninja.leaping.configurate.objectmapping.ObjectMapperFactory;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializerCollection;
import ninja.leaping.configurate.util.MapFactories;
import ninja.leaping.configurate.util.MapFactory;
import xyz.xy718.poster.XyDashboardPosterPlugin;

public class XyDashboardPosterConfig {
	final private String mainConfName = "config.conf";

	private ConfigurationLoader<CommentedConfigurationNode> configLoader;
	private static Logger LOGGER=XyDashboardPosterPlugin.LOGGER;

	Path path;
	
	// formmater:on
	public XyDashboardPosterConfig(XyDashboardPosterPlugin plugin, Path configDir) {
        this.path = configDir.resolve(mainConfName);
        this.configLoader = HoconConfigurationLoader.builder()
        		.setPath(path).build();
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
            this.mainNode				=this.configLoader.load();
            this.post_internal			=this.mainNode.getNode("a-general","post-internal").getInt(10);
            this.data_center_type		=this.mainNode.getNode("a-general","data-center-type").getString("InfluxDB");
            this.logger_debug			=this.mainNode.getNode("a-general","logger-debug").getBoolean(false);
            this.lang					=this.mainNode.getNode("a-general","lang").getString("zh_CN");
            
            this.host					=this.mainNode.getNode("data-center","host").getString("http://localhost");
            this.port					=this.mainNode.getNode("data-center","port").getString("8086");
            this.user					=this.mainNode.getNode("data-center","user").getString("root");
            this.password				=this.mainNode.getNode("data-center","password").getString("123456");
            this.database				=this.mainNode.getNode("data-center","database").getString("mcserver");
            this.retention_policy		=this.mainNode.getNode("data-center","retention-policy").getString("default");
            //模块启用
            this.useWorldGraf			=this.mainNode.getNode("modules","world").getBoolean(false);
            this.useServerGraf			=this.mainNode.getNode("modules","server").getBoolean(false);
            
            this.tbNameWorld			=this.mainNode.getNode("modules-world","a-measurement-name").getString("world");
            this.useChunkCount			=this.mainNode.getNode("modules-world","chunk","enable").getBoolean(false);
            this.grafChunkCountInternal	=this.mainNode.getNode("modules-world","chunk","internal").getDouble(1.5);
            this.useEntityCount			=this.mainNode.getNode("modules-world","entity","enable").getBoolean(false);
            this.grafEntityCountInternal=this.mainNode.getNode("modules-world","entity","internal").getDouble(1);
            this.useTECount				=this.mainNode.getNode("modules-world","tile-entity","enable").getBoolean(false);
            this.grafTECountInternal	=this.mainNode.getNode("modules-world","tile-entity","internal").getDouble(1);
            
            this.tbNameServer			=this.mainNode.getNode("modules-server","a-measurement-name").getString("server");
            this.useTPS					=this.mainNode.getNode("modules-server","tps","enable").getBoolean(false);
            this.grafTPSInternal		=this.mainNode.getNode("modules-server","tps","internal").getDouble(3);
            this.useMemory				=this.mainNode.getNode("modules-server","memory","enable").getBoolean(false);
            this.grafMemoryInternal		=this.mainNode.getNode("modules-server","memory","internal").getDouble(3);
            this.useUptime				=this.mainNode.getNode("modules-server","uptime","enable").getBoolean(false);
            this.grafUptimeInternal		=this.mainNode.getNode("modules-server","uptime","internal").getDouble(3);
            
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

    public void saveConfig() throws IOException {
    	this.configLoader.save(mainNode);
    	reload();
    }
    
	// formmater:off
	/** 主节点 */
	@Getter private CommentedConfigurationNode mainNode = null;
	/** 推送间隔*/
	@Getter private int post_internal;
	/** 数据中心类型*/
	@Getter private String data_center_type;
	/** debug输出模式*/
	@Getter private boolean logger_debug;
	/** 语言*/
	@Getter private String lang;
	
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

	/**世界信息收集是否启用*/
	@Getter private boolean useWorldGraf;
	//表名
	@Getter private String tbNameWorld;
	//区块数量信息是否收集
	@Getter private boolean useChunkCount;
	//区块数据采集间隔(秒)
	@Getter private double grafChunkCountInternal;
	//实体数量信息收集
	@Getter private boolean useEntityCount;
	//实体数量信息数据采集间隔(秒)
	@Getter private double grafEntityCountInternal;
	//TE数量信息收集
	@Getter private boolean useTECount;
	//TE数量信息数据采集间隔(秒)
	@Getter private double grafTECountInternal;
	
	/**服务器信息收集是否启用*/
	@Getter private boolean useServerGraf;
	//表名
	@Getter private String tbNameServer;
	//TPS信息是否收集
	@Getter private boolean useTPS;
	//TPS数据采集间隔(秒)
	@Getter private double grafTPSInternal;
	//内存信息是否收集
	@Getter private boolean useMemory;
	//内存数据采集间隔(秒)
	@Getter private double grafMemoryInternal;
	//运行时间信息是否收集
	@Getter private boolean useUptime;
	//运行时间数据采集间隔(秒)
	@Getter private double grafUptimeInternal;
}
