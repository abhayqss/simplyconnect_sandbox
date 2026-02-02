let provider = null

export default class TrackingService {
	constructor(p) {
		provider = p
	}

	setProvider(p) {
		provider = p
	}

	catchError(e) {
		return provider.catchError(e)
	}

	catchMessage(msg) {
		return provider.catchMessage(msg)
	}
}