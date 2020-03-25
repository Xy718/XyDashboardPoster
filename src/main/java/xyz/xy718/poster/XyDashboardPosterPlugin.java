package xyz.xy718.poster;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.LocaleUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.command.spec.CommandSpec.Builder;
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
import com.sun.glass.ui.TouchInputSupport;

import lombok.Getter;
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

	@Getter private static PosterManager posterManager;
	@Getter private static InfluxDBConnection influxDB;

    private static Instant gameStartedTime = null;
    
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
    	influxDB=new InfluxDBConnection(
    			mainConfig.getUser()
    			,mainConfig.getPassword()
    			,mainConfig.getHost()+":"+mainConfig.getPort()
    			,mainConfig.getDatabase()
    			,mainConfig.getRetention_policy()
    			);
    	try {
    		//通过注解注册指令
			int access=registerCommandsByAnnotation(this.getClass(),this.container);
			LOGGER.info("已注册{}条指令",access);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
    @Listener
    public void onServerStart(GameStartedServerEvent event) {
    	LOGGER.info("服务器启动成功，{}也开始工作了~",NAME);
    	Sponge.getScheduler().createSyncExecutor(this).submit(() -> this.gameStartedTime = Instant.now());
    	if(influxDB.ping()) {
    		//ping通才可以使用
        	posterManager=new PosterManager(instance);
    	}else {
    		LOGGER.error(I18N.getString("error.influxdb.ping"));
    	}
    }
    
    @Listener
    public void onPluginsReload(GameReloadEvent event) {
    	LOGGER.warn("{}重新加载~",NAME);
    }
    
    @Listener
    public void onServerStoped(GameStoppedServerEvent event) {
        this.gameStartedTime = null;
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
	private void loadI18N() {
   	 // init i18n service
        I18N.setLogger(LOGGER);
        I18N.setPlugin(this);
        String localeStr = mainConfig.getNode("general").getNode("lang").getString();
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
	
    public static Optional<Instant> getGameStartedTime() {
        return Optional.ofNullable(gameStartedTime);
    }
	public static int registerCommandsByAnnotation(Class<?> classX,PluginContainer plugin) {
        List<AbstractCommand> cmds=new ArrayList<>();
		try {
			//List<Class> cs=loadClassByLoader(classX.getClassLoader());
	        List<String> classNames = PackageUtil.getClassName("xyz.xy718.poster", true);
	        List<Class> cs=new ArrayList<>();
	        for(String name:classNames) {
	        	cs.add(classX.getClassLoader().loadClass(name));
	        }
	        //获取所有指令对象
			for(Class<AbstractCommand> c:cs) {
				if(c.getAnnotation(RegisterCommand.class)!=null) {
					//c是一个待注册指令
					AbstractCommand abc=c.newInstance();
					cmds.add(abc);
				}
			}
			List<AbstractCommand> buildedCommands=tree(999, cmds);
			buildedCommands.forEach(c->{
				Sponge.getCommandManager().register(XyDashboardPosterPlugin.get(), c.getBuilder().build(), c.getAliases()[0]);
			});
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return cmds.size();
	}

	private static List<AbstractCommand> tree(int dept,List<AbstractCommand> cmds){
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
				getChild(top,childCommands,dept,map);
			});
		}
		return topCommands;
	}

	private static void getChild(
			AbstractCommand top, List<AbstractCommand> childCommands, int dept,Map<String, String> map
			) {
		childCommands.stream()
			.filter(c->!map.containsKey(c.getAliases()[1]))
			.forEach(c->{
				map.put(c.getAliases()[1], c.getAliases()[0]);
				getChild(c, childCommands, dept, map);
				top.getBuilder().child(c.getBuilder().build(), c.getAliases()[1]);
			});
	}
}
