package com.scnsoft.eldermark.service.storage;

import com.scnsoft.eldermark.shared.carecoordination.utils.Pair;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

public interface FileStorage {

    String save(InputStream inputStream, String fileName);
    String save(byte[] bytes, String fileName);
    String save(MultipartFile file);
    InputStream loadAsInputStream(String fileName);
    byte[] loadAsBytes(String fileName);
    Pair<byte[], MediaType> loadAsBytesWithMediaType(String fileName);
    boolean delete(String fileName);

    MediaType probeContentType(String fileName);
    boolean exists(String fileName);
    String hash(String fileName);
    long size(String fileName);

    String getAbsolutePath(String fileName);
}
