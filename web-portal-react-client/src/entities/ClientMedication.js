const { Record } = require("immutable");

const ClientMedication = Record({
  name: null,
  ndc: null,
  status: null,
  frequency: null,
  directions: null,
  startedDate: "",
  stoppedDate: "",
  dosageQuantity: "",
  indicatedFor: "",
  comment: "",
});

export default ClientMedication;
