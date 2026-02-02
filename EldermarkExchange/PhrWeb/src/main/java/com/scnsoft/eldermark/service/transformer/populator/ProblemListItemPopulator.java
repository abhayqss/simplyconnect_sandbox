package com.scnsoft.eldermark.service.transformer.populator;

import com.scnsoft.eldermark.entity.ProblemObservation;
import com.scnsoft.eldermark.web.entity.ListItemDto;
import org.springframework.stereotype.Component;

@Component
public class ProblemListItemPopulator implements Populator<ProblemObservation, ListItemDto> {
    @Override
    public void populate(final ProblemObservation src, final ListItemDto target) {
        if (src == null) {
            return;
        }
        target.setId(src.getId());
        target.setName(src.getProblemName());
    }
}
