package com.shadorc.ahchat.server;

import com.shadorc.ahchat.BaseCmd;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class CommandManager {

    private static CommandManager instance;

    static {
        CommandManager.instance = new CommandManager();
    }

    private final Map<String, BaseCmd> commandsMap;

    private CommandManager() {
        this.commandsMap = this.initialize();
    }

    private Map<String, BaseCmd> initialize(final BaseCmd... cmds) {
        final Map<String, BaseCmd> map = new LinkedHashMap<>();
        for (final BaseCmd cmd : cmds) {
            for (final String name : cmd.getNames()) {
                if (map.putIfAbsent(name, cmd) != null) {
                    System.err.println(String.format("Command name collision between %s and %s",
                            name, map.get(name).getClass().getSimpleName()));
                }
            }
        }
        System.out.println(String.format("%d commands initialized", cmds.length));
        return Collections.unmodifiableMap(map);
    }

    public BaseCmd getCommand(final String cmdName) {
        return this.commandsMap.get(cmdName);
    }

    public Collection<BaseCmd> getCommands() {
        return this.commandsMap.values();
    }

    public static CommandManager getInstance() {
        return CommandManager.instance;
    }

}
