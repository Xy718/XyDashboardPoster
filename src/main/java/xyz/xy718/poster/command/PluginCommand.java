package xyz.xy718.poster.command;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.text.Text;

import xyz.xy718.poster.annotation.Permission;
import xyz.xy718.poster.annotation.RegisterCommand;
import xyz.xy718.poster.config.Permissions;

/**
 * 简单输出一下有关插件的信息
 * @author Xy718
 *
 */
@Permission(Permissions.PLUGIN_INFO)
@RegisterCommand({"xydp"})
public class PluginCommand extends AbstractCommand{

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		src.sendMessage(Text.of("啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊kksk"));
		return CommandResult.success();
	}

}
