package com.scnsoft.eldermark.exchange.normalizers;

import com.scnsoft.eldermark.exchange.ExchangeUtils;
import com.scnsoft.eldermark.framework.Utils;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class PersonPhonesNormalizerImpl implements PersonPhonesNormalizer {
    private static final Map<String, String> PHONES_REPLACEMENT_MAP = new HashMap<String, String>();

    static {
        PHONES_REPLACEMENT_MAP.put("'", "");
        PHONES_REPLACEMENT_MAP.put(" ", "");
        PHONES_REPLACEMENT_MAP.put(")", "");
        PHONES_REPLACEMENT_MAP.put("(", "");
        PHONES_REPLACEMENT_MAP.put("-", "");
        PHONES_REPLACEMENT_MAP.put("+", "");
    }

    @Override
    public String normalizePhone(String phone) {
        return phone != null ? ExchangeUtils.replace(phone, PHONES_REPLACEMENT_MAP) : null;
    }
}
