import { State } from "redux/utils/Form";

const { Record, Set } = require("immutable");

export default class extends State({
  error: null,
  isValid: true,
  isFetching: false,
  validation: Record({
    isSuccess: true,
    errors: Record({
      clients: "",
      noteDate: "",
      subTypeId: "",
      clientProgram: Record({
        typeId: "",
        serviceProvider: "",
        startDate: "",
        endDate: "",
      })(),
      encounter: Record({
        typeId: "",
        toDate: "",
        toTime: "",
        fromDate: "",
        clinicianId: "",
        otherClinician: "",
      })(),
      subjective: "",
    })(),
  })(),
  fields: Record({
    id: null,
    plan: "",
    clients: Set(),
    eventId: null,
    noteDate: "",
    noteName: "",
    subTypeId: null,
    objective: "",
    assessment: "",
    subjective: "",
    clientProgram: Record({
      typeId: null,
      serviceProvider: "",
      startDate: "",
      endDate: "",
    })(),
    encounter: Record({
      typeId: null,
      toDate: "",
      toTime: "",
      fromDate: "",
      clinicianId: null,
      otherClinician: "",
    })(),
  })(),
}) {
  constructor(state) {
    super(state);

    this.updateHashCode();
  }

  changeField(name, value) {
    return super.changeField(name, this.fromJS(value));
  }

  changeFields(changes, shouldUpdateHashCode = false) {
    return super.changeFields(this.fromJS(changes), shouldUpdateHashCode);
  }
}
