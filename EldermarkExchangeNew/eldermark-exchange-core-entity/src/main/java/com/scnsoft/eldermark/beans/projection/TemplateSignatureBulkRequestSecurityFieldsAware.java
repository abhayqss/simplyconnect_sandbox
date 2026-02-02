package com.scnsoft.eldermark.beans.projection;

import java.util.List;

public interface TemplateSignatureBulkRequestSecurityFieldsAware {

    List<Long> getClientIds();

    List<Long> getTemplateIds();
}
