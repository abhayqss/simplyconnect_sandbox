package com.scnsoft.eldermark.entity.basic;

import com.scnsoft.eldermark.beans.projection.IdAware;

import java.util.Optional;

public interface HistoryIdsAware extends IdAware, ChainIdAware {

    default Long resolveHistoryId() {
        return Optional.ofNullable(getChainId()).orElseGet(this::getId);
    }
}
