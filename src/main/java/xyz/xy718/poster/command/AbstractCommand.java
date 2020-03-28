package xyz.xy718.poster.command;

import java.lang.reflect.Modifier;
import java.util.List;

import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.command.spec.CommandSpec.Builder;
import com.google.common.collect.Lists;
import lombok.Getter;
import xyz.xy718.poster.annotation.Permission;
import xyz.xy718.poster.annotation.RegisterCommand;


public abstract class AbstractCommand implements CommandExecutor {

    // A period separated list of parent commands, starting with the prefix. Period terminated.
    private final String commandPath;

    @Getter protected final String permissions;
    @Getter  private final String[] aliases;
    @Getter private final boolean isRoot;
    @Getter Builder builder;

	public AbstractCommand() {
        this.commandPath = getSubcommandOf();

        // Now, if this is
        RegisterCommand rc = this.getClass().getAnnotation(RegisterCommand.class);

        this.isRoot = rc.value().length == 1;
        List<String> a = rc == null ? Lists.newArrayList() : Lists.newArrayList(rc.value());

        this.aliases = a.toArray(new String[0]);

        this.permissions = this.getClass().getAnnotation(Permission.class).value().getPermission();

        this.builder=CommandSpec.builder()
    			.permission(this.permissions)
    			.executor(this);
    }
	
	public String getCommandPath() {
		return this.commandPath;
	}
    private String getSubcommandOf() {
        StringBuilder sb = new StringBuilder();
        getSubcommandOf(this.getClass(), sb, false);
        return sb.toString();
    }

    private void getSubcommandOf(Class<? extends AbstractCommand> c, StringBuilder sb, boolean appendPeriod) {
        // Get subcommand alias, if any.
        RegisterCommand rc = c.getAnnotation(RegisterCommand.class);
        if (!Modifier.isAbstract( rc.subcommandOf().getModifiers()) && rc.subcommandOf() != this.getClass()) {
            getSubcommandOf(rc.subcommandOf(), sb, true);
        }

        sb.append(rc.value()[0]);
        if (appendPeriod) {
            sb.append(".");
        }
    }
}
