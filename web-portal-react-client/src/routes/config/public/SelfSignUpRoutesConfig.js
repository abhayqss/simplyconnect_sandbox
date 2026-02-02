import { lazy } from 'react'

const SelfSignUp = lazy(() => import('containers/SelfSignUp/SelfSignUp'))

export default {
    component: SelfSignUp,
    path: '/sign-up/expired',
    exact: true
}