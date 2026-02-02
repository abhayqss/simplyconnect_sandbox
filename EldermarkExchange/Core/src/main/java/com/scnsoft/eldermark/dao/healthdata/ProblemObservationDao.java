package com.scnsoft.eldermark.dao.healthdata;

import com.google.common.base.Optional;
import com.scnsoft.eldermark.entity.ProblemObservation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Date;
import java.util.List;


/**
 * @author phomal
 * Created on 11/4/2017.
 */
@Repository
public interface ProblemObservationDao extends JpaRepository<ProblemObservation, Long> {

    String PROBLEM_IS_ACTIVE = "(lower(po.problemStatusText) = 'active' OR (po.problemStatusText IS NULL AND p.timeLow < current_date() AND (p.timeHigh IS NULL OR p.timeHigh > current_date())))";
    String PROBLEM_IS_RESOLVED = "(lower(po.problemStatusText) = 'resolved' OR (po.problemStatusText IS NULL AND p.timeHigh < current_date()))";
    String PROBLEM_IS_OTHER = "(po.problemStatusText IS NULL AND p.timeLow IS NULL AND p.timeHigh IS NULL)";

    Sort.Order ORDER_BY_START_DATE_DESC = new Sort.Order(Sort.Direction.DESC, "p.timeLow");
    Sort.Order ORDER_BY_NAME = new Sort.Order(Sort.Direction.ASC, "problemName");

    List<ProblemObservation> findByProblemResidentId(Long residentId);

    Long countByProblemResidentIdIn(Collection<Long> residentIds);

    @Query("SELECT po FROM ProblemObservation po INNER JOIN FETCH po.problem p " +
            "WHERE p.residentId = :residentId AND (" +
            "(:active = true AND :resolved = true AND :other = true) OR " +
            "(:active = true AND " + PROBLEM_IS_ACTIVE + ") OR " +
            "(:resolved = true AND " + PROBLEM_IS_RESOLVED + ") OR " +
            "(:other = true AND (" + PROBLEM_IS_OTHER + " OR NOT (" + PROBLEM_IS_ACTIVE + " OR " + PROBLEM_IS_RESOLVED + "))))")
    List<ProblemObservation> listResidentProblems(@Param("residentId") Long residentId,
                                                  @Param("active") boolean includeActive,
                                                  @Param("resolved") boolean includeResolved,
                                                  @Param("other") boolean includeOther);


    String QUERY_FOR_listResidentProblemsWithoutDuplicates = "SELECT po FROM ProblemObservation po" +
            "   INNER JOIN FETCH po.problem p " +
            "   LEFT JOIN FETCH po.problemCode popc " +
            "   LEFT JOIN FETCH po.problemType popt " +
            "WHERE po.id IN (" +
            "SELECT min(po.id) FROM ProblemObservation po INNER JOIN po.problem p " +
            "WHERE p.residentId IN :residentIds AND (" +
            "   (:active = true AND :resolved = true AND :other = true) OR " +
            "   (:active = true AND " + PROBLEM_IS_ACTIVE + ") OR " +
            "   (:resolved = true AND " + PROBLEM_IS_RESOLVED + ") OR " +
            "   (:other = true AND (" + PROBLEM_IS_OTHER + " OR NOT (" + PROBLEM_IS_ACTIVE + " OR " + PROBLEM_IS_RESOLVED + ")))) " +
            "GROUP BY po.problemName, po.problemDateTimeLow, po.problemDateTimeHigh, po.problemStatusText, po.problemIcdCode, po.problemIcdCodeSet)";

    String COUNT_QUERY_FOR_listResidentProblemsWithoutDuplicates = "SELECT count(po) FROM ProblemObservation po " +
            "WHERE po.id IN (" +
            "SELECT min(po.id) FROM ProblemObservation po INNER JOIN po.problem p " +
            "WHERE p.residentId IN :residentIds AND (" +
            "   (:active = true AND :resolved = true AND :other = true) OR " +
            "   (:active = true AND " + PROBLEM_IS_ACTIVE + ") OR " +
            "   (:resolved = true AND " + PROBLEM_IS_RESOLVED + ") OR " +
            "   (:other = true AND (" + PROBLEM_IS_OTHER + " OR NOT (" + PROBLEM_IS_ACTIVE + " OR " + PROBLEM_IS_RESOLVED + ")))) " +
            "GROUP BY po.problemName, po.problemDateTimeLow, po.problemDateTimeHigh, po.problemStatusText, po.problemIcdCode, po.problemIcdCodeSet)";

    @Query(value = QUERY_FOR_listResidentProblemsWithoutDuplicates,
            countQuery = COUNT_QUERY_FOR_listResidentProblemsWithoutDuplicates
    )
    Page<ProblemObservation> listResidentProblemsWithoutDuplicates(@Param("residentIds") Collection<Long> residentIds,
                                                                   @Param("active") boolean includeActive,
                                                                   @Param("resolved") boolean includeResolved,
                                                                   @Param("other") boolean includeOther,
                                                                   final Pageable pageable);

    @Query(value = QUERY_FOR_listResidentProblemsWithoutDuplicates,
            countQuery = COUNT_QUERY_FOR_listResidentProblemsWithoutDuplicates
    )
    List<ProblemObservation> listResidentProblemsWithoutDuplicates(@Param("residentIds") Collection<Long> residentIds,
                                                                   @Param("active") boolean includeActive,
                                                                   @Param("resolved") boolean includeResolved,
                                                                   @Param("other") boolean includeOther);

    @Query("SELECT count(po) FROM ProblemObservation po " +
            "WHERE po.id IN (" +
            "SELECT min(po.id) FROM ProblemObservation po INNER JOIN po.problem p " +
            "WHERE p.residentId IN :residentIds " +
            "GROUP BY po.problemName, po.problemDateTimeLow, po.problemDateTimeHigh, po.problemStatusText, po.problemIcdCode, po.problemIcdCodeSet)")
    Long countResidentProblemsWithoutDuplicates(@Param("residentIds") Collection<Long> residentIds);

    Optional<ProblemObservation> getByProblemResidentIdAndPrimaryIsTrue(@Param("residentId") Long residentId);

    Optional<ProblemObservation> getTopByProblemNameAndProblemTypeIdAndProblemResidentId(String problemName, Long problemTypeId, Long residentId);

}
