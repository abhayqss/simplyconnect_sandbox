package com.scnsoft.eldermark.service.passwords;

public interface ResetLoginCounterRunnable extends Runnable{

    void setUserId(Long userId);
}
