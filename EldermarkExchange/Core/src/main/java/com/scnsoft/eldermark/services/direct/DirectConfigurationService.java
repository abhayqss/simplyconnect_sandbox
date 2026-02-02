package com.scnsoft.eldermark.services.direct;

import com.scnsoft.eldermark.entity.DirectConfiguration;
import com.scnsoft.eldermark.services.SaveDocumentCallback;

public interface DirectConfigurationService {
    public void setConfigured(String companyCode, boolean isConfigured);
    boolean isConfigured(String companyCode);
    public void setPin(String companyCode, String pin);
    public void uploadKeystore(SaveDocumentCallback callback, String companyCode);
    public String getKeystoreLocation(String companyCode);
    public String getKeystoreRelativeLocation(String companyCode);
    public String getKeystoresBaseLocation();
    public DirectConfiguration find(String companyCode);
}