package xyz.xy718.poster;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Locale;
import java.util.Optional;

import org.apache.commons.lang3.LocaleUtils;
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

import com.google.inject.Inject;

import lombok.Getter;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import xyz.xy718.poster.config.I18N;
import xyz.xy718.poster.config.XyDashboardPosterConfig;
import xyz.xy718.poster.util.InfluxDBConnection;

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
    	LOGGER.info(I18N.getString("plugin.starting"));
    	loadConfig();
    	
        loadI18N();
    }

    @Listener
    public void onGameStarting(GameInitializationEvent event) {
    	LOGGER.info("配置加载完成,{}开始连接数据库~",NAME);
    	influxDB=new InfluxDBConnection(
    			mainConfig.getUser()
    			,mainConfig.getPassword()
    			,mainConfig.getHost()+":"+mainConfig.getPort()
    			,mainConfig.getDatabase()
    			,mainConfig.getRetention_policy()
    			);
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
}
