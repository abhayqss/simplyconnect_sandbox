const { Record, List, Set } = require('immutable')

const Marketplace = Record({
    id: null,
    confirmVisibility: false,
    referralEmails: List(),
    servicesSummaryDescription: '',
    serviceCategoryIds: Set(),
    serviceIds: Set(),
    languageIds: Set(),
    allowExternalInboundReferrals: false,
    featuredCommunities: List(),
    isSaved: false,
    rating: 0
})

export default Marketplace