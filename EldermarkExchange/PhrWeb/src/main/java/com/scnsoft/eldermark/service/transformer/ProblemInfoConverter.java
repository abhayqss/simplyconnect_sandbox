package com.scnsoft.eldermark.service.transformer;

import com.scnsoft.eldermark.entity.ProblemObservation;
import com.scnsoft.eldermark.service.transformer.populator.Populator;
import com.scnsoft.eldermark.web.entity.ProblemInfoDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class ProblemInfoConverter implements Converter<ProblemObservation, ProblemInfoDto> {

    @Autowired
    private Populator<ProblemObservation, ProblemInfoDto> problemInfoItemPopulator;

    @Override
    public ProblemInfoDto convert(ProblemObservation problemObservation) {
        if (problemObservation == null) {
            return null;
        }
        final ProblemInfoDto problemInfoDto = new ProblemInfoDto();
        getProblemInfoItemPopulator().populate(problemObservation, problemInfoDto);
        return problemInfoDto;
    }

    public Populator<ProblemObservation, ProblemInfoDto> getProblemInfoItemPopulator() {
        return problemInfoItemPopulator;
    }

    public void setProblemInfoItemPopulator(Populator<ProblemObservation, ProblemInfoDto> problemInfoItemPopulator) {
        this.problemInfoItemPopulator = problemInfoItemPopulator;
    }
}
