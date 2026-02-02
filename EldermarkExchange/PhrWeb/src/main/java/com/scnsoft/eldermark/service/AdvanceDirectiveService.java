package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.entity.AdvanceDirective;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Collection;

public interface AdvanceDirectiveService {
    Page<AdvanceDirective> getAdvanceDirectivesForResidents(final Collection<Long> residentIds, final Pageable pageable);

    AdvanceDirective getAdvanceDirective(Long advanceDirectiveId);
}
