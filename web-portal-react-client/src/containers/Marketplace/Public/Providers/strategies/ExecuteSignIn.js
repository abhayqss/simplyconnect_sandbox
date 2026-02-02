import config from 'config'

import { getQueryParamsString } from 'lib/utils/UrlUtils'

export default function ExecuteSignIn(context) {
	const { data } = context
	//window.location.href = `${config.location.origin}${config.context}/home?${getQueryParamsString({ ...data, target: 'marketplace' })}`
}