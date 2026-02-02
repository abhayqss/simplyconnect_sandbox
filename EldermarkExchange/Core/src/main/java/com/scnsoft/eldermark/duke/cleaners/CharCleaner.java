package com.scnsoft.eldermark.duke.cleaners;

import no.priv.garshol.duke.Cleaner;

/**
 * A cleaner which removes leading and trailing whitespace, without
 * making any other changes.
 */
public class CharCleaner implements Cleaner {

    String chars;
    String separator = "|";


    public String clean(String value) {
        value=value.toLowerCase().trim();
        String[] charArray = chars.split(separator);
        for (String ch:charArray) {
            value = value.replace(ch,"");
        }
        return value;
    }

    public void setChars(String chars) {
        this.chars = chars;
    }
}