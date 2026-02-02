import * as Sentry from '@sentry/react'

class SentryTrackingService {
	catchError(e) {
		return Sentry.captureException(e)
	}

	catchMessage(message) {
		return Sentry.captureMessage(message)
	}
}

export default new SentryTrackingService()