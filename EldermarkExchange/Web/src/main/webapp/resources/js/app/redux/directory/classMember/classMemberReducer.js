define(
    [
        path('./ClassMemberInitialState'),
        path('./type/classMemberTypeReducer')
    ],
    function (InitialState, typeReducer) {
        return function (state, action) {
            state = state || InitialState();

            var nextState = state;

            var type = typeReducer(state.type, action);
            if (type !== state.type) nextState = state.setIn(['type'], type);

            return nextState;
        }
    }
);