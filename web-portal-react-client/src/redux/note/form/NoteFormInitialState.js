import { State } from "redux/utils/Form";
import Struct from "redux/utils/Struct";

export default class extends State({
  error: null,
  isValid: true,
  isFetching: false,
  validation: Struct({
    isSuccess: true,
    errors: Struct({
      noteDate: "",
      subTypeId: "",
      clientProgram: Struct({
        typeId: "",
        serviceProvider: "",
        startDate: "",
        endDate: "",
      }),
      encounter: Struct({
        typeId: "",
        toDate: "",
        toTime: "",
        fromDate: "",
        clinicianId: "",
        otherClinician: "",
      }),

      subjective: "",
      admitDateId: "",
      serviceStatusCheck: Struct({
        auditPerson: "",
        checkDate: "",
        resourceName: "",
        providerName: "",
        serviceProvided: "",
        servicePlanCreatedDate: "",
      }),
    }),
  }),
  fields: Struct({
    id: null,
    plan: "",
    eventId: null,
    noteDate: null,
    subTypeId: null,
    objective: "",
    assessment: "",
    subjective: "",
    clientProgram: Struct({
      typeId: null,
      serviceProvider: "",
      startDate: null,
      endDate: null,
    }),
    encounter: Struct({
      typeId: null,
      toDate: null,
      toTime: "",
      fromDate: null,
      clinicianId: null,
      otherClinician: "",
    }),

    admitDateId: "",
    serviceStatusCheck: Struct({
      checkDate: "",
      auditPerson: "",
      resourceName: "",
      providerName: "",
      nextCheckDate: "",
      servicePlanId: null,
      serviceProvided: null,
      servicePlanCreatedDate: "",
    }),
  }),
}) {
  constructor(state) {
    super(state);

    this.updateHashCode();
  }
}
