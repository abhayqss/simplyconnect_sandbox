package com.scnsoft.eldermark.ws.server.security;

import org.apache.ws.security.WSPasswordCallback;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;
import java.io.IOException;

public class ServerPasswordCallback implements CallbackHandler {

    private String alias;
    private String privatePassword;

    public ServerPasswordCallback(String alias, String privatePassword) {
        this.alias = alias;
        this.privatePassword = privatePassword;
    }

    public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
        for (Callback callback : callbacks) {
            WSPasswordCallback pc = (WSPasswordCallback) callback;

            if (pc.getUsage() == WSPasswordCallback.SIGNATURE ||
                pc.getUsage() == WSPasswordCallback.DECRYPT ) {

                if (alias.equals(pc.getIdentifier()) ) {
                    pc.setPassword(privatePassword);
                }
            }
        }
    }
}
