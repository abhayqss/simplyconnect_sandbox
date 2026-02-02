package com.scnsoft.eldermark.dto;

public class ClientAccessibleUserDto {

    private Boolean canViewTargetClient;
    private Boolean isTargetClientSynced;
    private Long targetClientId;
    private UserDto user;

    public Boolean getCanViewTargetClient() {
        return canViewTargetClient;
    }

    public void setCanViewTargetClient(Boolean canViewTargetClient) {
        this.canViewTargetClient = canViewTargetClient;
    }

    public Boolean getIsTargetClientSynced() {
        return isTargetClientSynced;
    }

    public void setIsTargetClientSynced(Boolean targetClientSynced) {
        isTargetClientSynced = targetClientSynced;
    }

    public Long getTargetClientId() {
        return targetClientId;
    }

    public void setTargetClientId(Long targetClientId) {
        this.targetClientId = targetClientId;
    }

    public UserDto getUser() {
        return user;
    }

    public void setUser(UserDto user) {
        this.user = user;
    }
}
