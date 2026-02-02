package com.scnsoft.eldermark.service.transformer.populator;

import com.scnsoft.eldermark.entity.Encounter;
import com.scnsoft.eldermark.entity.Participant;
import com.scnsoft.eldermark.entity.ProblemObservation;
import com.scnsoft.eldermark.entity.ProcedureActivity;
import com.scnsoft.eldermark.service.transformer.util.Converters;
import com.scnsoft.eldermark.util.EldermarkCollectionUtils;
import com.scnsoft.eldermark.web.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EncounterPopulator implements Populator<Encounter, EncounterDto> {

    @Autowired
    private Converter<ProblemObservation, ListItemDto> problemListItemConverter;

    @Autowired
    private Converter<Encounter, EncounterAdditionalInfoDto> encounterAdditionalInfoConverter;

    @Autowired
    private Converter<ProcedureActivity, ProcedureListItemDto> procedureListConverter;

    @Autowired
    Converter<Participant, ParticipantListItemDto> participantListItemConverter;

    @Override
    public void populate(final Encounter src, final EncounterDto target) {
        if (src == null) {
            return;
        }
        target.setStatus(null);//TODO when data in DB
        target.setEndDateTime(null);//TODO when data in DB
        target.setMode(null);//TODO when data in DB
        target.setPriority(null);//TODO when data in DB


        target.setProblems(EldermarkCollectionUtils.singltoneListOfNullableElement(problemListItemConverter.convert(src.getProblemObservation())));
        target.setProcedures(Converters.convertAll(src.getProcedureActivities(), procedureListConverter));

        target.setPatientInstructions(null);//TODO when data in DB

        final List<ParticipantListItemDto> participantListItemDtos = Converters.convertAll(src.getResident().getParticipants(), participantListItemConverter);
        target.setParticipants(participantListItemDtos);

        target.setAdditionalInfo(encounterAdditionalInfoConverter.convert(src));

    }

}
