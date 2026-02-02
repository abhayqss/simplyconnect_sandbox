package com.scnsoft.eldermark.shared.exceptions;

import org.springframework.context.i18n.LocaleContextHolder;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

public abstract class LocalizedException extends RuntimeException {
    private List<Object> params = new ArrayList<Object>();

    public LocalizedException() {
        super();
    }

    public LocalizedException(Object... localizedMessageParams) {
        super();
        this.params = Arrays.asList(localizedMessageParams);
    }

    public LocalizedException(String message) {
        super(message);
    }

    public LocalizedException(String message, Throwable cause) {
        super(message, cause);
    }

    public LocalizedException(Throwable cause) {
        super(cause);
    }

    @Override
    public String getLocalizedMessage() {
        ResourceBundle resourceBundle = ResourceBundle.getBundle("i18n/EldermarkExchangeCoreErrors", LocaleContextHolder.getLocale());
        if (resourceBundle == null)
            return getMessage();
        String localizedMessage = resourceBundle.getString(getCode());
        if (params.isEmpty()) {
            return localizedMessage;
        } else {
            return MessageFormat.format(localizedMessage, params.toArray(new Object[params.size()]));
        }
    }

    public abstract String getCode();
}
