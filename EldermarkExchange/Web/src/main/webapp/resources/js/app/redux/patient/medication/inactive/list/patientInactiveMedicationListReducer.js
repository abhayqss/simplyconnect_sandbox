define(
    [
        path('app/lib/Constants'),
        path('./PatientInactiveMedicationListInitialState')
    ],
    function (varants, InitialState) {
        var types = varants.actionTypes;
        var FIRST_PAGE = varants.FIRST_PAGE;

        return function (state, action) {
            state = state || new InitialState();

            if (!(state instanceof InitialState)) {
                return initialState.mergeDeep(state)
            }

            var payload = action.payload;

            switch (action.type) {
                case types.CLEAN_PATIENT_INACTIVE_MEDICATIONS:
                    return state
                        .removeIn(['error'])
                        .setIn(['isFetching'], false)
                        .removeIn(['dataSource', 'data']);

                case types.CLEAN_PATIENT_INACTIVE_MEDICATION_LIST_ERROR:
                    return state.removeIn(['error']);

                case types.LOAD_PATIENT_INACTIVE_MEDICATIONS_REQUEST:
                    return state
                        .setIn(['error'], null)
                        .setIn(['isFetching'], true);

                case types.LOAD_PATIENT_INACTIVE_MEDICATIONS_SUCCESS: {
                    var page = payload.page;
                    var pageSize = payload.pageSize;
                    var totalCount = payload.totalCount;
                    var patientId = payload.patientId || null;

                    var nextState = state.setIn(['isFetching'], false)
                        .setIn(['dataSource', 'pagination', 'page'], page)
                        .setIn(['dataSource', 'pagination', 'pageSize'], pageSize)
                        .setIn(['dataSource', 'pagination', 'totalCount'], totalCount)
                        .setIn(['dataSource', 'filter', 'patientId'], patientId);

                    var data = action.payload.data;
                    var prevData = state.getIn(['dataSource', 'data']) || [];
                    data = page === FIRST_PAGE ? data : $.extend(prevData, data);

                    return nextState.setIn(['dataSource', 'data'], data);
                }

                case types.LOAD_PATIENT_INACTIVE_MEDICATIONS_FAILURE:
                    return state.setIn(['isFetching'], false)
                        .setIn(['error'], action.payload)
            }

            return state;
        }
    }
);