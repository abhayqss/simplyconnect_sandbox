package com.scnsoft.eldermark.web.controller;

import com.scnsoft.eldermark.authentication.SecurityExpressions;
import com.scnsoft.eldermark.facades.DirectMessagesFacade;
import com.scnsoft.eldermark.services.direct.DirectAccountDetails;
import com.scnsoft.eldermark.shared.SesDirectoryAccountDto;
import com.scnsoft.eldermark.shared.form.AddressBookFilter;
import com.scnsoft.eldermark.web.resolvers.MessagingAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

//@Controller
//@RequestMapping(value = "/secure-messaging")
//@PreAuthorize(SecurityExpressions.IS_ELDERMARK_USER)
public class MessagingDirectoryController {

    @Autowired
    private DirectMessagesFacade directMessagesFacade;


    @RequestMapping(value = "/directory", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Page<SesDirectoryAccountDto> getDirectory(@ModelAttribute(value = "addressBookFilter") AddressBookFilter addressBookFilter,
                                                     @MessagingAccount DirectAccountDetails messagingAccount,
                                                     Pageable pageRequest) {
        return directMessagesFacade.directorySearch(addressBookFilter, pageRequest, messagingAccount);
    }
}
