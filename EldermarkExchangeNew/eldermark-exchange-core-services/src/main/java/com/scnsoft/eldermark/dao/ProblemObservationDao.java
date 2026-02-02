package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.dao.basic.AppJpaRepository;
import com.scnsoft.eldermark.entity.document.ccd.ProblemObservation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Set;

@Repository
public interface ProblemObservationDao extends AppJpaRepository<ProblemObservation, Long>, CustomProblemObservationDao {
    final String PROBLEM_IS_ACTIVE = "( lower(po.problemStatusText) = 'active' OR (po.problemStatusText IS NULL AND po.problem.timeLow < current_date() AND (po.problem.timeHigh IS NULL OR po.problem.timeHigh > current_date())))";

    final String PROBLEM_IS_RESOLVED = "(lower(po.problemStatusText) = 'resolved' OR (po.problemStatusText IS NULL AND po.problem.timeHigh < current_date()))";

    final String PROBLEM_IS_OTHER = "(po.problemStatusText IS NULL AND po.problem.timeLow IS NULL AND po.problem.timeHigh IS NULL)";

    final String FIND_PROBLEM_BY_CLIENTIDS = "FROM ProblemObservation pov "
            + " WHERE pov.id IN ( SELECT min(po.id) FROM ProblemObservation po WHERE po.problem.client.id IN (:clientId) "
            + " AND ( (:active = true AND :resolved = true AND :other = true)  " + " OR (:active = true AND "
            + PROBLEM_IS_ACTIVE + ") " + " OR (:resolved = true AND " + PROBLEM_IS_RESOLVED + ") "
            + " OR (:other = true AND (" + PROBLEM_IS_OTHER + " OR NOT (" + PROBLEM_IS_ACTIVE + " OR "
            + PROBLEM_IS_RESOLVED + ")) ))" + " GROUP BY po.problemName)";

    String QUERY_FOR_listClientProblemsWithoutDuplicates = "SELECT po FROM ProblemObservation po" +
            "   INNER JOIN FETCH po.problem p " +
            "   LEFT JOIN FETCH po.problemCode popc " +
            "   LEFT JOIN FETCH po.problemType popt " +
            "WHERE po.id IN (" +
            "SELECT min(po.id) FROM ProblemObservation po INNER JOIN po.problem p " +
            "WHERE p.clientId IN :clientIds AND (" +
            "   (:active = true AND :resolved = true AND :other = true) OR " +
            "   (:active = true AND " + PROBLEM_IS_ACTIVE + ") OR " +
            "   (:resolved = true AND " + PROBLEM_IS_RESOLVED + ") OR " +
            "   (:other = true AND (" + PROBLEM_IS_OTHER + " OR NOT (" + PROBLEM_IS_ACTIVE + " OR " + PROBLEM_IS_RESOLVED + ")))) " +
            "GROUP BY po.problemName, po.problemDateTimeLow, po.problemDateTimeHigh, po.problemStatusText, po.problemIcdCode, po.problemIcdCodeSet)";

    String COUNT_QUERY_FOR_listClientProblemsWithoutDuplicates = "SELECT count(po) FROM ProblemObservation po " +
            "WHERE po.id IN (" +
            "SELECT min(po.id) FROM ProblemObservation po INNER JOIN po.problem p " +
            "WHERE p.clientId IN :clientIds AND (" +
            "   (:active = true AND :resolved = true AND :other = true) OR " +
            "   (:active = true AND " + PROBLEM_IS_ACTIVE + ") OR " +
            "   (:resolved = true AND " + PROBLEM_IS_RESOLVED + ") OR " +
            "   (:other = true AND (" + PROBLEM_IS_OTHER + " OR NOT (" + PROBLEM_IS_ACTIVE + " OR " + PROBLEM_IS_RESOLVED + ")))) " +
            "GROUP BY po.problemName, po.problemDateTimeLow, po.problemDateTimeHigh, po.problemStatusText, po.problemIcdCode, po.problemIcdCodeSet)";

    @Query(value = "SELECT count(pov) " + FIND_PROBLEM_BY_CLIENTIDS)
    Long countProblemByClientIds(@Param("clientId") Collection<Long> clientIds, @Param("active") Boolean active,
                                 @Param("resolved") Boolean resolved, @Param("other") Boolean other);

    @Query(value = "SELECT pov " + FIND_PROBLEM_BY_CLIENTIDS)
    Page<ProblemObservation> findProblemByClientIds(@Param("clientId") Collection<Long> clientIds,
                                                    @Param("active") Boolean active, @Param("resolved") Boolean resolved, @Param("other") Boolean other,
                                                    Pageable pageable);

    @Query(value = QUERY_FOR_listClientProblemsWithoutDuplicates,
            countQuery = COUNT_QUERY_FOR_listClientProblemsWithoutDuplicates
    )
    List<ProblemObservation> listResidentProblemsWithoutDuplicates(@Param("clientIds") Collection<Long> clientIds,
                                                                   @Param("active") boolean includeActive,
                                                                   @Param("resolved") boolean includeResolved,
                                                                   @Param("other") boolean includeOther);

    ProblemObservation findByProblem_Id(Long problemId);


    @Query("SELECT CASE WHEN count(pov) > 0 THEN true ELSE false END " +
            " from ProblemObservation pov " +
            "  left join pov.problemCode c " +
            "  left join pov.translations tr " +
            "where " +
            "   ((c.code in (:codes) and c.codeSystemName = :codeSystemOid) or (pov.problemIcdCode in (:codes) and pov.problemIcdCodeSet in (:codeSystemNames)) or (tr.code in (:codes) and tr.codeSystemName = :codeSystemOid)) " +
            "   and pov.problem.client.id = :clientId")
    boolean existsDiagnosisForClient(@Param("clientId") Long clientId, @Param("codeSystemOid") String codeSystemOid, @Param("codeSystemNames") Set<String> codeSystemNames, @Param("codes") Set<String> codes);

}
