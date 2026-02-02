package com.scnsoft.eldermark.service.transformer;

import com.scnsoft.eldermark.entity.ProblemObservation;
import com.scnsoft.eldermark.service.transformer.populator.Populator;
import com.scnsoft.eldermark.web.entity.ListItemDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class ProblemListItemConverter implements Converter<ProblemObservation, ListItemDto> {

    @Autowired
    private Populator<ProblemObservation, ListItemDto> problemListItemPopulator;

    @Override
    public ListItemDto convert(final ProblemObservation problemObservation) {
        if (problemObservation == null) {
            return null;
        }
        final ListItemDto listItemDto = new ListItemDto();
        getProblemListItemPopulator().populate(problemObservation, listItemDto);
        return listItemDto;
    }

    public Populator<ProblemObservation, ListItemDto> getProblemListItemPopulator() {
        return problemListItemPopulator;
    }

    public void setProblemListItemPopulator(final Populator<ProblemObservation, ListItemDto> problemListItemPopulator) {
        this.problemListItemPopulator = problemListItemPopulator;
    }
}
