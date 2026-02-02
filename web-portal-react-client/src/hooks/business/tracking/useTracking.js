import { TrackingService } from 'factories'

const service = new TrackingService()

const api = {
	catchError: e => service.catchError(e),
	catchMessage: msg => service.catchMessage(msg)
}

export default function useTracking() {
	return api
}