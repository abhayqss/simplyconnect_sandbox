package com.scnsoft.eldermark.service.storage;

import com.scnsoft.eldermark.exception.BusinessException;
import com.scnsoft.eldermark.exception.InternalServerException;
import com.scnsoft.eldermark.exception.InternalServerExceptionType;
import com.scnsoft.eldermark.shared.carecoordination.utils.Pair;
import com.scnsoft.eldermark.util.DocumentUtils;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import static com.scnsoft.eldermark.exception.BusinessExceptionType.FILE_IS_EMPTY_OR_CORRUPTED;
import static com.scnsoft.eldermark.exception.InternalServerExceptionType.FILE_NOT_DELETED;

public abstract class BaseFileStorage implements FileStorage {

    private final static Logger logger = LoggerFactory.getLogger(BaseFileStorage.class);

    private final Path storageLocation;

    public BaseFileStorage(Path storageLocation) {
        this.storageLocation = storageLocation;
        try {
            Files.createDirectories(this.storageLocation);
        } catch (IOException e) {
            throw new RuntimeException("Error on creating folder " + storageLocation, e);
        }
    }

    @SuppressFBWarnings(
        value = "PATH_TRAVERSAL_IN",
        justification = "storageLocation is configured on Spring context initialization"
    )
    protected BaseFileStorage(String storageLocation) {
        this(Paths.get(storageLocation));
    }

    @Override
    public String save(byte[] data, String fileName) {
        try {
            String finalFileName = generateNewFileName(fileName);

            var absolutePath = getPathByFileName(finalFileName);

            var processedData = processInputData(data, finalFileName);

            var encrypted = encrypt(processedData);

            Files.copy(new ByteArrayInputStream(encrypted), absolutePath, StandardCopyOption.REPLACE_EXISTING);

            return finalFileName;
        } catch (IOException e) {
            throw new InternalServerException(InternalServerExceptionType.FILE_IO_ERROR, e);
        }
    }

    @Override
    public String save(InputStream inputStream, String fileName) {
        try {
            var data = inputStream.readAllBytes();
            return this.save(data, fileName);
        } catch (IOException e) {
            throw new InternalServerException(InternalServerExceptionType.FILE_IO_ERROR, e);
        }
    }

    @Override
    public String save(MultipartFile file) {
        validateFile(file);
        try (var inputStream = file.getInputStream()) {
            return save(inputStream, file.getOriginalFilename());
        } catch (IOException e) {
            throw new InternalServerException(InternalServerExceptionType.FILE_IO_ERROR, e);
        }
    }

    @Override
    public String getAbsolutePath(String fileName) {
        return getPathByFileName(fileName).toAbsolutePath().toString();
    }

    @Override
    public InputStream loadAsInputStream(String fileName) {
        var path = getPathByFileName(fileName);
        try {
            return new ByteArrayInputStream(decrypt(Files.readAllBytes(path)));
        } catch (IOException e) {
            throw new InternalServerException(InternalServerExceptionType.FILE_IO_ERROR, e);
        }
    }

    @Override
    public byte[] loadAsBytes(String fileName) {
        var path = getPathByFileName(fileName);
        try {
            return decrypt(Files.readAllBytes(path));
        } catch (IOException e) {
            throw new InternalServerException(InternalServerExceptionType.FILE_IO_ERROR, e);
        }
    }

    @Override
    public Pair<byte[], MediaType> loadAsBytesWithMediaType(String fileName) {
        return new Pair<>(
            loadAsBytes(fileName),
            probeContentType(fileName)
        );
    }

    @Override
    public boolean delete(String fileName) {
        try {
            if (exists(fileName)) {
                Files.delete(getPathByFileName(fileName));
                return true;
            } else {
                return false;
            }
        } catch (IOException e) {
            logger.error("File not deleted", e);
            throw new InternalServerException(FILE_NOT_DELETED, e);
        }
    }

    @Override
    public MediaType probeContentType(String fileName) {
        var path = getPathByFileName(fileName);
        try {
            return MediaType.valueOf(Files.probeContentType(path));
        } catch (IOException e) {
            throw new InternalServerException(InternalServerExceptionType.FILE_IO_ERROR, e);
        }
    }

    @Override
    public boolean exists(String fileName) {
        return StringUtils.isNotEmpty(fileName) && Files.exists(getPathByFileName(fileName));
    }

    @Override
    public String hash(String fileName) {
        return DocumentUtils.hash(loadAsBytes(fileName));
    }

    @Override
    public long size(String fileName) {
        try {
            return Files.size(getPathByFileName(fileName));
        } catch (IOException e) {
            throw new InternalServerException(InternalServerExceptionType.FILE_IO_ERROR, e);
        }
    }

    protected String generateNewFileName(String originalFileName) {
        return originalFileName;
    }

    protected void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(FILE_IS_EMPTY_OR_CORRUPTED);
        }
    }

    protected byte[] processInputData(byte[] inputData, String fileName) {
        return inputData;
    }

    @SuppressFBWarnings(
        value = "PATH_TRAVERSAL_IN",
        justification = "if fileName is path then exception is thrown"
    )
    private Path getPathByFileName(String fileName) {

        if (StringUtils.isEmpty(fileName)) {
            throw new IllegalArgumentException("File name must not be empty");
        }

        var path = Paths.get(fileName);
        if (path.getNameCount() != 1) {
            throw new IllegalArgumentException("Invalid file name");
        }

        return storageLocation.resolve(path);
    }

    public static String addUuidPostfixToFileName(String originalFileName) {
        String extension = FilenameUtils.getExtension(originalFileName);
        String baseName = FilenameUtils.getBaseName(originalFileName);

        return baseName + "_" + UUID.randomUUID().toString().replace("-", "") + "." + extension;
    }

    public static String addTimestampPostfixToFileName(String originalFileName) {
        String extension = FilenameUtils.getExtension(originalFileName);
        String baseName = FilenameUtils.getBaseName(originalFileName);

        return baseName + "_" + System.currentTimeMillis() + "." + extension;
    }

    protected abstract byte[] encrypt(byte[] decrypted);
    protected abstract byte[] decrypt(byte[] encrypted);

}
