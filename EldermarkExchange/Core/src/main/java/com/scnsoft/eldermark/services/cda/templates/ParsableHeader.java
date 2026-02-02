package com.scnsoft.eldermark.services.cda.templates;

import com.scnsoft.eldermark.entity.Resident;

public interface ParsableHeader<H, D> {
    D parseSection(Resident resident, H header);
}
