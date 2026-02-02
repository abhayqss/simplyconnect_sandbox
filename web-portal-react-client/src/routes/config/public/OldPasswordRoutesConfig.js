import { lazy } from 'react'

const OldPassword = lazy(() => import('containers/OldPassword/OldPassword'))

export default {
	component: OldPassword,
	path: '/old-password',
	exact: true
}
