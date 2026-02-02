package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.entity.State;

import java.util.List;

/**
 * Created by pzhurba on 24-Sep-15.
 */
public interface StateDao extends BaseDao<State> {
    State findByAbbr(String abbr);

    State findByAbbrOrFullName(String fullName);

    List<State> searchByFullNameLike(String searchText);
}
