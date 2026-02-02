package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.entity.State;
import org.springframework.stereotype.Repository;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;

/**
 * Created by pzhurba on 24-Sep-15.
 */
@Repository
public class StateDaoImpl extends BaseDaoImpl<State> implements StateDao {
    public StateDaoImpl() {
        super(State.class);
    }

    @Override
    public State findByAbbr(String abbr) {
        TypedQuery<State> query = entityManager.createQuery("Select o from State o WHERE o.abbr =:abbr", State.class);
        query.setParameter("abbr", abbr);
        return query.getSingleResult();
    }

    @Override
    public State findByAbbrOrFullName(String nameOrAbbr) {
        TypedQuery<State> query = entityManager.createQuery("Select o from State o WHERE o.name =:nameOrAbbr OR o.abbr = :nameOrAbbr", State.class);
        query.setParameter("nameOrAbbr", nameOrAbbr);
        List<State> results = query.getResultList();
        if (results.size()==0) return null;
        if (results.size()>1) throw new RuntimeException("There are more that 1 state with name "+nameOrAbbr);
        return results.get(0);
    }

    @Override
    public List<State> searchByFullNameLike(String searchText) {
        String likeFormatSearchStr = String.format("%%%s%%", searchText);
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery stateCriteria = cb.createQuery(State.class);
        Root<State> stateRoot = stateCriteria.from(State.class);
        Predicate fullStateLike = cb.like(stateRoot.<String>get("name"), likeFormatSearchStr);
        stateCriteria.where(fullStateLike);
        List<State> result = entityManager.createQuery(stateCriteria).getResultList();
        return result;
    }

}
