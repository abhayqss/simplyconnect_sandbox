package com.scnsoft.eldermark.config;

import org.springframework.format.Formatter;
import org.springframework.lang.NonNull;

import java.util.Locale;

public class StringCrlfToLfFormatter implements Formatter<String> {

    private static final String CRLF = "\r\n";
    private static final String LF = "\n";

    @Override
    @NonNull
    public String parse(String text, @NonNull Locale locale) {
        return text.replaceAll(CRLF, LF);
    }

    @Override
    @NonNull
    public String print(@NonNull String object, @NonNull Locale locale) {
        return object;
    }
}
