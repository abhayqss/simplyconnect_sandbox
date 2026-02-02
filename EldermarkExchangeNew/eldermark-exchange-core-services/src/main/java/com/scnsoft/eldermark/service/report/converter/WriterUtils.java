package com.scnsoft.eldermark.service.report.converter;

import com.scnsoft.eldermark.entity.document.CommunityDocumentAndFolder;
import com.scnsoft.eldermark.entity.document.DocumentReport;
import com.scnsoft.eldermark.exception.BusinessException;
import com.scnsoft.eldermark.exception.BusinessExceptionType;
import com.scnsoft.eldermark.exception.InternalServerException;
import com.scnsoft.eldermark.exception.InternalServerExceptionType;
import com.scnsoft.eldermark.service.document.DocumentTreeItem;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StreamUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public final class WriterUtils {

    private static final Logger logger = LoggerFactory.getLogger(WriterUtils.class);

    private static final String ZIP_SEPARATOR = "/";

    public static final String APPLICATION_ZIP_MIME_TYPE = "application/zip";
    public static final String XSLX_MIME_TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

    @SuppressFBWarnings(value = "PATH_TRAVERSAL_OUT", justification = "It is used only for local testing")
    public static void writeToFile(Workbook workbook, String name) {
        String file = name + LocalDate.now() + ".xlsx";
        try {
            FileOutputStream out = new FileOutputStream(file);
            workbook.write(out);
            out.close();
            workbook.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void copyDocumentContentToResponse(String title, byte[] bytes,
                                                     String mimeType, boolean isViewMode,
                                                     HttpServletResponse response) {
        copyDocumentContentToResponse(title, () -> new ByteArrayInputStream(bytes), mimeType, isViewMode, response);
    }


    public static void copyDocumentContentToResponse(String title, InputStreamProvider inputStreamProvider,
                                                     String mimeType, boolean isViewMode,
                                                     HttpServletResponse response) {
        String openType = isViewMode ? "inline" : "attachment";
        response.setContentType(mimeType);
        response.setHeader("Access-Control-Expose-Headers", "Content-Disposition");
        response.setHeader("Content-Disposition", openType + ";filename=\"" + title + "\"");
        try (var inputStream = inputStreamProvider.getInputStream()) {
            FileCopyUtils.copy(inputStream, response.getOutputStream());
        } catch (IOException e) {
            throw new BusinessException(BusinessExceptionType.FILE_IO_ERROR, e);
        }
    }

    public static void copyDocumentContentToResponse(Workbook workbook, String fileName, HttpServletResponse response) {
        response.setContentType(XSLX_MIME_TYPE);
        response.setHeader("Access-Control-Expose-Headers", "Content-Disposition");
        response.setHeader("Content-Disposition", "attachment;filename=\"" + fileName + ".xlsx" + "\"");
        copyToResponseFile(convertWorkbookToInputStream(workbook), response);
    }

    public static void copyDocumentContentToResponse(DocumentReport document, boolean isViewMode, HttpServletResponse response) {
        response.setContentType(isViewMode ? "text/plain" : document.getMimeType());
        response.setHeader("Access-Control-Expose-Headers", "Content-Disposition");
        response.setHeader("Content-Disposition", isViewMode ? "inline" : "attachment" + ";filename=\"" + document.getDocumentTitle() + "\"");
        copyToResponseFile(document.getInputStream(), response);
    }

    public static void copyDocumentContentToResponse(HttpServletResponse response, DocumentReport document) {
        copyDocumentContentToResponse(document, false, response);
    }

    public static void copyFileContentToResponse(FileProvider file, HttpServletResponse response) {
        copyDocumentContentToResponse(file.getFileName(), file, file.getMimeType(), false, response);
    }

    public static void copyDocumentContentAndContentTypeToResponse(byte[] bytes, String mimeType, HttpServletResponse response) {
        response.setContentType(mimeType);
        try {
            FileCopyUtils.copy(new ByteArrayInputStream(bytes), response.getOutputStream());
        } catch (IOException e) {
            throw new BusinessException("Error while copying report to response.");
        }
    }

    public static void copyBytesAsZipToResponse(String filename, byte[] bytes, HttpServletResponse response) {
        response.setHeader(HttpHeaders.CONTENT_TYPE, APPLICATION_ZIP_MIME_TYPE);
        response.setHeader(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.CONTENT_DISPOSITION);
        response.setHeader(
            HttpHeaders.CONTENT_DISPOSITION,
            ContentDisposition.builder("attachment")
                .filename(filename)
                .build()
                .toString()
        );
        try (var os = response.getOutputStream()) {
            FileCopyUtils.copy(bytes, response.getOutputStream());
        } catch (IOException e) {
            throw new BusinessException("Error while copying data to response.");
        }
    }

    private static void copyToResponseFile(InputStream inputStream, HttpServletResponse response) {
        try {
            FileCopyUtils.copy(inputStream, response.getOutputStream());
        } catch (IOException e) {
            throw new BusinessException("Error while copying report to response.");
        }
    }

    private static InputStream convertWorkbookToInputStream(Workbook workbook) {
        InputStream is = null;
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            workbook.write(bos);
            byte[] barray = bos.toByteArray();
            is = new ByteArrayInputStream(barray);
        } catch (IOException e) {
            logger.error("Error during convertion of workbook to input stream", e);
        }
        return is;
    }

    public interface InputStreamProvider {
        InputStream getInputStream() throws IOException;
    }

    public interface FileProvider extends InputStreamProvider {
        String getFileName();

        String getMimeType();

        InputStream getInputStream() throws IOException;

        static FileProvider of(String fileName, String mimeType, InputStreamProvider inputStreamProvider) {
            return new FileProvider() {
                @Override
                public String getFileName() {
                    return fileName;
                }

                @Override
                public String getMimeType() {
                    return mimeType;
                }

                @Override
                public InputStream getInputStream() throws IOException {
                    return inputStreamProvider.getInputStream();
                }
            };
        }
    }

    public static byte[] generateZip(List<FileProvider> zipEntries) {
        return generateZip(
                DocumentTreeItem.folder(
                        null,
                        zipEntries.stream()
                                .map(DocumentTreeItem::file)
                                .collect(Collectors.toList())
                )
        );
    }

    public static byte[] generateZip(DocumentTreeItem<FileProvider> tree) {
        try {
            var baos = new ByteArrayOutputStream();
            var zos = new ZipOutputStream(baos);
            writeFolderZipEntry(zos, "", tree.getChildren());
            zos.close();
            return baos.toByteArray();
        } catch (IOException e) {
            logger.warn("Failed to create zip archive", e);
            throw new InternalServerException(InternalServerExceptionType.FILE_IO_ERROR, e);
        }
    }

    public static byte[] generateZip(
            DocumentTreeItem<CommunityDocumentAndFolder> tree,
            Function<CommunityDocumentAndFolder, InputStream> readDocumentFunction
    ) {
        try (var outputStream = new ByteArrayOutputStream()) {
            try (var zos = new ZipOutputStream(outputStream)) {
                var root = tree.getValue();
                var rootName = root != null ? root.getTitle() : null;
                writeDocumentTreeItemToZip(zos, rootName, tree, readDocumentFunction);
            }
            return outputStream.toByteArray();
        } catch (IOException e) {
            logger.warn("Failed to create zip archive", e);
            throw new InternalServerException(InternalServerExceptionType.FILE_IO_ERROR, e);
        }
    }

    private static void writeDocumentTreeItemToZip(
            ZipOutputStream zos,
            String entryName,
            DocumentTreeItem<CommunityDocumentAndFolder> item,
            Function<CommunityDocumentAndFolder, InputStream> readFileFunction
    ) throws IOException {

        if (item.getValue() == null || item.isFolder()) {
            if (item.getChildren().isEmpty()) {
                zos.putNextEntry(new ZipEntry(entryName + ZIP_SEPARATOR));
                zos.closeEntry();
            } else {
                var titleCountMap = new HashMap<String, Integer>();
                for (var child : item.getChildren()) {
                    var childName = child.getValue().getTitle();
                    if (!child.isFolder()) {
                        var titleCount = titleCountMap.compute(childName, (key, prevCount) -> prevCount == null ? 1 : prevCount + 1);
                        if (titleCount != 1) {
                            childName = FilenameUtils.getBaseName(childName) + "(" + titleCount + ")." + FilenameUtils.getExtension(childName);
                        }
                    }
                    writeDocumentTreeItemToZip(zos, (entryName != null ? entryName + ZIP_SEPARATOR : "") + childName, child, readFileFunction);
                }
            }
        } else {
            zos.putNextEntry(new ZipEntry(entryName));
            StreamUtils.copy(readFileFunction.apply(item.getValue()), zos);
            zos.closeEntry();
        }
    }

    private static void writeZipEntry(
            ZipOutputStream zos,
            String entryName,
            DocumentTreeItem<FileProvider> entry
    ) throws IOException {
        if (entry.isFolder()) {
            writeFolderZipEntry(zos, entryName + ZIP_SEPARATOR, entry.getChildren());
        } else {
            writeFileZipEntry(zos, entryName, entry.getValue());
        }
    }

    private static void writeFileZipEntry(
            ZipOutputStream zos,
            String entryName,
            InputStreamProvider inputStreamProvider
    ) throws IOException {
        zos.putNextEntry(new ZipEntry(entryName));
        try (var inputStream = inputStreamProvider.getInputStream()) {
            StreamUtils.copy(inputStream, zos);
        }
        zos.closeEntry();
    }

    private static void writeFolderZipEntry(
            ZipOutputStream zos,
            String entryName,
            List<DocumentTreeItem<FileProvider>> children
    ) throws IOException {
        if (children.isEmpty()) {
            zos.putNextEntry(new ZipEntry(entryName));
            zos.closeEntry();
        } else {
            var titleCountMap = new HashMap<String, Integer>();
            for (var child : children) {
                var childName = child.getValue().getFileName();
                var titleCount = titleCountMap.compute(childName, (key, prevCount) -> prevCount == null ? 1 : prevCount + 1);
                if (titleCount != 1) {
                    childName = FilenameUtils.getBaseName(childName) + "(" + titleCount + ")." + FilenameUtils.getExtension(childName);
                }

                writeZipEntry(zos, entryName + childName, child);
            }
        }
    }
}
