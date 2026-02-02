package com.scnsoft.eldermark.services.connect;

import com.scnsoft.eldermark.authentication.ExchangeUserDetails;
import com.scnsoft.eldermark.shared.DocumentDto;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: knetkachou
 * Date: 4/7/14
 * Time: 1:28 PM
 * To change this template use File | Settings | File Templates.
 */
public interface NhinQueryForDocumentsService {
    public List<DocumentDto> queryForDocuments(String residentId, String assigningAuthorityId, ExchangeUserDetails employeeInfo);
}
