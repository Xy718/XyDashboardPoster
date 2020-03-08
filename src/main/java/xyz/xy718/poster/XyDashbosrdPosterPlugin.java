package xyz.xy718.poster;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

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
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.InventoryArchetype;
import org.spongepowered.api.item.inventory.InventoryArchetypes;
import org.spongepowered.api.item.inventory.InventoryProperty;
import org.spongepowered.api.item.inventory.property.InventoryDimension;
import org.spongepowered.api.item.inventory.property.InventoryTitle;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import com.google.inject.Inject;

import lombok.Getter;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import xyz.xy718.poster.config.I18N;
import xyz.xy718.poster.config.XyDashboardPosterConfig;
import xyz.xy718.poster.schedule.WorldDataPoster;

@Plugin(
id = XyDashbosrdPosterPlugin.PLUGIN_ID
, name = XyDashbosrdPosterPlugin.NAME
, version = XyDashbosrdPosterPlugin.VERSION
, description = XyDashbosrdPosterPlugin.DESCRIPTION
)
public class XyDashbosrdPosterPlugin {
	@Getter public static final String PLUGIN_ID = "@id@";
	@Getter public static final String NAME = "@name@";
	@Getter public static final String VERSION = "@version@";
	@Getter public static final String DESCRIPTION = "@description@";
	

	private static XyDashbosrdPosterPlugin instance;
	
	public static final Logger LOGGER = LoggerFactory.getLogger(NAME);
	
	@Inject
	private PluginContainer container;
	/*
	@Inject
    @ConfigDir(sharedRoot = false)
    private Path configPath;
	
	@Inject
	@DefaultConfig(sharedRoot = false)
	private ConfigurationLoader<CommentedConfigurationNode> configLoader;

	private static XyDashboardPosterConfig mainConfig;
*/
	private static PosterManager posterManager;
	
    public XyDashbosrdPosterPlugin() {
    	if (instance != null)
			throw new IllegalStateException();
		instance = this;
	}
	
    @Listener
    public void onGamePreStarting(GamePreInitializationEvent event) {
    	LOGGER.info("服务器启动中,先加载配置与语言文件~");
    	//loadConfig();
    	
        //loadI18N();
    }

    @Listener
    public void onGameStarting(GameInitializationEvent event) {
    	LOGGER.info("配置加载完成,{}开始注册事件与指令~",NAME);
	}
    @Listener
    public void onServerStart(GameStartedServerEvent event) {
    	LOGGER.info("服务器启动成功，{}也开始工作了~",NAME);
    	posterManager=new PosterManager(instance);
    }
    
    @Listener
    public void onPluginsReload(GameReloadEvent event) {
    	LOGGER.warn("{}重新加载~",NAME);
    }
	public static XyDashbosrdPosterPlugin get() {
		if (instance == null)
			throw new IllegalStateException("Instance not available");
		return instance;
	}
    public PluginContainer getContainer() {
        return container;
    }

	public Path getConfigDir() {
		//return configPath;
		return null;
	}
	/*
	/**
	 * 加载配置文件
	 
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
	 
	private void loadI18N() {
   	 // init i18n service
        I18N.setLogger(LOGGER);
        I18N.setPlugin(this);
        String localeStr = mainConfig.getNode("lang").getString();
        if (localeStr == null || localeStr.isEmpty()) {
            localeStr = "zh_CN";
        }
        Locale locale = LocaleUtils.toLocale(localeStr);
        I18N.setLocale(locale);
	}

	*/
}
