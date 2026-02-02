package com.scnsoft.eldermark.service.healthpartners.client;

import com.scnsoft.eldermark.entity.Client;

public interface HpClientFactory<T extends HpClientInfo> {

    Client create(T clientInfo);

}
