package com.scnsoft.eldermark.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.scnsoft.eldermark.entity.State;

@Repository
public interface StateDao extends JpaRepository<State, Long> {

    State findByAbbr(String abbr);

    State findByAbbrOrName(String abbr, String name);
 
    List<State> findByNameLike(@Param("searchString") String searchString);
}
