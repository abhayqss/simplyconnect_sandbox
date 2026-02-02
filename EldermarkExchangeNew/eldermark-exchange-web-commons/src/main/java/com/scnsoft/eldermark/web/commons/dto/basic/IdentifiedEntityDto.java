package com.scnsoft.eldermark.web.commons.dto.basic;

import com.scnsoft.eldermark.beans.projection.IdAware;

public interface IdentifiedEntityDto extends EntityDto, IdAware {
    Long getId();
}
