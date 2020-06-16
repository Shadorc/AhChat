package com.shadorc.ahchat;

public class CommandException extends RuntimeException {

    public CommandException(final String message) {
        super(message, null, false, false);
    }

}
