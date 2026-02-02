import InitialState from "./EthnicityInitialState";

import listReducer from "./list/ethnicityListReducer";

const initialState = new InitialState();

export default function ethnicityReducer(state = initialState, action) {
  let nextState = state;

  const list = listReducer(state.list, action);
  if (list !== state.list) nextState = nextState.setIn(["list"], list);

  return nextState;
}
