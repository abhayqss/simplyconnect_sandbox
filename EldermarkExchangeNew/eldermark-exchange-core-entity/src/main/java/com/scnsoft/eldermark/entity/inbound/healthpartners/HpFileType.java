package com.scnsoft.eldermark.entity.inbound.healthpartners;

import java.util.function.Supplier;


public enum HpFileType {

//    CONSANA_MEDICAL_YYYYMMDD_HHMMSS.txt (medical)
//    CONSANA_RX_YYYYMMDD_HHMMSS.txt (pharmacy)
//    CONSANA_TERMED_MEMBERS_YYYYMMDD_HHMMSS.txt (termed members)

    CONSANA_RX("Prescription claims", HpRxClaimFileProcessingSummary::new),
    CONSANA_TERMED_MEMBERS("Termed members", HpTermedMembersFileProcessingSummary::new),
    CONSANA_MEDICAL("Medical claims", HpMedClaimFileProcessingSummary::new);

    private final String displayName;
    private final Supplier<HpFileProcessingSummary<?>> summaryCreator;

    HpFileType(String displayName,
               Supplier<HpFileProcessingSummary<?>> summaryCreator) {
        this.displayName = displayName;
        this.summaryCreator = summaryCreator;
    }

    public String getDisplayName() {
        return displayName;
    }

    public HpFileProcessingSummary<?> createFileSummary() {
        return summaryCreator.get();
    }
}
