import List from "./list/ethnicityListInitialState";

const { Record } = require("immutable");

const InitialState = Record({
  list: new List(),
});

export default InitialState;
