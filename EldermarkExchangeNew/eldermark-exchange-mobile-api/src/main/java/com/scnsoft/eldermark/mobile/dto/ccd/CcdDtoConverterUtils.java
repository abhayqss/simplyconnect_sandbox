package com.scnsoft.eldermark.mobile.dto.ccd;

import com.scnsoft.eldermark.entity.Client;

public class CcdDtoConverterUtils {

    public static DataSource createDataSource(Client client) {
        var dataSource = new DataSource();
        dataSource.setOrganizationId(client.getOrganizationId());
        dataSource.setOrganizationName(client.getOrganization().getName());
        dataSource.setOrganizationLogoName(client.getOrganization().getMainLogoPath());
        if (client.getCommunity() != null) {
            dataSource.setCommunityId(client.getCommunityId());
            dataSource.setCommunityName(client.getCommunity().getName());
            dataSource.setCommunityLogoName(client.getCommunity().getMainLogoPath());
        }
        return dataSource;
    }

}
