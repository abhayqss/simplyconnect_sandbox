const { Record, List } = require('immutable')

const Client = Record({
    fullName: '',
    unit: '',
    phone: '',
    siteName: '',
    address: '',
})

const Notification = Record({
    family: Record({
        date: null,
        byWhom: '',
        fullName: '',
        phone: '',
        isNotified: false,
    })(),
    friend: Record({
        date: null,
        byWhom: '',
        fullName: '',
        phone: '',
        isNotified: false,
    })(),
    physician: Record({
        date: null,
        byWhom: '',
        fullName: '',
        phone: '',
        response: '',
        responseDate: null,
        isNotified: false,
    })(),
    adultProtectiveServices: Record({
        date: null,
        byWhom: '',
        isNotified: false,
    })(),
    careManager: Record({
        date: null,
        byWhom: '',
        fullName: '',
        phone: '',
        isNotified: false,
    })(),
    ohioHealthDepartment: Record({
        date: null,
        byWhom: '',
        isNotified: false,
    })(),
    emergency: Record({
        date: null,
        byWhom: '',
        isNotified: false,
    })(),
    police: Record({
        date: null,
        byWhom: '',
        isNotified: false,
    })(),
    other: Record({
        date: null,
        byWhom: '',
        isNotified: false,
        comment: '',
    })()
})

const VitalSigns = Record({
    bloodPressure: '',
    pulse: '',
    respirationRate: '',
    temperature: '',
    o2Saturation: '',
    bloodSugar: '',
})

export default Record({
    id: null,
    eventId: null,
    statusName: 'DRAFT',
    client: Client(),
    incidentDate: null,
    incidentDiscoveredDate: '',
    wasProviderPresentOrScheduled: null,
    places: List(),
    weatherConditions: List(),
    incidentDetails: '',
    wasIncidentParticipantTakenToHospital: null,
    incidentParticipantHospitalName: '',
    wereApparentInjuries: null,
    injuries: List(),
    currentInjuredClientCondition: '',
    vitalSigns: VitalSigns(),
    witnesses: List(),
    wereOtherIndividualsInvolved: null,
    involvedIndividuals: List(),
    incidentPictureFiles: List(),
    notification : Notification(),
    immediateIntervention: '',
    followUpInformation: '',
    completedBy: '',
    completedByPosition: '',
    completedByPhone: '',
    completedDate: null,
    reportedBy: '',
    reportedByPosition: '',
    reportedByPhone: '',
    reportDate: null,

    // title: '',
    // incidentNarrative: '',
    // agencyResponseToIncident: '',
    // wasIncidentCausedBySubstance: false,
    // notifiedPersons: List(),
    // level1IncidentTypes: List(),
	// level2IncidentTypes: List(),
    // level3IncidentTypes: List(),
})
