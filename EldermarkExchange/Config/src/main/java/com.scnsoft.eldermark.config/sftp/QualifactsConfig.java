package com.scnsoft.eldermark.config.sftp;

import com.jcraft.jsch.JSchException;
import com.pastdev.jsch.SessionManager;
import com.scnsoft.eldermark.services.integration.qualifacts.QualifactsIntegrationEnabledCondition;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.Resource;

import java.io.IOException;

@Configuration
@PropertySource("classpath:sftp/qualifacts.properties")
@Conditional(QualifactsIntegrationEnabledCondition.class)
public class QualifactsConfig {

    @Bean
    public SessionManager qualifactsSftpSessionManager(
            @Value("${qualifacts.sftp.hostname}") String hostname,
            @Value("${qualifacts.sftp.port}") Integer port,
            @Value("${qualifacts.sftp.username}") String username,
            @Value("${qualifacts.sftp.password}") String password,
            @Value("classpath:sftp/known_hosts") Resource knownHosts) throws IOException, JSchException {
        return SftpSessionManagerFactory.newSessionManager(
                hostname,
                port,
                username,
                password,
                knownHosts.getInputStream());
    }
}
