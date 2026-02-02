import Can from './can/CanContactInitialState'
import Form from './form/ContactFormInitialState'
import Role from './role/ContactRoleInitialState'
import Count from './count/ContactCountInitialState'
import Community from './community/CommunityInitialState'
import Details from './details/ContactDetailsInitialState'
import History from './history/ContactHistoryInitialState'
import Organization from './organization/OrganizationInitialState'

const { Record } = require('immutable')

const InitialState = Record({
    can: Can(),
    form: Form(),
    role: Role(),
    count: Count(),
    details: Details(),
    history: History(),
    community: Community(),
    organization: Organization(),
})

export default InitialState