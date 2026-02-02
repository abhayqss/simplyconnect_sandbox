import Address from './Address'

const { Record, Set, List } = require('immutable')

const Demographics = {
	firstName: null,
	lastName: null,
	middleName: null,
	fullName: null,
	birthDate: null,
	genderId: null,
	maritalStatusId: null,
	raceId: null,
	race: null,
	ssn: null,
	canEditSsn: false,
	hasNoSsn: false,
	veteranStatusName: null,
	avatar: null,
	avatarId: null,
	avatarName: null
}

const Insurance = {
	insuranceNetworkId: null,
	insuranceNetwork: null,
	insuranceNetworkTitle: null,
	insurancePaymentPlan: null
}

export const RelatedParty = Record({
	firstName: null,
	lastName: null,
	relationshipTypeName: null,
	address: Address(),
	cellPhone: null,
	email: null
})

export const SecondOccupant = Record({
	/*Demographics & Insurance*/
	...Demographics,
	...Insurance,

	/*Contact Information*/
	cellPhone: null,
	email: null,
	address: Address(),
	hasProspectAddress: false
})

const PrimaryContact = Record({
	typeName: null,
	typeTitle: null,
	notificationMethodName: null,
	notificationMethodTitle: null,
	careTeamMemberId: null,
	firstName: null,
	lastName: null,
	active: false
})

const Prospect = Record({
	id: null,
	legacyId: null,

	isActive: true,
	manuallyCreated: true,

	/*Demographics & Insurance*/
	...Demographics,
	...Insurance,

	/*Community*/
	organizationId: null,
	communityId: null,
	community: null,

	/*Contact Information*/
	cellPhone: null,
	email: null,
	address: Address(),

	primaryContact: PrimaryContact(),

	/*Move-In Information*/
	moveInDate: null,
	rentalAgreementSignedDate: null,
	assessmentDate: null,
	referralSource: null,
	notes: null,

	/*related party*/
	relatedParty: RelatedParty(),

	relatedPartyIs2ndOccupant: false,

	/*2nd occupant*/
	secondOccupant: null,

	/*Primary contact*/
	//primaryContact: PrimaryContact(),
})

export default Prospect