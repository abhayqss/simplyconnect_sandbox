package com.scnsoft.eldermark.exchange.normalizers;

import com.scnsoft.eldermark.exchange.ExchangeUtils;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class PersonNamesNormalizerImpl implements PersonNamesNormalizer {
    private static final Map<String, String> NAMES_REPLACEMENT_MAP = new HashMap<String, String>();

    static {
        NAMES_REPLACEMENT_MAP.put("'", "");
        NAMES_REPLACEMENT_MAP.put(" ", "");
        NAMES_REPLACEMENT_MAP.put("-", "");
    }

    @Override
    public String normalizeName(String name) {
        if (name == null) {
            return null;
        }

        return ExchangeUtils.replace(name, NAMES_REPLACEMENT_MAP).toLowerCase();
    }
}
