package com.scnsoft.eldermark.entity.inbound.healthpartners;

public class HpMedClaimFileProcessingSummary extends HpFileProcessingSummary<HpMedClaimProcessingSummary> {
    protected HpMedClaimFileProcessingSummary() {
        super(HpFileType.CONSANA_MEDICAL);
    }
}
