package com.scnsoft.eldermark.merger.duke;

import no.priv.garshol.duke.Cleaner;

public class RegexpCharCleaner implements Cleaner {

    private String regexp;

    public String clean(String value) {
        return value.toLowerCase()
                .trim()
                .replaceAll(regexp, "");
    }

    public void setRegexp(String regexp) {
        this.regexp = regexp;
    }
}
