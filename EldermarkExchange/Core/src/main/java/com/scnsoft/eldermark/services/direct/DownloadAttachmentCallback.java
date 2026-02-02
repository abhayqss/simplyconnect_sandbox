package com.scnsoft.eldermark.services.direct;

import java.io.IOException;

public interface DownloadAttachmentCallback {
    public void download(DirectAttachment directAttachment) throws IOException;
}
