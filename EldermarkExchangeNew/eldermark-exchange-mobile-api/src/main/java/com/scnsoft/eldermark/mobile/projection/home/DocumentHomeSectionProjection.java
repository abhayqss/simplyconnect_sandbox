package com.scnsoft.eldermark.mobile.projection.home;

import com.scnsoft.eldermark.beans.projection.ClientIdAware;
import com.scnsoft.eldermark.beans.projection.IdAware;

public interface DocumentHomeSectionProjection extends IdAware, ClientIdAware {

    String getDocumentTitle();

    String getMimeType();
}
