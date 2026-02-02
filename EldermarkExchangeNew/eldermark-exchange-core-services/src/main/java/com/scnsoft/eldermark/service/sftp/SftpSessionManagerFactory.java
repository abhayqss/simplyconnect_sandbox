package com.scnsoft.eldermark.service.sftp;

import com.jcraft.jsch.JSchException;
import com.pastdev.jsch.DefaultSessionFactory;
import com.pastdev.jsch.SessionManager;

import java.io.InputStream;

public final class SftpSessionManagerFactory {

    private SftpSessionManagerFactory() {
    }

    public static SessionManager newSessionManager(String hostname, Integer port, String username,
                                                   String password, InputStream knownHosts) throws JSchException {
        final DefaultSessionFactory sessionFactory = new DefaultSessionFactory(username, hostname, port);
        sessionFactory.setPassword(password);

        sessionFactory.setConfig("StrictHostKeyChecking", "yes");
        sessionFactory.setKnownHosts(knownHosts);

        return new SessionManager(sessionFactory);
    }

}
