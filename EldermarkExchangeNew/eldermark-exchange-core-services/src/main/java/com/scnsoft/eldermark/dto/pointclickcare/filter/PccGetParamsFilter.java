package com.scnsoft.eldermark.dto.pointclickcare.filter;

import com.scnsoft.eldermark.util.CareCoordinationUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Collection;
import java.util.Objects;

public abstract class PccGetParamsFilter {

    public MultiValueMap<String, String> toParamsMap() {
        var map = new LinkedMultiValueMap<String, String>();
        fillParams(map);
        return map;
    }

    protected abstract void fillParams(MultiValueMap<String, String> map);

    protected void addNonNull(MultiValueMap<String, String> map, String key, Object value) {
        if (value != null) {
            map.add(key, value.toString());
        }
    }

    protected void addComaSeparated(MultiValueMap<String, String> map, String key, Collection<?> values) {
        if (CollectionUtils.isNotEmpty(values)) {
            map.add(key, CareCoordinationUtils.concat(",", values.stream().map(Objects::toString)));
        }
    }
}
