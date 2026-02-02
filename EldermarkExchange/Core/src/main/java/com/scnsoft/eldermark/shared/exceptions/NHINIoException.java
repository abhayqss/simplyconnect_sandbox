package com.scnsoft.eldermark.shared.exceptions;

import java.io.IOException;

/**
 * Created by averazub on 8/18/2016.
 */
public class NHINIoException extends IOException {
    public NHINIoException() {
    }

    public NHINIoException(String message) {
        super(message);
    }

    public NHINIoException(String message, Throwable cause) {
        super(message, cause);
    }

    public NHINIoException(Throwable cause) {
        super(cause);
    }
}
