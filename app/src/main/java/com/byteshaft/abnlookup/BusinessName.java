package com.byteshaft.abnlookup;

import java.io.Serializable;

public class BusinessName implements Serializable {

    private String name;
    private String from;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }
}
