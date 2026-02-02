package com.scnsoft.eldermark.web.controller;

import com.scnsoft.eldermark.authentication.ExchangeUserDetails;
import com.scnsoft.eldermark.authentication.SecurityExpressions;
import com.scnsoft.eldermark.facades.DirectConfigurationFacade;
import com.scnsoft.eldermark.facades.DirectMessagesFacade;
import com.scnsoft.eldermark.services.SaveDocumentCallback;
import com.scnsoft.eldermark.services.SaveDocumentCallbackImpl;
import com.scnsoft.eldermark.services.direct.DirectAccountDetails;
import com.scnsoft.eldermark.services.direct.MailAccountDetailsFactory;
import com.scnsoft.eldermark.shared.DirectConfigurationDto;
import com.scnsoft.eldermark.shared.exceptions.DirectNotConfiguredException;
import com.scnsoft.eldermark.shared.exceptions.FileIOException;
import com.scnsoft.eldermark.shared.form.MessagingSetupForm;
import com.scnsoft.eldermark.web.resolvers.MessagingAccount;
import com.scnsoft.eldermark.web.session.SecureMessagingConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.validation.Valid;
import java.io.File;
import java.io.FileOutputStream;

//@Controller
//@RequestMapping(value = "/secure-messaging/setup")
//@PreAuthorize(SecurityExpressions.IS_DIRECT_MANAGER)
public class MessagingSetupController {

    @Autowired
    private DirectConfigurationFacade directConfigFacade;

    @Autowired
    private DirectMessagesFacade directMessagesFacade;

    @Autowired
    private SecureMessagingConfig messagingConfig;

    @Autowired
    private MailAccountDetailsFactory mailAccountDetailsFactory;

    @RequestMapping(method = RequestMethod.GET)
    public String initView(Model model,
                           @MessagingAccount DirectAccountDetails messagingAccount) {
        DirectConfigurationDto configDto = directConfigFacade.getDirectConfiguration(messagingAccount.getCompany());

        MessagingSetupForm messagingSetup = new MessagingSetupForm();
        messagingSetup.setPin(configDto.getPin());
        messagingSetup.setCertificateName(configDto.getKeystoreName());
        messagingSetup.setConfigured(configDto.getIsConfigured());

        model.addAttribute("msgSetup", messagingSetup);

        return "messaging.setup";
    }

    @RequestMapping(value = "/set-keystore", method = RequestMethod.POST)
    @ResponseBody
    public void setKeyStore(@Valid @ModelAttribute(value = "msgSetup") MessagingSetupForm msgSetup,
                            @MessagingAccount DirectAccountDetails messagingAccount) {

        final CommonsMultipartFile keystore = msgSetup.getKeystore();

        directConfigFacade.uploadKeystore(new SaveDocumentCallbackImpl() {
            public void saveToFile(File file) {
                try {
                    FileCopyUtils.copy(keystore.getInputStream(), new FileOutputStream(file));
                } catch (Exception e) {
                    throw new FileIOException("Failed to save file " + file.getName(), e);
                }
            }
        }, messagingAccount.getCompany());
    }

    @RequestMapping(value = "/set-pin", method = RequestMethod.POST)
    @ResponseBody
    public void setPin(@Valid @ModelAttribute(value = "msgSetup") MessagingSetupForm msgSetup,
                       @MessagingAccount DirectAccountDetails messagingAccount) {

        directConfigFacade.setPIN(messagingAccount.getCompany(), msgSetup.getPin());
    }

    @RequestMapping(value = "/verify", method = RequestMethod.GET)
    @ResponseBody
    public void verifyDirectConfiguration(@AuthenticationPrincipal ExchangeUserDetails userDetails,
                                          @MessagingAccount DirectAccountDetails messagingAccount) {
        messagingConfig.resetCache();

        if (!directConfigFacade.verify(messagingAccount.getCompany())) {
            throw new DirectNotConfiguredException();
        }
    }
}
