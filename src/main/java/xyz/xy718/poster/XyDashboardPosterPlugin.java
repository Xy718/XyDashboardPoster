package xyz.xy718.poster;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Callable;

import org.apache.commons.lang3.LocaleUtils;
import org.bstats.sponge.Metrics;
import org.bstats.sponge.Metrics2;
import org.bstats.sponge.Metrics2.Factory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.GameReloadEvent;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.game.state.GameStoppedServerEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.inject.Inject;

import lombok.Getter;
import lombok.Setter;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import xyz.xy718.poster.annotation.RegisterCommand;
import xyz.xy718.poster.command.AbstractCommand;
import xyz.xy718.poster.config.I18N;
import xyz.xy718.poster.config.XyDashboardPosterConfig;
import xyz.xy718.poster.util.InfluxDBConnection;
import xyz.xy718.poster.util.PackageUtil;

@Plugin(
id = XyDashboardPosterPlugin.PLUGIN_ID
, name = XyDashboardPosterPlugin.NAME
, version = XyDashboardPosterPlugin.VERSION
, description = XyDashboardPosterPlugin.DESCRIPTION
)
public class XyDashboardPosterPlugin {
	@Getter public static final String PLUGIN_ID = "@id@";
	@Getter public static final String NAME = "@name@";
	@Getter public static final String VERSION = "@version@";
	@Getter public static final String DESCRIPTION = "@description@";
	

	private static XyDashboardPosterPlugin instance;
	
	public static final Logger LOGGER = LoggerFactory.getLogger(NAME);
	
	@Inject
	private PluginContainer container;

	@Inject
    @ConfigDir(sharedRoot = false)
    private Path configPath;
	
	@Inject
	@DefaultConfig(sharedRoot = false)
	private ConfigurationLoader<CommentedConfigurationNode> configLoader;

	@Getter private static XyDashboardPosterConfig mainConfig;

	@Getter @Setter private static PosterManager posterManager;
	@Getter private static InfluxDBConnection influxDB;
    
    private static Instant gameStartedTime = null;
    
    @Inject
    public XyDashboardPosterPlugin() {
    	if (instance != null)
			throw new IllegalStateException();
		instance = this;
	}
	
    @Listener
    public void onGamePreStarting(GamePreInitializationEvent event) {
    	//先加载配置
    	loadConfig();
    	//再加载I18N
        loadI18N();
    }

    @Listener
    public void onGameStarting(GameInitializationEvent event) {
    	LOGGER.info(I18N.getString("plugin.starting",NAME));
    	addMetricsInformation();
    	reloadInfluxDB();
    	try {
    		//通过注解注册指令
			int access=registerCommandsByAnnotation(this.getClass(),this.container);
			LOGGER.info(I18N.getString("plugin.starting.regcommands",access));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
    @Listener
    public void onServerStart(GameStartedServerEvent event) {
    	LOGGER.info(I18N.getString("plugin.started",NAME));
    	Sponge.getScheduler().createSyncExecutor(this).submit(() -> gameStartedTime = Instant.now());
    	if(influxDB.ping()) {
    		//ping通才可以使用
        	posterManager=new PosterManager(instance);
    	}else {
    		LOGGER.error(I18N.getString("error.influxdb.ping"));
    	}
    }
    
    private void addMetricsInformation()
    {
    	/*
    	metrics.addCustomChart(new Metrics2.SimplePie("pluginVersion", new Callable<String>() {
            @Override
            public String call() throws Exception {
                return XyDashboardPosterPlugin.VERSION;
            }
        }));
    	metrics.addCustomChart(new Metrics2.SingleLineChart("servers_using_panels", new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                return posterManager.getDatagraf().workedGraf();
            }
        }));*/
    }
    
    @Listener
    public void onPluginsReload(GameReloadEvent event) {
    	LOGGER.warn(I18N.getString("plugin.reloadplugin",NAME));
    }
    
    @Listener
    public void onServerStoped(GameStoppedServerEvent event) {
        gameStartedTime = null;
    }
	public static XyDashboardPosterPlugin get() {
		if (instance == null)
			throw new IllegalStateException("Instance not available");
		return instance;
	}
    public PluginContainer getContainer() {
        return container;
    }

	public Path getConfigDir() {
		return configPath;
	}
	/**
	 * 加载配置文件
	 */
	private void loadConfig() {
        // init config
        if (!Files.exists(configPath)) {
            try {
                Files.createDirectories(configPath);
            } catch (IOException e) {
                LOGGER.error("Failed to create main config directory: {}",configPath);
                LOGGER.error(e.toString());
            }
        }
    	mainConfig=new XyDashboardPosterConfig(instance,configPath);
    	LOGGER.info("{}当前版本{}",NAME,VERSION);
	}

	/**
	 * 加载语言文件
	 */
	public void loadI18N() {
   	 // init i18n service
        I18N.setLogger(LOGGER);
        I18N.setPlugin(XyDashboardPosterPlugin.get());
        String localeStr = mainConfig.getLang();
        if (localeStr == null || localeStr.isEmpty()) {
            localeStr = "zh_CN";
        }
        Locale locale = LocaleUtils.toLocale(localeStr);
        I18N.setLocale(locale);
	}
	
	public static void configLogger(String msg,Object... args) {
		if(mainConfig.isLogger_debug()) {
			LOGGER.info(msg,args);
		}
	}
	
	public static void reloadInfluxDB() {
    	influxDB=new InfluxDBConnection(
    			mainConfig.getUser()
    			,mainConfig.getPassword()
    			,mainConfig.getHost()+":"+mainConfig.getPort()
    			,mainConfig.getDatabase()
    			,mainConfig.getRetention_policy()
    			);
	}
	
	
    public static Optional<Instant> getGameStartedTime() {
        return Optional.ofNullable(gameStartedTime);
    }
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static int registerCommandsByAnnotation(Class ObjectPlugin,PluginContainer plugin) {
        List<AbstractCommand> cmds=new ArrayList<>();
		try {
			//List<Class> cs=loadClassByLoader(classX.getClassLoader());
	        List<String> classNames = PackageUtil.getClassName("xyz.xy718.poster", true);
	        List<Class> cs=new ArrayList<>();
	        for(String name:classNames) {
	        	cs.add(ObjectPlugin.getClassLoader().loadClass(name));
	        }
	        //获取所有指令对象
			for(Class<AbstractCommand> c:cs) {
				if(c.getAnnotation(RegisterCommand.class)!=null) {
					//c是一个待注册指令
					AbstractCommand abc=c.newInstance();
					cmds.add(abc);
				}
			}
			List<AbstractCommand> buildedCommands=tree(cmds);
			buildedCommands.forEach(c->{
				Sponge.getCommandManager().register(XyDashboardPosterPlugin.get(), c.getBuilder().build(), c.getAliases()[0]);
			});
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return cmds.size();
	}

	private static List<AbstractCommand> tree(List<AbstractCommand> cmds){
		List<AbstractCommand> topCommands=Lists.newArrayList();
		cmds.forEach(cmd->{//载入顶级指令
			if(cmd.isRoot())topCommands.add(cmd);
		});
		List<AbstractCommand> childCommands=Lists.newArrayList();
		cmds.forEach(cmd->{//载入非顶级指令
			if(!cmd.isRoot())childCommands.add(cmd);
		});
		if(!topCommands.isEmpty()) {
			Map<String, String> map=Maps.newHashMapWithExpectedSize(childCommands.size());
			topCommands.forEach(top->{
				getChild(top,childCommands,map);
			});
		}
		return topCommands;
	}

	private static void getChild(
			AbstractCommand top, List<AbstractCommand> childCommands,Map<String, String> map
			) {
		childCommands.stream()
			.filter(c->!map.containsKey(c.getAliases()[1]))
			.forEach(c->{
				//判断我是不是主指令
				if(top.isRoot()) {
					//是,那就判断我的0是不是你的0
					if(top.getAliases()[0].equals(c.getAliases()[0])) {
						top.getBuilder().child(c.getBuilder().build(), c.getAliases()[1]);
						map.put(c.getAliases()[1], c.getAliases()[0]);
					}
				}else {
					//不是，我也是个弟弟，那就判断我的1是不是你的0
					if(top.getAliases()[1].equals(c.getAliases()[0])) {
						top.getBuilder().child(c.getBuilder().build(), c.getAliases()[1]);
						map.put(c.getAliases()[1], c.getAliases()[0]);
					}
				}
				//我完事了，你呢？
				if(!c.equals(top)) {//禁止套娃！
					getChild(c, childCommands, map);
				}
			});
	}
	
}
