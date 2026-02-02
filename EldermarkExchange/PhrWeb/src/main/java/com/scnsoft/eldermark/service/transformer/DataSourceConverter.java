package com.scnsoft.eldermark.service.transformer;

import com.scnsoft.eldermark.entity.Database;
import com.scnsoft.eldermark.web.entity.DataSourceDto;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class DataSourceConverter {

    public DataSourceDto convert(final Database database, final Long residentId) {

        DataSourceDto dest = new DataSourceDto();
        if (database != null) {
            dest.setId(database.getId());
            dest.setName(database.getName());
        }
        dest.setResidentId(residentId);

        return dest;
    }
}
