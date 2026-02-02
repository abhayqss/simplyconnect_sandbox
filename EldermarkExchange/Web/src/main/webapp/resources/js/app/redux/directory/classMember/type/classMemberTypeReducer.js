define(
    [
        path('./ClassMemberTypeInitialState'),
        path('./list/classMemberTypeListReducer')
    ],
    function (InitialState, listReducer) {
        return function (state, action) {
            state = state || InitialState();

            var nextState = state;

            var list = listReducer(state.list, action);
            if (list !== state.list) nextState = state.setIn(['list'], list);

            return nextState;
        }
    }
);