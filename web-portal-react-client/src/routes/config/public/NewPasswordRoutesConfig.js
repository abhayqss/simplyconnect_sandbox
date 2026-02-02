import { lazy } from 'react'

const NewPassword = lazy(() => import('containers/NewPassword/NewPassword'))

export default {
    component: NewPassword,
    path: '/reset-password',
    exact: true
}
