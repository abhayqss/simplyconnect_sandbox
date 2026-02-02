package com.scnsoft.eldermark.duke.comparators;

import no.priv.garshol.duke.comparators.Levenshtein;

//Same as LevenshteinOrig but a bit more strict
public class LastnameComparator extends Levenshtein {

    public double compare(String s1, String s2) {
        int len = Math.min(s1.length(), s2.length());

        // we know that if the outcome here is 0.5 or lower, then the
        // property will return the lower probability. so the moment we
        // learn that probability is 0.5 or lower we can return 0.0 and
        // stop. this optimization makes a perceptible improvement in
        // overall performance.
        int maxlen = Math.max(s1.length(), s2.length());
        if ((double) len / (double) maxlen <= 0.5)
            return 0.0;

        // if the strings are equal we can stop right here.
        if (len == maxlen && s1.equals(s2))
            return 1.0;

        // we couldn't shortcut, so now we go ahead and compute the full
        // metric
        int dist = Math.min(distance(s1, s2), len);

        //to make it a bit more strict reducing the length
        if (len>1) len--;
        return 1.0 - (((double) dist) / ((double) len));
    }
}