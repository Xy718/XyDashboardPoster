package xyz.xy718.poster.command;

import java.lang.reflect.Modifier;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Nullable;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.args.parsing.InputTokenizer;
import org.spongepowered.api.command.dispatcher.SimpleDispatcher;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.command.spec.CommandSpec.Builder;
import org.spongepowered.api.text.Text;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import co.aikar.timings.Timing;
import lombok.Getter;
import xyz.xy718.poster.XyDashboardPosterPlugin;
import xyz.xy718.poster.annotation.Permission;
import xyz.xy718.poster.annotation.RegisterCommand;


public abstract class AbstractCommand implements CommandExecutor {
	private static final InputTokenizer tokeniser = InputTokenizer.quotedStrings(false);

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
