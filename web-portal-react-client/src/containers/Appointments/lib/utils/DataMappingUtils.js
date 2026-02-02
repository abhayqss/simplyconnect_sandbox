import {
	EVENT_COLORS,
	EVENT_DECORATORS
} from 'components/Calendar/Constants'

import {
	APPOINTMENT_TYPE_EVENT_COLORS
} from '../Constants'

const {
	CROSSED_OUT,
	NO_BACKGROUND
} = EVENT_DECORATORS

export function mapAppointmentToCalendarEvent(o) {
	if (!o) return

	const {
		id,
		canView,
		typeName,
		typeTitle,
		clientName,
		statusName,
		dateFrom,
		dateTo,
		communityName
	} = o

	let color = APPOINTMENT_TYPE_EVENT_COLORS[typeName]
	let decorators = []

	if (!canView) {
		color = EVENT_COLORS.GRAY
	}

	if (['CANCELLED', 'ENTERED_IN_ERROR'].includes(statusName)) {
		color = EVENT_COLORS.GRAY
		decorators = [CROSSED_OUT, NO_BACKGROUND]
	}

	return {
		id,
		color,
		decorators,
		type: typeTitle,
		title: clientName,
		startDate: dateFrom,
		endDate: dateTo,
		text: canView ? communityName : 'Busy',
		data: o
	}
}