package com.scnsoft.eldermark.facades;

import com.scnsoft.eldermark.services.SaveDocumentCallback;
import com.scnsoft.eldermark.shared.DirectConfigurationDto;
import com.scnsoft.eldermark.shared.exceptions.DirectMessagingException;

public interface DirectConfigurationFacade {
    public void setPIN(String databaseAlternativeId, String pin);

    public void uploadKeystore(SaveDocumentCallback callback, String databaseAlternativeId);

    public DirectConfigurationDto getDirectConfiguration(String databaseAlternativeId);

    public boolean verify(String databaseAlternativeId) throws DirectMessagingException;

    public boolean isConfigured(String databaseAlternativeId);
}
