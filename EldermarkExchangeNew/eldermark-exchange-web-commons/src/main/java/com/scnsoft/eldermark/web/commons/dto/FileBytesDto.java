package com.scnsoft.eldermark.web.commons.dto;

import org.springframework.http.MediaType;

public class FileBytesDto {
    private byte[] bytes;
    private MediaType mediaType;

    public FileBytesDto() {
    }

    public FileBytesDto(byte[] bytes, MediaType mediaType) {
        this.bytes = bytes;
        this.mediaType = mediaType;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }

    public MediaType getMediaType() {
        return mediaType;
    }

    public void setMediaType(MediaType mediaType) {
        this.mediaType = mediaType;
    }
}
