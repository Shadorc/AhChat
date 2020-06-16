package com.shadorc.ahchat.command;

import java.util.Collections;
import java.util.List;

public abstract class BaseCmd {

    private final List<String> names;
    private final String alias;

    public BaseCmd(final List<String> names, final String alias) {
        this.names = names;
        this.alias = alias;
    }

    protected BaseCmd(final List<String> names) {
        this(names, null);
    }

    public String getName() {
        return this.names.get(0);
    }

    public List<String> getNames() {
        return Collections.unmodifiableList(this.names);
    }

    public String getAlias() {
        return this.alias;
    }

}
