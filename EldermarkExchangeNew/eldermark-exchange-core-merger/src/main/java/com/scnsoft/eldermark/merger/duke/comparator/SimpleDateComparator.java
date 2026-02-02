package com.scnsoft.eldermark.merger.duke.comparator;

import no.priv.garshol.duke.Comparator;

public class SimpleDateComparator implements Comparator {

    private final String datePattern = "\\d{4}-\\d{2}-\\d{2}";

    @Override
    public boolean isTokenized() {
        return false;
    }

    @Override
    public double compare(String s1, String s2) {

        if (!s1.matches(datePattern) || !s2.matches(datePattern)) {
            System.out.println("ERROR! Wrong date format: " + s1 + " or " + s2 + "!");
            System.out.println("Should be yyyy-MM-dd!");
            return 0.0;
        }

        if (s1.equals(s2)) {
            return 1.0;
        }

        int matches = 0;

        String[] dt1str = {s1.substring(0, 4), s1.substring(5, 7), s1.substring(8, 10)};
        String[] dt2str = {s2.substring(0, 4), s2.substring(5, 7), s2.substring(8, 10)};

        for (int i = 0; i < 3; i++) {
            if (dt1str[i].equals(dt2str[i])) {
                matches++;
            }
        }

        switch (matches) {
            case 3:
                return 1.0;
            case 2:
                return 0.5;
            default:
                return 0.0;
        }
    }
}
