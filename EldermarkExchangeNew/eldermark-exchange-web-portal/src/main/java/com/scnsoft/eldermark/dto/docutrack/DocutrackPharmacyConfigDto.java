package com.scnsoft.eldermark.dto.docutrack;

import com.scnsoft.eldermark.dto.CertificateInfoDto;
import com.scnsoft.eldermark.validation.SpELAssert;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.Size;
import java.util.List;

@SpELAssert.List(
        value = {
                @SpELAssert(
                        applyIf = "isIntegrationEnabled",
                        value = "#isNotEmpty(clientType)",
                        message = "clientType {javax.validation.constraints.NotEmpty.message}",
                        helpers = {StringUtils.class}
                ),
                @SpELAssert(
                        applyIf = "isIntegrationEnabled",
                        value = "#isNotEmpty(serverDomain)",
                        message = "serverDomain {javax.validation.constraints.NotEmpty.message}",
                        helpers = {StringUtils.class}
                )
        }
)
public class DocutrackPharmacyConfigDto {

    private boolean isIntegrationEnabled;

    private List<@Size(max = 256) String> businessUnitCodes;

    @Size(max = 50)
    private String clientType;

    @Size(max = 256)
    private String serverDomain;

    private boolean shouldRemoveCertificate;
    private MultipartFile publicKeyCertificate;
    private CertificateInfoDto configuredCertificate;
    private CertificateInfoDto serverCertificate;
    private String acceptedCertificateSha1Fingerprint;
    private String docutrackError;

    public boolean getIsIntegrationEnabled() {
        return isIntegrationEnabled;
    }

    public DocutrackPharmacyConfigDto setIsIntegrationEnabled(boolean integrationEnabled) {
        isIntegrationEnabled = integrationEnabled;
        return this;
    }

    public List<String> getBusinessUnitCodes() {
        return businessUnitCodes;
    }

    public void setBusinessUnitCodes(List<String> businessUnitCodes) {
        this.businessUnitCodes = businessUnitCodes;
    }

    public String getClientType() {
        return clientType;
    }

    public void setClientType(String clientType) {
        this.clientType = clientType;
    }

    public String getServerDomain() {
        return serverDomain;
    }

    public void setServerDomain(String serverDomain) {
        this.serverDomain = serverDomain;
    }

    public boolean isShouldRemoveCertificate() {
        return shouldRemoveCertificate;
    }

    public void setShouldRemoveCertificate(boolean shouldRemoveCertificate) {
        this.shouldRemoveCertificate = shouldRemoveCertificate;
    }

    public MultipartFile getPublicKeyCertificate() {
        return publicKeyCertificate;
    }

    public void setPublicKeyCertificate(MultipartFile publicKeyCertificate) {
        this.publicKeyCertificate = publicKeyCertificate;
    }

    public CertificateInfoDto getConfiguredCertificate() {
        return configuredCertificate;
    }

    public void setConfiguredCertificate(CertificateInfoDto configuredCertificate) {
        this.configuredCertificate = configuredCertificate;
    }

    public CertificateInfoDto getServerCertificate() {
        return serverCertificate;
    }

    public void setServerCertificate(CertificateInfoDto serverCertificate) {
        this.serverCertificate = serverCertificate;
    }

    public String getAcceptedCertificateSha1Fingerprint() {
        return acceptedCertificateSha1Fingerprint;
    }

    public void setAcceptedCertificateSha1Fingerprint(String acceptedCertificateSha1Fingerprint) {
        this.acceptedCertificateSha1Fingerprint = acceptedCertificateSha1Fingerprint;
    }

    public String getDocutrackError() {
        return docutrackError;
    }

    public void setDocutrackError(String docutrackError) {
        this.docutrackError = docutrackError;
    }
}
