import { lazy } from 'react'

const Invitation = lazy(() => import('containers/Invitation/Invitation'))

export default {
    component: Invitation,
    path: '/invitation',
    exact: true
}
