package com.scnsoft.eldermark.consana.sync.server.model.entity;

public interface Telecom {
    Long getId();

    /**
     * @return {@code useCode} - Indicates the type of telecom
     */
    String getUseCode();

    String getValue();

    void setId(Long id);

    /**
     * @param useCode Indicates the type of telecom
     */
    void setUseCode(String useCode);

    void setValue(String value);
}
