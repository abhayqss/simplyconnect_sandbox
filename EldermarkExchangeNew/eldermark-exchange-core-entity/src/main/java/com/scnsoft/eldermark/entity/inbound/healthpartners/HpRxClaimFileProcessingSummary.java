package com.scnsoft.eldermark.entity.inbound.healthpartners;

public class HpRxClaimFileProcessingSummary extends HpFileProcessingSummary<HpRxClaimProcessingSummary> {

    public HpRxClaimFileProcessingSummary() {
        super(HpFileType.CONSANA_RX);
    }

}
