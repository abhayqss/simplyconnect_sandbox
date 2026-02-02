package com.scnsoft.eldermark.service.passwords;

public interface UnlockAccountRunnable extends Runnable {

    void setUserId(Long userId);
}
