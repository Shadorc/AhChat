package com.shadorc.ahchat.command;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class CommandManager<T extends BaseCmd<?>> {

    private final Map<String, T> commandsMap;

    public CommandManager(final T... cmds) {
        this.commandsMap = this.initialize(cmds);
    }

    private Map<String, T> initialize(final T... cmds) {
        final Map<String, T> map = new LinkedHashMap<>();
        for (final T cmd : cmds) {
            for (final String name : cmd.getNames()) {
                if (map.putIfAbsent(name, cmd) != null) {
                    System.err.println(String.format("Command name collision between %s and %s",
                            name, map.get(name).getClass().getSimpleName()));
                }
            }
        }
        System.out.println(String.format("%d command(s) initialized", cmds.length));
        return Collections.unmodifiableMap(map);
    }

    public T getCommand(final String cmdName) {
        return this.commandsMap.get(cmdName);
    }

    public Collection<T> getCommands() {
        return this.commandsMap.values();
    }

}
