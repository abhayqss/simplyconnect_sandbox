const { Record, List, Set } = require('immutable')

const Marketplace = Record({
    id: null,
    confirmVisibility: false,
    servicesSummaryDescription: '',
    serviceCategoryIds: Set(),
    serviceIds: Set(),
    languageIds: Set()
})

const Features = Record({
    canEdit: true,
    isChatEnabled: true,
    isVideoEnabled: true,
    isSignatureEnabled: false,
    areAppointmentsEnabled: false,
    isPaperlessHealthcareEnabled: false,
    areComprehensiveAssessmentsEnabled: false
})

const Organization = Record({
    id: null,
    name: '',
    oid: '',
    companyId: '',
    email: '',
    phone: '1',
    street: '',
    city: '',
    stateId: null,
    zipCode: null,
    logo: null,
    logoName: '',
    hasCommunities: false,
    marketplace: Marketplace(),
    features: Features(),
    allowExternalInboundReferrals: false,
    affiliatedRelationships: List()
})

export default Organization
