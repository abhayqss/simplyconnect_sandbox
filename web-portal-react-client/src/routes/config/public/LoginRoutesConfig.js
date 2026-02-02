import { lazy } from 'react'

const Login = lazy(() => import('containers/Login/Login'))

export default {
    component: Login,
    path: '/home',
    exact: true
}
