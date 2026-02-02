package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.entity.PersonAddress;

import java.util.List;
import java.util.Map;

public interface CustomPersonAddressDao {
    Map<Long, List<PersonAddress>> findAllByClientIdIn(List<Long> clientIds);
}
