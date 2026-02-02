package com.scnsoft.eldermark.exchange.validators;

import com.scnsoft.eldermark.exchange.dao.target.CcdCodeDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class CachingCcdCodesValidatorImpl implements CcdCodesValidator {
    private static final int CACHE_SIZE = 2000;
    private final Map<Long, Void> ccdCodesCache = new LinkedHashMap<Long, Void>() {
        @Override
        protected boolean removeEldestEntry(Map.Entry<Long, Void> eldest) {
            return size() > CACHE_SIZE;
        }
    };

    @Autowired
    private CcdCodeDao ccdCodeDao;

    @Override
    public boolean validate(long ccdCodeId) {
        boolean isValid;
        if (ccdCodesCache.containsKey(ccdCodeId)) {
            isValid = true;
        } else {
            isValid = ccdCodeDao.isCcdCodeExists(ccdCodeId);
            if (isValid) {
                ccdCodesCache.put(ccdCodeId, null);
            }
        }
        return isValid;
    }
}
