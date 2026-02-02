package com.scnsoft.eldermark.services.cda.templates;

public interface TemplateFactory<S, D> {

    S buildTemplateInstance(D data);
    boolean isTemplateIncluded();

}
