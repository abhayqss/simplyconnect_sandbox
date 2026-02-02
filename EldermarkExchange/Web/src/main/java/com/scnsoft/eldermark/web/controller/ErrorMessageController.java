package com.scnsoft.eldermark.web.controller;

import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

/**
 * @author stsiushkevich
 */

//@RequestMapping("/error-messages")
//@Controller
public class ErrorMessageController {

    @ResponseBody
    @RequestMapping(method = RequestMethod.GET)
    public Map<String, String> list() {
        ResourceBundle bundle = ResourceBundle.getBundle("i18n/EldermarkExchangeErrors", LocaleContextHolder.getLocale());
        Set<String> keySet = bundle.keySet();
        Map<String, String> messages = new HashMap<String, String>();
        for (String key : keySet) {
            messages.put(key, bundle.getString(key));
        }
        return messages;
    }
}
