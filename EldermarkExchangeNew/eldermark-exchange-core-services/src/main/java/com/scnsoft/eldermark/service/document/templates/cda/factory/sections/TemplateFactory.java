package com.scnsoft.eldermark.service.document.templates.cda.factory.sections;

public interface TemplateFactory<S, D> {

    S buildTemplateInstance(D data);
    boolean isTemplateIncluded();

}
