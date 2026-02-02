const { Record, Set, List } = require('immutable')


const PrimaryContact = Record({
    typeName: '',
    typeTitle: '',
    notificationMethodName: '',
    notificationMethodTitle: '',
    careTeamMemberId: null,
    firstName: '',
    lastName: '',
    active: false,
    isMarkedForDeletion: false
})


export const HousingVouchers = Record({
    tCode: null,
    expiryDate: null,
})


export const Attorney = Record({
    id: null,
    firstName: null,
    lastName: null,
    types: Set(),
    email: null,
    phone: null,
    street: null,
    city: null,
    state: null,
    stateTitle: null,
    zipCode: null
})


export const Insurance = Record({
    id: null,
    groupNumber: null,
    memberNumber: null,
    networkId: null,
    paymentPlan: null,
})

const Association =Record({
    isActive: true,
    /**
     * General Data
     */
    id: null,
    name:'',
    website:'',
    companyId:'',
    email: '',
    phone: '',
    /**
     * Address
     */
    street:'',
    city:'',
    state:'',
    zipCode:'',
    /**
     * Association Logo
     */
    logoPic:'',
    logoPicName:''
})

export  default Association
