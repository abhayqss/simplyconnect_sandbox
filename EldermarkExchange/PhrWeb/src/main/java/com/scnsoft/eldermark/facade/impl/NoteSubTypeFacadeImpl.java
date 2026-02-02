package com.scnsoft.eldermark.facade.impl;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.scnsoft.eldermark.facade.NoteSubTypeFacade;
import com.scnsoft.eldermark.services.carecoordination.NoteSubTypeService;
import com.scnsoft.eldermark.shared.carecoordination.notes.NoteSubTypeDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class NoteSubTypeFacadeImpl implements NoteSubTypeFacade {

    @Autowired
    private Converter<NoteSubTypeDto, com.scnsoft.eldermark.web.entity.notes.NoteSubTypeDto> noteSubTypeDtoConverter;

    @Autowired
    private NoteSubTypeService noteSubTypeService;

    @Override
    public List<com.scnsoft.eldermark.web.entity.notes.NoteSubTypeDto> getAllPhrVisibleSubTypes() {
        return FluentIterable.from(noteSubTypeService.getAllSubTypes()).filter(
            new Predicate<NoteSubTypeDto>(){
                @Override
                public boolean apply(NoteSubTypeDto input) {
                    return !input.isPhrHidden();
                }
            }
        ).transform(new Function<NoteSubTypeDto, com.scnsoft.eldermark.web.entity.notes.NoteSubTypeDto>() {

            @Override
            public com.scnsoft.eldermark.web.entity.notes.NoteSubTypeDto apply(NoteSubTypeDto noteSubTypeDto) {
                return noteSubTypeDtoConverter.convert(noteSubTypeDto);
            }
        }).toList();
    }
}
