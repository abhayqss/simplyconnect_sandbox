package com.scnsoft.eldermark.service.document.cda;

import com.scnsoft.eldermark.cda.service.schema.CdaDocumentType;
import com.scnsoft.eldermark.entity.basic.BasicEntity;
import com.scnsoft.eldermark.service.document.cda.schema.ClinicalDocumentVO;
import com.scnsoft.eldermark.service.document.templates.cda.factory.sections.SectionFactory;
import org.eclipse.mdht.uml.cda.ClinicalDocument;
import org.eclipse.mdht.uml.cda.Section;

import java.util.Collection;

public interface CdaGenerator<T extends ClinicalDocument> {
    T generate(ClinicalDocumentVO document);

    CdaDocumentType getGeneratedType();

    /**
     *
     * @param <S>
     * @param <D>
     * @param document
     * @param sectionFactory
     * @param data
     */
    default <S extends Section, D extends BasicEntity> void addSection(T document,
                                                                       SectionFactory<S, D> sectionFactory, Collection<D> data) {
        if (sectionFactory.isTemplateIncluded()) {
            //todo if section is optional - don't create 'null' sections
            document.addSection(sectionFactory.buildTemplateInstance(data));
        }
    }
}
