package com.scnsoft.eldermark.test.mock;

import com.scnsoft.eldermark.entity.State;
import com.scnsoft.eldermark.services.StateServiceImpl;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by knetkachou on 1/19/2017.
 */
public class StateServiceMock extends StateServiceImpl {
    Map<Long, State> statesByIdMap = new HashMap<Long, State>();
    Map<String, State> statesByAbbrMap = new HashMap<String, State>();

    public StateServiceMock() {

        statesByIdMap.put(2L, createState(1L, "AL"));
        statesByIdMap.put(3L, createState(2L, "AZ"));
        statesByIdMap.put(4L, createState(3L, "CA"));
        statesByIdMap.put(5L, createState(4L, "CT"));
        statesByIdMap.put(6L, createState(5L, "FL"));
        statesByIdMap.put(7L, createState(6L, "GA"));
        statesByIdMap.put(8L, createState(7L, "ID"));
        statesByIdMap.put(9L, createState(8L, "IN"));
        statesByIdMap.put(10L, createState(9L, "KS"));
        statesByIdMap.put(11L, createState(10L, "NH"));
        statesByIdMap.put(12L, createState(11L, "NM"));
        statesByIdMap.put(13L, createState(12L, "ND"));
        statesByIdMap.put(14L, createState(13L, "OK"));
        statesByIdMap.put(15L, createState(14L, "OR"));
        statesByIdMap.put(16L, createState(15L, "RI"));
        statesByIdMap.put(17L, createState(16L, "SD"));
        statesByIdMap.put(18L, createState(17L, "TN"));
        statesByIdMap.put(19L, createState(18L, "UT"));
        statesByIdMap.put(20L, createState(19L, "VA"));
        statesByIdMap.put(21L, createState(20L, "WV"));
        statesByIdMap.put(22L, createState(21L, "WY"));
        statesByIdMap.put(23L, createState(22L, "ME"));
        statesByIdMap.put(24L, createState(23L, "MA"));
        statesByIdMap.put(24L, createState(24L, "MN"));
        statesByIdMap.put(25L, createState(25L, "MO"));
        statesByIdMap.put(26L, createState(26L, "NE"));
        statesByIdMap.put(27L, createState(27L, "NV"));
        statesByIdMap.put(28L, createState(28L, "NJ"));
        statesByIdMap.put(29L, createState(29L, "NY"));
        statesByIdMap.put(30L, createState(30L, "NC"));
        statesByIdMap.put(31L, createState(31L, "OH"));
        statesByIdMap.put(32L, createState(32L, "PA"));
        statesByIdMap.put(33L, createState(33L, "SC"));
        statesByIdMap.put(34L, createState(34L, "VT"));
        statesByIdMap.put(35L, createState(35L, "WA"));
        statesByIdMap.put(36L, createState(36L, "WI"));
        statesByIdMap.put(37L, createState(37L, "AK"));
        statesByIdMap.put(38L, createState(38L, "AR"));
        statesByIdMap.put(39L, createState(39L, "CO"));
        statesByIdMap.put(40L, createState(40L, "DE"));
        statesByIdMap.put(41L, createState(41L, "HI"));
        statesByIdMap.put(42L, createState(42L, "IL"));
        statesByIdMap.put(43L, createState(43L, "IA"));
        statesByIdMap.put(44L, createState(44L, "KY"));
        statesByIdMap.put(45L, createState(45L, "LA"));
        statesByIdMap.put(46L, createState(46L, "MD"));
        statesByIdMap.put(47L, createState(47L, "MI"));
        statesByIdMap.put(48L, createState(48L, "MS"));
        statesByIdMap.put(49L, createState(49L, "MT"));
        statesByIdMap.put(50L, createState(50L, "TX"));


        for (Map.Entry<Long, State> state : statesByIdMap.entrySet()) {
            statesByAbbrMap.put(state.getValue().getAbbr(), state.getValue());
        }
    }

    @Override
    public State findByAbbr(String abbr) {
        return statesByAbbrMap.get(abbr);
    }

    @Override
    public State get(Long id) {
        return statesByIdMap.get(id);
    }


    private State createState(Long id, String abbr) {
        State state = new State();
        state.setId(id);
        state.setAbbr(abbr);
        state.setName(abbr);
        return state;
    }
}
