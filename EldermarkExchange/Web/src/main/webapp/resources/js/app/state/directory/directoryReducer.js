
exports.directoryReducer = function (state, action) {
    if (!state) return new exports.DirectoryState();

    var types = exports.constants.actionTypes;

    switch (action.type) {
        case types.LOAD_GENDERS_REQUEST:
            return state.setIn(['genders', 'list', 'isFetching'], true);
        case types.LOAD_GENDERS_SUCCESS:
            return state.setIn(['genders', 'list', 'dataSource', 'data'], action.payload);
        case types.LOAD_GENDERS_ERROR:
            return state.setIn(['genders', 'list', 'error'], action.payload);
        case types.LOAD_RACES_REQUEST:
            return state.setIn(['races', 'list', 'isFetching'], true);
        case types.LOAD_RACES_SUCCESS:
            return state.setIn(['races', 'list', 'dataSource', 'data'], action.payload);
        case types.LOAD_RACES_ERROR:
            return state.setIn(['races', 'list', 'error'], action.payload);
    }

    return state;
};