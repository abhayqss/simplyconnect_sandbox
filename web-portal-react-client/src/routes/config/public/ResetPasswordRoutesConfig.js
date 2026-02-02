import { lazy } from 'react'

const ResetPassword = lazy(() => import('containers/ResetPassword/ResetPassword'))

export default {
    component: ResetPassword,
    path: '/reset-password-request',
    exact: true
}
