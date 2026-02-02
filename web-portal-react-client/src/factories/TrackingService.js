import { TrackingService } from 'services'

import sentryErrorLogService from 'services/SentryTrackingService'

import memoize from 'memoize-one'

import { tracking } from 'config'

const getService = memoize(() => {
	let service = null

	if (tracking.provider === 'sentry') {
		service = sentryErrorLogService
	}

	return new TrackingService(service)
})

export default function () {
	return getService()
}