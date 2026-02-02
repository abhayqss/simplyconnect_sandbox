package com.scnsoft.eldermark.service.inbound.healthpartners;

import java.io.File;
import java.util.Comparator;
import java.util.regex.Pattern;

public class HpFileSortComparator implements Comparator<File> {

    static final Pattern PATTERN = Pattern.compile(".*_(\\d{8})_\\d{6}.txt");

    @Override
    public int compare(File o1, File o2) {
        var name1 = o1.getName();
        var name2 = o2.getName();

        var daysCompare = dateDayPart(name1).compareTo(dateDayPart(name2));

        if (daysCompare != 0) {
            return daysCompare;
        }
        return name1.compareTo(name2);
    }

    private String dateDayPart(String name) {
        var mather = PATTERN.matcher(name);
        if (!mather.matches()) {
            return "";
        }
        return mather.group(1);
    }
}
