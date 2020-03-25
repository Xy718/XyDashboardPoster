package xyz.xy718.poster.command;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.text.Text;

import xyz.xy718.poster.annotation.Permission;
import xyz.xy718.poster.annotation.RegisterCommand;
import xyz.xy718.poster.config.Permissions;

@Permission(Permissions.DEBUG)
@RegisterCommand({"xydp","debug"})
public class DebugCommand extends AbstractCommand{

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		src.sendMessage(Text.of(this.getClass().getName()));
		return CommandResult.success();
	}

}
