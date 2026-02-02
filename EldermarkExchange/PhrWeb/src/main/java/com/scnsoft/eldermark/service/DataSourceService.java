package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.entity.Database;
import com.scnsoft.eldermark.web.entity.DataSourceDto;

/**
 * @author phomal
 * Created on 5/3/2017.
 */
public final class DataSourceService {

    public static DataSourceDto transform(Database database, Long residentId) {
        DataSourceDto dest = new DataSourceDto();

        dest.setId(database.getId());
        dest.setName(database.getName());
        dest.setResidentId(residentId);

        return dest;
    }

}
