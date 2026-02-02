package com.scnsoft.eldermark.service.transformer.populator;

import com.scnsoft.eldermark.entity.Database;
import com.scnsoft.eldermark.web.entity.DataSourceDto;
import org.springframework.stereotype.Component;

@Component
public class DataSourcePopulator implements Populator<Database, DataSourceDto> {

    @Override
    public void populate(final Database src, final DataSourceDto target) {
        if (src == null) {
            return;
        }
        target.setId(src.getId());
        target.setName(src.getName());
    }
}
