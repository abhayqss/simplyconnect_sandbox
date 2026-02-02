package com.scnsoft.eldermark.exchange.normalizers;

public interface PersonNamesNormalizer {
    /**
     * Returns person name in its normalized form.
     *
     * @param name person name (may be null)
     * @return normalized value for name (or null if name is null)
     */
    String normalizeName(String name);
}
