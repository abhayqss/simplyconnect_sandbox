package com.scnsoft.eldermark.matcher.duke.cleaners;

import no.priv.garshol.duke.Cleaner;

public class RegexpCharCleaner implements Cleaner {

    String regexp;

    public String clean(String value) {
        value=value.toLowerCase().trim();
        return value.replaceAll(regexp,"");
    }

    public void setRegexp(String regexp) {
        this.regexp = regexp;
    }
}