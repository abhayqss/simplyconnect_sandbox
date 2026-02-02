package com.scnsoft.eldermark.service.sftp;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import com.pastdev.jsch.SessionManager;
import com.scnsoft.eldermark.exception.SftpGatewayException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class SftpUtils {

    private static final Logger logger = LoggerFactory.getLogger(SftpUtils.class);

    private SftpUtils() {
    }

    public static <T> T getFromSftp(SessionManager sessionManager, SftpGetter<T> getter) {
        ChannelSftp channelSftp = null;
        try {
            channelSftp = openAndConnectChannel(sessionManager);
            return getter.get(channelSftp);
        } catch (Exception e) {
            throw new SftpGatewayException(e);
        } finally {
            if (channelSftp != null) {
                channelSftp.disconnect();
            }
        }
    }

    public static <T> void doInSftp(SessionManager sessionManager, SftpAction<T> action) {
        ChannelSftp channelSftp = null;
        try {
            channelSftp = openAndConnectChannel(sessionManager);
            action.doInSftp(channelSftp);
        } catch (Exception e) {
            throw new SftpGatewayException(e);
        } finally {
            if (channelSftp != null) {
                channelSftp.disconnect();
            }
        }
    }

    public static ChannelSftp openAndConnectChannel(SessionManager sessionManager) throws JSchException {
        ChannelSftp channel = (ChannelSftp) sessionManager.getSession().openChannel("sftp");
        channel.connect();
        return channel;
    }

    public static void createAndCdToFolders(ChannelSftp channel, String dirs) throws SftpException {
        var split = dirs.split("/");

        for (String dir : split) {
            createAndCdToFolder(channel, dir);
        }
    }

    public static void createAndCdToFolder(ChannelSftp channel, String dir) throws SftpException {
        if (StringUtils.isNotEmpty(dir)) {
            if (!folderExists(channel, dir)) {
                logger.info("SFTP folder {} doesn't exist, creating new", dir);
                channel.mkdir(dir);
            }
            channel.cd(dir);
        }
    }

    public static boolean folderExists(ChannelSftp channel, String dir) throws SftpException {
        for (Object o : channel.ls(".")) {
            ChannelSftp.LsEntry entry = (ChannelSftp.LsEntry) o;
            if (entry.getAttrs().isDir() && dir.equals(entry.getFilename())) {
                return true;
            }
        }
        return false;
    }

    public interface SftpGetter<T> {

        T get(ChannelSftp channelSftp) throws Exception;

    }

    public interface SftpAction<T> {

        void doInSftp(ChannelSftp channelSftp) throws Exception;

    }
}
