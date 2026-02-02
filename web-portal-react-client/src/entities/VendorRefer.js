import Address from './Address'

const { Record, Set, List } = require('immutable')

const VendorRefer = Record({
    referContent:'',
    sourceCommunityId:'',
    sourcePhone:'',
    sourceContactEmail:'',
    sourceAddress:'',
    referObject: null,
    sourceContactId: '',
    contactWay:'',
    sourceOrganizationId:'',
    sourceCommunityEmail:'',
})

export default VendorRefer
