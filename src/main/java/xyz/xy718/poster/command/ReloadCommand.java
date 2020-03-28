package xyz.xy718.poster.command;

import org.slf4j.Logger;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;

import xyz.xy718.poster.PosterManager;
import xyz.xy718.poster.XyDashboardPosterPlugin;
import xyz.xy718.poster.annotation.Permission;
import xyz.xy718.poster.annotation.RegisterCommand;
import xyz.xy718.poster.config.I18N;
import xyz.xy718.poster.config.Permissions;
/**
 * 重载插件配置及相关收集器推送器
 * @author Xy718
 *
 */
@Permission(Permissions.RELOAD)
@RegisterCommand({"xydp","reload"})
public class ReloadCommand extends AbstractCommand {

	private static Logger LOGGER=XyDashboardPosterPlugin.LOGGER;
	
	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		//重载本地配置文件
		XyDashboardPosterPlugin.getMainConfig().reload();
		XyDashboardPosterPlugin.get().loadI18N();
		//停止所有的收集器及推送器
		XyDashboardPosterPlugin.getPosterManager().stopAllGraf();
		XyDashboardPosterPlugin.getPosterManager().stopAllPoster();
		
		//重新配置influxDB
		XyDashboardPosterPlugin.reloadInfluxDB();
		
		//重载所有的收集器和推送器
    	if(XyDashboardPosterPlugin.getInfluxDB().ping()) {
    		//ping通才可以使用
        	XyDashboardPosterPlugin.setPosterManager(new PosterManager(XyDashboardPosterPlugin.get()));
    	}else {
    		LOGGER.error(I18N.getString("error.influxdb.ping"));
    	}
    	src.sendMessage(I18N.getPluginNameText("plugin.reloadplugin", XyDashboardPosterPlugin.NAME));
		return CommandResult.success();
	}

}
