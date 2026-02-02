import List from './list/CareTeamMemberListInitialState'
import Form from './form/CareTeamMemberFormInitialState'
import Count from './count/CareTeamMemberCountInitialState'
import Details from './details/CareTeamMemberDetailsInitialState'
import Affiliated from './affiliated/CareTeamMemberAffiliatedInitialState'
import History from './history/CareTeamMemberHistoryInitialState'
import Can from './can/CanCareTeamMemberInitialState'
import Organization from './organization/CareTeamMemberContactOrganizationInitialState'

const { Record } = require('immutable')

const InitialState = Record({
    list: new List(),
    form: new Form(),
    count: new Count(),
    details: new Details(),
    history: new History(),
    affiliated: Affiliated(),
    can: new Can(),
    organization: Organization(),
})

export default InitialState