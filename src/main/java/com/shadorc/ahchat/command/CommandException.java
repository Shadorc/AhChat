package com.shadorc.ahchat.command;

public class CommandException extends RuntimeException {

    public CommandException(final String message) {
        super(message, null, false, false);
    }

}
