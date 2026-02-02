package com.scnsoft.eldermark.service.sftp;

import java.io.InputStream;
import java.util.List;

public interface ApolloSftpGateway {

    boolean put(InputStream content, String dir, String filename);

    List<String> listFiles(String dir, String extension);

    byte[] get(String dir, String fileName);

    boolean remove(String dir, String fileName);

    boolean isSftpEnabled();
}
