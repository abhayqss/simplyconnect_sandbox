package com.scnsoft.exchange.adt.entity;

/**
 * Created by averazub on 10/13/2016.
 */
public class FileUploadDto {
    private String base64Content;
    private String fileName;
    private String hash;
    private String size;

    public String getBase64Content() {
        return base64Content;
    }

    public void setBase64Content(String base64Content) {
        this.base64Content = base64Content;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }
}
