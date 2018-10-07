package com.djrapitops.plan.command;

import com.djrapitops.plan.command.commands.*;
import com.djrapitops.plan.command.commands.manage.ManageConDebugCommand;
import com.djrapitops.plan.command.commands.manage.ManageRawDataCommand;
import com.djrapitops.plan.system.locale.Locale;
import com.djrapitops.plan.system.locale.lang.DeepHelpLang;
import com.djrapitops.plan.system.settings.Permissions;
import com.djrapitops.plugin.command.ColorScheme;
import com.djrapitops.plugin.command.CommandNode;
import com.djrapitops.plugin.command.CommandType;
import com.djrapitops.plugin.command.TreeCmdNode;
import dagger.Lazy;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * TreeCommand for the /plan command, and all subcommands.
 * <p>
 * Uses the Abstract Plugin Framework for easier command management.
 *
 * @author Rsl1122
 * @since 1.0.0
 */
@Singleton
public class PlanBungeeCommand extends TreeCmdNode {

    private final NetworkCommand networkCommand;
    private final ListServersCommand listServersCommand;
    private final ListPlayersCommand listPlayersCommand;
    private final RegisterCommand registerCommand;
    private final Lazy<WebUserCommand> webUserCommand;
    private final ManageConDebugCommand conDebugCommand;
    private final ManageRawDataCommand rawDataCommand;
    private final BungeeSetupToggleCommand setupToggleCommand;
    private final ReloadCommand reloadCommand;
    private final DisableCommand disableCommand;

    private boolean commandsRegistered;

    @Inject
    public PlanBungeeCommand(
            ColorScheme colorScheme,
            Locale locale,
            // Group 1
            NetworkCommand networkCommand,
            ListServersCommand listServersCommand,
            ListPlayersCommand listPlayersCommand,
            // Group 2
            RegisterCommand registerCommand,
            Lazy<WebUserCommand> webUserCommand,
            // Group 3
            ManageConDebugCommand conDebugCommand,
            ManageRawDataCommand rawDataCommand,
            BungeeSetupToggleCommand setupToggleCommand,
            ReloadCommand reloadCommand,
            DisableCommand disableCommand
    ) {
        super("planbungee", Permissions.MANAGE.getPermission(), CommandType.CONSOLE, null);

        commandsRegistered = false;

        this.networkCommand = networkCommand;
        this.listServersCommand = listServersCommand;
        this.listPlayersCommand = listPlayersCommand;
        this.registerCommand = registerCommand;
        this.webUserCommand = webUserCommand;
        this.conDebugCommand = conDebugCommand;
        this.rawDataCommand = rawDataCommand;
        this.setupToggleCommand = setupToggleCommand;
        this.reloadCommand = reloadCommand;
        this.disableCommand = disableCommand;

        setColorScheme(colorScheme);
        setInDepthHelp(locale.getArray(DeepHelpLang.PLAN));
    }

    public void registerCommands() {
        if (commandsRegistered) {
            return;
        }

        CommandNode[] analyticsGroup = {
                networkCommand,
                listServersCommand,
                listPlayersCommand
        };
        CommandNode[] webGroup = {
                registerCommand,
                webUserCommand.get()
        };
        CommandNode[] manageGroup = {
                conDebugCommand,
                rawDataCommand,
                setupToggleCommand,
                reloadCommand,
                disableCommand
        };
        setNodeGroups(analyticsGroup, webGroup, manageGroup);
        commandsRegistered = true;
    }
}
