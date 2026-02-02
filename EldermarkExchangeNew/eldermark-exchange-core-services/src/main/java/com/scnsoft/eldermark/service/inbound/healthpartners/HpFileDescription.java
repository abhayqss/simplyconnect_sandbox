package com.scnsoft.eldermark.service.inbound.healthpartners;

import com.scnsoft.eldermark.entity.inbound.healthpartners.HpFileType;

public class HpFileDescription {
    private final HpFileType type;
    private final HpFileSource source;

    public HpFileDescription(HpFileType type, HpFileSource source) {
        this.type = type;
        this.source = source;
    }

    public HpFileType getType() {
        return type;
    }

    public HpFileSource getSource() {
        return source;
    }
}
