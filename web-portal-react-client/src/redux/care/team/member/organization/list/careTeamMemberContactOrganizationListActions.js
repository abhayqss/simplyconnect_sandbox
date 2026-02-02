import { Actions } from 'redux/utils/List'
import service from 'services/CareTeamMemberService'

import actionTypes from './careTeamMemberContactOrganizationListActionTypes'

export default Actions({
    actionTypes,
    isMinimal: true,
    doLoad: params => service.findOrganizations(params)
})
