package com.scnsoft.eldermark.service.sftp;

import com.jcraft.jsch.ChannelSftp;
import com.pastdev.jsch.SessionManager;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ApolloSftpGatewayImpl implements ApolloSftpGateway {

    private static final Logger logger = LoggerFactory.getLogger(ApolloSftpGatewayImpl.class);

    @Autowired
    @Qualifier("apolloSftpSessionManager")
    private SessionManager sessionManager;

    @Value("${apollo.sftp.enabled}")
    private boolean sftpEnabled;

    @Value("${apollo.sftp.receive.enabled}")
    private boolean sftpReceiveEnabled;

    @Override
    public boolean put(InputStream content, String dir, String filename) {
        if (!sftpEnabled) {
            logger.info("Attempt to push file to Apollo SFTP - SFTP disabled");
            return false;
        }
        return SftpUtils.getFromSftp(sessionManager, channelSftp -> {
            channelSftp.cd(dir);
            channelSftp.put(content, filename);
            logger.info("File [{}] was pushed to [{}] directory in Apollo sftp", filename, dir);
            return true;
        });
    }

    @Override
    public List<String> listFiles(String dir, String extension) {
        if (!sftpEnabled) {
            logger.info("Attempt to list Apollo SFTP files - SFTP is disabled");
            return Collections.emptyList();
        }
        if (!sftpReceiveEnabled) {
            logger.info("Attempt to list Apollo SFTP files - SFTP receive is disabled");
            return Collections.emptyList();
        }
        return SftpUtils.getFromSftp(sessionManager, channelSftp -> {
            var vector = channelSftp.ls(dir);
            logger.debug("Listing files in {} at Apollo SFTP", dir);
            return ((Stream<?>) vector.stream())
                    .map(ChannelSftp.LsEntry.class::cast)
                    .filter(entry -> !entry.getAttrs().isDir())
                    .filter(entry -> StringUtils.endsWithIgnoreCase(entry.getFilename(), extension))
                    .map(ChannelSftp.LsEntry::getFilename)
                    .collect(Collectors.toList());
        });
    }

    @Override
    public byte[] get(String dir, String fileName) {
        if (!sftpEnabled) {
            logger.info("Attempt to load file from Apollo SFTP - SFTP is disabled");
            return null;
        }
        if (!sftpReceiveEnabled) {
            logger.info("Attempt to load file from Apollo SFTP - SFTP receive is disabled");
            return null;
        }
        return SftpUtils.getFromSftp(sessionManager, channelSftp -> {
            channelSftp.cd(dir);
            logger.info("Loading [{}] at [{}] from Apollo SFTP", fileName, dir);
            var bytes = channelSftp.get(fileName).readAllBytes();
            logger.info("Loadede [{}] at [{}] from Apollo SFTP", fileName, dir);
            return bytes;
        });
    }

    @Override
    public boolean remove(String dir, String fileName) {
        if (!sftpEnabled) {
            logger.info("Attempt to remove Apollo SFTP files - SFTP disabled");
            return false;
        }
        return SftpUtils.getFromSftp(sessionManager, channelSftp -> {
            channelSftp.cd(dir);
            logger.info("Removing file {} at {} from Apollo SFTP", fileName, dir);
            channelSftp.rm(fileName);
            logger.info("Removed file {} at {} from Apollo SFTP", fileName, dir);
            return true;
        });
    }

    @Override
    public boolean isSftpEnabled() {
        return sftpEnabled;
    }

}
