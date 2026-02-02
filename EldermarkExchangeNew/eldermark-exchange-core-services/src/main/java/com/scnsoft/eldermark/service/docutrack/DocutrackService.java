package com.scnsoft.eldermark.service.docutrack;

import com.scnsoft.eldermark.dto.docutrack.DocutrackSupportedFileListItemDto;
import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.community.Community;

import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Optional;

public interface DocutrackService {

    boolean isDocutrackEnabled(Employee employee);

    List<Community> getAssociatedPharmacies(Employee employee);

    List<DocutrackSupportedFileListItemDto> getSupportedFileTypes();

    void sendToDocutrackFromChat(Employee loggedUser, String mediaSid, String businessUnitCode, String documentText);

    String attachFromDocutrackToChat(Employee currentEmployee, String conversationSid, Long documentId);

    void updateServerCertificate(Community community, byte[] cert);

    void updateServerCertificate(Community community, X509Certificate cert);

    Optional<X509Certificate> getConfiguredCertificate(Community community);

    Optional<X509Certificate> loadServerCertIfSelfSigned(String serverDomain);

    List<String> nonUniqueBusinessUnitCodes(String serverDomain, Long excludeCommunityId, List<String> businessUnitCodes);
}
