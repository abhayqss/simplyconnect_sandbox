const { List, Record } = require('immutable')

const Appointment = Record({
	title: null,
	status: null,
	isPublic: true,
	organizationId: null,
	communityId: null,
	location: null,
	type: null,
	serviceCategory: null,
	referralSource: null,
	reasonForVisit: null,
	directionsInstructions: null,
	notes: null,

	clientId: null,
	creator: null,
	date: null,
	from: null,
	to: null,
	serviceProviderIds: List(),
	isExternalProviderServiceProvider: false,

	reminders: List(),
	notificationMethods: List(),
	email: null,
	phone: null
})

export default Appointment
