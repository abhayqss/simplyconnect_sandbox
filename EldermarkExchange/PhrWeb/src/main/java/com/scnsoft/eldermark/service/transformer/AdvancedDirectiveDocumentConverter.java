package com.scnsoft.eldermark.service.transformer;

import com.scnsoft.eldermark.entity.AdvanceDirective;
import com.scnsoft.eldermark.entity.AdvanceDirectiveDocument;
import com.scnsoft.eldermark.service.transformer.populator.Populator;
import com.scnsoft.eldermark.web.entity.AdvanceDirectiveInfoDto;
import com.scnsoft.eldermark.web.entity.ExternalDocumentDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class AdvancedDirectiveDocumentConverter implements Converter<AdvanceDirectiveDocument, ExternalDocumentDto> {

    @Autowired
    private Populator<AdvanceDirectiveDocument, ExternalDocumentDto> advancedDirectiveDocumentPopulator;

    @Override
    public ExternalDocumentDto convert(final AdvanceDirectiveDocument advanceDirectiveDocument) {
        if (advanceDirectiveDocument == null) {
            return null;
        }
        final ExternalDocumentDto result = new ExternalDocumentDto();
        getAdvancedDirectiveDocumentPopulator().populate(advanceDirectiveDocument, result);
        return result;
    }

    public Populator<AdvanceDirectiveDocument, ExternalDocumentDto> getAdvancedDirectiveDocumentPopulator() {
        return advancedDirectiveDocumentPopulator;
    }

    public void setAdvancedDirectiveDocumentPopulator(final Populator<AdvanceDirectiveDocument, ExternalDocumentDto> advancedDirectiveDocumentPopulator) {
        this.advancedDirectiveDocumentPopulator = advancedDirectiveDocumentPopulator;
    }
}
