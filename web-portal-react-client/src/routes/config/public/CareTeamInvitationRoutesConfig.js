import { lazy } from 'react'

const CareTeamInvitation = lazy(() => import('containers/CareTeam/CareTeamInvitation/CareTeamInvitation'))

export default {
    component: CareTeamInvitation,
    path: '/care-team-invitations/:result',
    exact: true
}
