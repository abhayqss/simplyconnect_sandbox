package com.scnsoft.eldermark.service.transformer;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.scnsoft.eldermark.entity.AdmitIntakeResidentDate;
import com.scnsoft.eldermark.entity.NoteSubType;
import com.scnsoft.eldermark.services.carecoordination.NoteSubTypeService;
import com.scnsoft.eldermark.web.entity.AdmitDateDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AdmitIntakeDateDtoConverter implements Converter<AdmitIntakeResidentDate, AdmitDateDto> {

    @Autowired
    private NoteSubTypeService noteSubTypeService;

    @Override
    public AdmitDateDto convert(AdmitIntakeResidentDate admitIntakeResidentDate) {
        final AdmitDateDto result = new AdmitDateDto();
        result.setId(admitIntakeResidentDate.getId());
        result.setValue(admitIntakeResidentDate.getAdmitIntakeDate());
        List<NoteSubType.FollowUpCode> taken = noteSubTypeService.getTakenFollowUpForAdmitDate(admitIntakeResidentDate.getResidentId(), admitIntakeResidentDate.getId());
        result.setTakenNoteTypeCodes(FluentIterable.from(taken).transform(new Function<NoteSubType.FollowUpCode, String>() {
            @Override
            public String apply(NoteSubType.FollowUpCode followUpCode) {
                return followUpCode.getCode();
            }
        }).toList());
        return result;
    }
}
