package com.scnsoft.eldermark.service.transformer.populator;

import com.scnsoft.eldermark.entity.AdvanceDirectiveDocument;
import com.scnsoft.eldermark.web.entity.ExternalDocumentDto;
import org.springframework.stereotype.Component;

@Component
public class AdvancedDirectiveDocumentPopulator implements Populator<AdvanceDirectiveDocument, ExternalDocumentDto> {


    @Override
    public void populate(final AdvanceDirectiveDocument src, final ExternalDocumentDto target) {
        if (src == null) {
            return;
        }
        target.setId(src.getId());
        target.setMediaType(src.getMediaType());
        target.setText(null);//TODO when information is present in entity
        target.setUrl(src.getUrl());
    }
}
