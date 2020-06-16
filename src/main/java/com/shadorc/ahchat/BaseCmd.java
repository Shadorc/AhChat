package com.shadorc.ahchat;

import java.util.List;

public abstract class BaseCmd {

    private final List<String> names;
    private final String alias;

    protected BaseCmd(List<String> names, String alias) {
        this.names = names;
        this.alias = alias;
    }

    protected BaseCmd(List<String> names) {
        this(names, null);
    }

    public String getName() {
        return this.names.get(0);
    }

    public List<String> getNames() {
        return this.names;
    }

    public String getAlias() {
        return this.alias;
    }

    public abstract void execute(final Context context);

    public abstract void displayHelp(final Context context);

}
