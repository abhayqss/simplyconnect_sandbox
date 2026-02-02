package com.scnsoft.eldermark.services.merging;

import com.google.common.collect.Iterables;
import com.scnsoft.eldermark.entity.MpiMergedResidents;
import com.scnsoft.eldermark.entity.Resident;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

/**
 * A class for choosing a surviving (main) Resident from a collection of residents.
 *
 * @author phomal
 * Created on 4/12/2017.
 */
@Component
public class SurvivingResidentSelector {

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Resident selectByMaxOccurrence(Iterable<Resident> residents) {
        if (Iterables.isEmpty(residents)) {
            return null;
        }

        Map<Resident, Integer> mainResidentVotes = new HashMap<Resident, Integer>();
        for (Resident resident : residents) {
            if (!CollectionUtils.isEmpty(resident.getSecondaryResidents())) {
                vote(mainResidentVotes, resident);
            }
            if (!CollectionUtils.isEmpty(resident.getMainResidents())) {
                for (MpiMergedResidents mpiMergedResidents : resident.getMainResidents()) {
                    if (Iterables.contains(residents, mpiMergedResidents.getSurvivingResident())) {
                        vote(mainResidentVotes, mpiMergedResidents.getSurvivingResident());
                    }
                }
            }
        }

        Integer maxVotes = -1;
        Resident survivingResident = Iterables.get(residents, 0);
        for (Map.Entry<Resident, Integer> entry : mainResidentVotes.entrySet()) {
            if (entry.getValue() > maxVotes) {
                maxVotes = entry.getValue();
                survivingResident = entry.getKey();
            }
        }

        return survivingResident;
    }

    private static <K, V> V getOrDefault(Map<K, V> map, K key, V defaultValue) {
        return map.containsKey(key) ? map.get(key) : defaultValue;
    }

    private Integer vote(Map<Resident, Integer> mainResidentVotes, Resident resident) {
        Integer votes = getOrDefault(mainResidentVotes, resident, 0) + 1;
        mainResidentVotes.put(resident, votes);
        return votes;
    }

}
