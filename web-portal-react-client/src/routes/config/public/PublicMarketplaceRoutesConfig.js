import { lazy } from 'react'

import OldPasswordRoutesConfig from './OldPasswordRoutesConfig'
import NewPasswordRoutesConfig from './NewPasswordRoutesConfig'
import ResetPasswordRoutesConfig from './ResetPasswordRoutesConfig'

const Marketplace = lazy(() => import('containers/Marketplace/Marketplace'))
const Providers = lazy(() => import('containers/Marketplace/Public/Providers/Providers'))
const ProviderDetails = lazy(() => import('containers/Marketplace/Public/Providers/ProviderDetails/ProviderDetails'))

export default {
	component: Marketplace,
	path: '/',
	children: [
		{
			component: Providers,
			path: '',
			exact: true
		},
		{
			component: ProviderDetails,
			path: '/communities/:communityName--@id=:communityId',
			exact: true
		},
		OldPasswordRoutesConfig,
		NewPasswordRoutesConfig,
		ResetPasswordRoutesConfig
	]
}
