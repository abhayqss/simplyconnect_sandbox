package com.scnsoft.exchange.adt.entity;


public class AdtRequestDto {
    private String message;
    private String host;
    private String port;

    private String template;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }
}
