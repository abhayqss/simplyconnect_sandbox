package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.dao.MissedChatsAndCallsDao;
import com.scnsoft.eldermark.entity.MissedChatsAndCalls;
import com.scnsoft.eldermark.entity.MissedChatsAndCalls_;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MissedChatsAndCallsServiceImpl implements MissedChatsAndCallsService {

    public static final Sort MISSED_CHATS_CALLS_SORT_ORDER = Sort.by(Sort.Order.desc(MissedChatsAndCalls_.DATE_TIME));

    @Autowired
    private MissedChatsAndCallsDao missedChatsAndCallsDao;

    @Override
    @Transactional(readOnly = true)
    public List<MissedChatsAndCalls> loadMissedChatsAndCalls(Long employeeId, int limit) {
        return missedChatsAndCallsDao.findAll(
                (root, criteriaQuery, criteriaBuilder) ->
                        criteriaBuilder.equal(root.get(MissedChatsAndCalls_.employeeId), employeeId),
                MissedChatsAndCalls.class,
                MISSED_CHATS_CALLS_SORT_ORDER,
                limit);
    }

}
