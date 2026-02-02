package com.scnsoft.eldermark.shared.ccd.converters;

import org.apache.commons.lang3.StringUtils;
import org.dozer.DozerConverter;
import org.dozer.Mapper;
import org.dozer.MapperAware;

import java.util.ArrayList;
import java.util.List;

public class ListConverter extends DozerConverter<List, String> implements MapperAware {

    private Mapper mapper;

    public ListConverter() throws Exception {
        this(List.class, String.class);
    }

    public ListConverter(Class<List> prototypeA, Class<String> prototypeB) {
        super(prototypeA, prototypeB);
    }

    @Override
    public String convertTo(List source, String destination) {
        if (source == null) return null;

        List<String> result = new ArrayList<String>();
        for(Object obj : source) {
            result.add(mapper.map(obj, String.class));
        }

        return StringUtils.join(result, "; ");
    }

    @Override
    public List convertFrom(String source, List destination) {
        throw new UnsupportedOperationException();
    }

    public void setMapper(Mapper mapper) {
        this.mapper = mapper;
    }
}
