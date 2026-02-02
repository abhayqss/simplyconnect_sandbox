package com.scnsoft.eldermark.service.document.templates.cda.factory.header;

import com.scnsoft.eldermark.entity.Client;

public interface ParsableHeader<H, D> {
    D parseSection(Client client, H header);
}
