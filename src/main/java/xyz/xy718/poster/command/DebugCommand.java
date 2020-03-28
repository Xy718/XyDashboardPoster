package xyz.xy718.poster.command;

import java.io.IOException;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;

import xyz.xy718.poster.XyDashboardPosterPlugin;
import xyz.xy718.poster.annotation.Permission;
import xyz.xy718.poster.annotation.RegisterCommand;
import xyz.xy718.poster.config.I18N;
import xyz.xy718.poster.config.Permissions;
import xyz.xy718.poster.config.XyDashboardPosterConfig;

/**
 * 切换debug输出模式
 * @author Xy718
 *
 */
@Permission(Permissions.DEBUG)
@RegisterCommand({"xydp","debug"})
public class DebugCommand extends AbstractCommand{
	
	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		//获取当前模式
		XyDashboardPosterConfig config=XyDashboardPosterPlugin.getMainConfig();
		//反一下写入
		config.getMainNode().getNode("a-general","logger-debug").setValue(!config.isLogger_debug());
		//保存
		try {
			config.saveConfig();
			
			String mode="Unknown";//模式
			if(config.isLogger_debug()) {
				mode=I18N.getString("plugin.set.debug.open");
			}else {
				mode=I18N.getString("plugin.set.debug.close");
			}
			//通知用户和控制台
			Sponge.getServer().getConsole().sendMessage(I18N.getPluginNameText("plugin.set.debug", mode));
			if(src instanceof Player) {
				src.sendMessage(I18N.getPluginNameText("plugin.set.debug", mode));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return CommandResult.success();
	}

}
