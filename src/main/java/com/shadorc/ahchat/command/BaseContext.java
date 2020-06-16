package com.shadorc.ahchat.command;

public abstract class BaseContext {

    private final String command;
    private final String arg;

    public BaseContext(final String message) {
        final String[] splitCmd = message.split(" ", 2);
        this.command = splitCmd[0].toLowerCase();
        this.arg = (splitCmd.length > 1) ? splitCmd[1] : null;
    }

    public String getCommandName() {
        return this.command;
    }

    public String getArg() {
        return this.arg;
    }

}
