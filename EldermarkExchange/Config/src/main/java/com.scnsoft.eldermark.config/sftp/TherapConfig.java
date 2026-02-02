package com.scnsoft.eldermark.config.sftp;

import com.jcraft.jsch.JSchException;
import com.pastdev.jsch.SessionManager;
import com.scnsoft.eldermark.services.inbound.therap.TherapInboundFilesServiceRunCondition;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.Resource;

import java.io.IOException;

@Configuration
@PropertySource("classpath:sftp/therap.properties")
@Conditional(TherapInboundFilesServiceRunCondition.class)
public class TherapConfig {
    @Bean
    public SessionManager therapJschSessionManager(
            @Value("${therap.sftp.hostname}") String hostname,
            @Value("${therap.sftp.port}") Integer port,
            @Value("${therap.sftp.username}") String username,
            @Value("${therap.sftp.password}") String password,
            @Value("classpath:sftp/known_hosts") Resource knownHosts) throws IOException, JSchException {
        return SftpSessionManagerFactory.newSessionManager(
                hostname,
                port,
                username,
                password,
                knownHosts.getInputStream());
    }
}
