package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.entity.MissedChatsAndCalls;

import java.util.List;

public interface MissedChatsAndCallsService {

    List<MissedChatsAndCalls> loadMissedChatsAndCalls(Long employeeId, int limit);
}
