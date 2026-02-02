const { Record } = require('immutable')

const ReferralMarketplace = Record({
    communityId: null,
    sharedChannel: '',
    sharedFax: '',
    sharedPhone: '',
    sharedFaxComment: ''
})

export default ReferralMarketplace