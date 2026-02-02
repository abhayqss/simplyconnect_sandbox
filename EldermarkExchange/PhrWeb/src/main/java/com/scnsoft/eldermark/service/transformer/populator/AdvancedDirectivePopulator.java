package com.scnsoft.eldermark.service.transformer.populator;

import com.scnsoft.eldermark.entity.AdvanceDirective;
import com.scnsoft.eldermark.entity.AdvanceDirectiveDocument;
import com.scnsoft.eldermark.entity.Participant;
import com.scnsoft.eldermark.service.transformer.DataSourceConverter;
import com.scnsoft.eldermark.service.transformer.util.Converters;
import com.scnsoft.eldermark.web.entity.AdvanceDirectiveDto;
import com.scnsoft.eldermark.web.entity.ExternalDocumentDto;
import com.scnsoft.eldermark.web.entity.ParticipantListItemDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AdvancedDirectivePopulator implements Populator<AdvanceDirective, AdvanceDirectiveDto> {

    @Autowired
    private DataSourceConverter dataSourceConverter;

    @Autowired
    private Converter<Participant, ParticipantListItemDto> participantListItemConverter;

    @Autowired
    private Converter<AdvanceDirectiveDocument, ExternalDocumentDto> advancedDirectiveDocumentConverter;

    @Override
    public void populate(final AdvanceDirective src, final AdvanceDirectiveDto target) {
        if (src == null) {
            return;
        }
        if (src.getResident() != null) {
            target.setDataSource(getDataSourceConverter().convert(src.getDatabase(), src.getResident().getId()));
        }

        final List<ParticipantListItemDto> participantListItemDtos = Converters.convertAll(src.getVerifiers(), getParticipantListItemConverter());
        participantListItemDtos.add(getParticipantListItemConverter().convert(src.getCustodian()));
        target.setParticipants(participantListItemDtos);

        target.setExternalDocuments(Converters.convertAll(src.getReferenceDocuments(), getAdvancedDirectiveDocumentConverter()));

    }

    public DataSourceConverter getDataSourceConverter() {
        return dataSourceConverter;
    }

    public void setDataSourceConverter(final DataSourceConverter dataSourceConverter) {
        this.dataSourceConverter = dataSourceConverter;
    }

    public Converter<Participant, ParticipantListItemDto> getParticipantListItemConverter() {
        return participantListItemConverter;
    }

    public void setParticipantListItemConverter(final Converter<Participant, ParticipantListItemDto> participantListItemConverter) {
        this.participantListItemConverter = participantListItemConverter;
    }

    public Converter<AdvanceDirectiveDocument, ExternalDocumentDto> getAdvancedDirectiveDocumentConverter() {
        return advancedDirectiveDocumentConverter;
    }

    public void setAdvancedDirectiveDocumentConverter(final Converter<AdvanceDirectiveDocument, ExternalDocumentDto> advancedDirectiveDocumentConverter) {
        this.advancedDirectiveDocumentConverter = advancedDirectiveDocumentConverter;
    }
}
