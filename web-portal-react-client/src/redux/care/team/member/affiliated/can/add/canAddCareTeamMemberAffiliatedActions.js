import { Actions } from 'redux/utils/Value'

import actionTypes from './canAddCareTeamMemberAffiliatedActionsTypes'

import service from 'services/CareTeamMemberService'

import { CARE_TEAM_AFFILIATION_TYPES } from 'lib/Constants'

const { AFFILIATED } = CARE_TEAM_AFFILIATION_TYPES

export default Actions({
    actionTypes,
    doLoad: params => service.canAdd({ affiliation: AFFILIATED, ...params })
})