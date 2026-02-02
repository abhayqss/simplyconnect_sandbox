package com.scnsoft.eldermark.exchange.normalizers;

public interface PersonPhonesNormalizer {
    /**
     * Returns person phone in its normalized form.
     *
     * @param phone person phone (may be null)
     * @return normalized value for phone (or null if phone is null)
     */
    String normalizePhone(String phone);
}
