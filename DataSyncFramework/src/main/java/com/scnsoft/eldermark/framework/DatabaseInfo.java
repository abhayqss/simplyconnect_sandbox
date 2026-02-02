package com.scnsoft.eldermark.framework;

import java.util.Date;

public class DatabaseInfo implements IdAware {
    private long id;
    private String url;
    private Date lastSyncDate;
    private Long lastSyncedTime;
    private Long currentSyncTime;

    private String remoteHost;
    private Integer remotePort;
    private String remoteUsername;
    private String remotePassword;
    private Boolean remoteUseSsl;
    private Boolean isXmlSync;
    private Boolean isInitialSync;
    private String consanaXOwningId;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Date getLastSyncDate() {
        return lastSyncDate;
    }

    public void setLastSyncDate(Date lastSyncDate) {
        this.lastSyncDate = lastSyncDate;
    }

    public Long getLastSyncedTime() {
        return lastSyncedTime;
    }

    public void setLastSyncedTime(Long lastSyncedTime) {
        this.lastSyncedTime = lastSyncedTime;
    }

    public Long getCurrentSyncTime() {
        return currentSyncTime;
    }

    public void setCurrentSyncTime(Long currentSyncTime) {
        this.currentSyncTime = currentSyncTime;
    }

    public String getRemoteHost() {
        return remoteHost;
    }

    public void setRemoteHost(String remoteHost) {
        this.remoteHost = remoteHost;
    }

    public Integer getRemotePort() {
        return remotePort;
    }

    public void setRemotePort(Integer remotePort) {
        this.remotePort = remotePort;
    }

    public String getRemoteUsername() {
        return remoteUsername;
    }

    public void setRemoteUsername(String remoteUsername) {
        this.remoteUsername = remoteUsername;
    }

    public String getRemotePassword() {
        return remotePassword;
    }

    public void setRemotePassword(String remotePassword) {
        this.remotePassword = remotePassword;
    }

    public Boolean getRemoteUseSsl() {
        return remoteUseSsl;
    }

    public void setRemoteUseSsl(Boolean remoteUseSsl) {
        this.remoteUseSsl = remoteUseSsl;
    }


    public Boolean getIsXmlSync() {
        return isXmlSync;
    }

    public void setIsXmlSync(Boolean isXmlSync) {
        this.isXmlSync = isXmlSync;
    }

    public String getConsanaXOwningId() {
        return consanaXOwningId;
    }

    public void setConsanaXOwningId(String consanaXOwningId) {
        this.consanaXOwningId = consanaXOwningId;
    }

    /**
     * Return <b>true</b>, if this company going to sync data for the first time else return <b> false</b>.
	 * @return the isInitialSync: 
	 */
	public Boolean getIsInitialSync() {
		return isInitialSync;
	}

	/**
	 * @param isInitialSync the isInitialSync to set
	 */
	public void setIsInitialSync(Boolean isInitialSync) {
		this.isInitialSync = isInitialSync;
	}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DatabaseInfo database = (DatabaseInfo) o;

        return (id == database.id);
    }


    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }
}
