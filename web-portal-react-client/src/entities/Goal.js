const { Record } = require('immutable')

export default Record({
    id: null,

    goal: '',
    goalHasError: false,
    goalErrorText: '',

    barriers: '',
    barriersHasError: false,
    barriersErrorText: '',

    interventionAction: '',
    interventionActionHasError: false,
    interventionActionErrorText: '',

    providerName: null,
    providerNameHasError: false,
    providerNameErrorText: '',

    providerEmail: null,
    providerEmailHasError: false,
    providerEmailErrorText: '',

    providerPhone: null,
    providerPhoneHasError: false,
    providerPhoneErrorText: '',

    providerAddress: '',

    wasPreviouslyInPlace: false,

    resourceName: null,
    resourceNameHasError: false,
    resourceNameErrorText: '',

    isOngoingService: null,
    isOngoingServiceHasError: false,
    isOngoingServiceErrorText: '',

    contactName: null,
    contactNameHasError: false,
    contactNameErrorText: '',

    serviceCtrlReqStatusId: null,

    serviceStatusId: null,

    targetCompletionDate: '',
    targetCompletionDateHasError: false,
    targetCompletionDateErrorText: '',

    completionDate: '',
    completionDateHasError: false,
    completionDateErrorText: '',

    goalCompletion: '',
    goalCompletionHasError: false,
    goalCompletionErrorText: ''
})