package com.scnsoft.eldermark.web.taglib;

public class SimplyConnectFunctions {

    /**
     * replaces all spaces with a single space
     * @param source
     * @return
     */
    public static String replaceAllSpaces(final String source) {
        final String replaceAll = source.replaceAll("\\s+", " ");
        return replaceAll;
    }

    /**
     * truncates string to given number of characters
     * leaves only whole words
     * @param source
     * @param maximumSymbolNumber
     * @return
     */
    public static String truncateWords(final String source, int maximumSymbolNumber) {
        final String truncatedString = source.substring(0, maximumSymbolNumber);
        final char lastChar = truncatedString.charAt(maximumSymbolNumber - 1);
        int lastIndex  = maximumSymbolNumber;
        if (!Character.isSpaceChar(lastChar)) {
            lastIndex = truncatedString.lastIndexOf(' ');
        }
        return truncatedString.substring(0, lastIndex);
    }
}
