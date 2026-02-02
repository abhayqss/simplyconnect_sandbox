package com.scnsoft.eldermark.entity.signature;

import java.util.EnumSet;
import java.util.Set;
import java.util.stream.Collectors;

public enum PdcFlowOverlayBoxType {

    SMALLER_SIGNATURE_BOX((short) 1),
    UNEDITABLE_CURRENT_DATE((short) 2),
    CHECKBOX((short) 3),
    LARGER_SIGNATURE_BOX((short) 4),
    TEXT_BOX((short) 5);

    private final short id;


    PdcFlowOverlayBoxType(short id) {
        this.id = id;
    }

    public short getId() {
        return id;
    }

    private static final Set<PdcFlowOverlayBoxType> SIGNATURES = EnumSet.of(SMALLER_SIGNATURE_BOX, LARGER_SIGNATURE_BOX);

    public static Set<Short> signatureBoxIds() {
        return SIGNATURES.stream()
                .map(PdcFlowOverlayBoxType::getId)
                .collect(Collectors.toSet());
    }
}
