import React from 'react'

import {
	where,
	findWhere
} from 'underscore'

import { rest } from 'msw'
import { setupServer } from 'msw/node'
import { waitFor, render, within } from 'lib/test-utils'

import { add } from 'date-arithmetic'

import {
	CONTACT_STATUSES
} from 'lib/Constants'

import { format, formats } from 'lib/utils/DateUtils'

import Response from 'lib/mock/server/Response'

import {
	User,
	Client,
	Community,
	Organization,
	AppointmentType,
	AppointmentStatus,
	NotificationMethods,
	AppointmentReminderType
} from 'lib/mock/db/DB'

import AppointmentForm from './AppointmentForm'
import { getFormattedTimeRange } from '../lib/utils/RepeatTimeUtils'

const {
	ACTIVE
} = CONTACT_STATUSES

const BASE_URL = 'https://dev.simplyconnect.me/web-portal-mock-backend'

const data = {
	creatorIds: [1, 2, 3],
	clientIds: [1, 2, 3],
	clientStatus: ACTIVE,
	serviceProviderIds: [4, 5, 6],
	types: [],
	statuses: [],
	hasNoServiceProviders: false,
	isExternalProviderServiceProvider: false,
	clientsWithAccessibleAppointments: true,
}

const server = setupServer(
	rest.get(`${BASE_URL}/authorized-directory/organizations`, (req, res, ctx) => {
		return res(
			ctx.json(Response.success(Organization))
		)
	}),
	rest.get(`${BASE_URL}/authorized-directory/communities`, (req, res, ctx) => {
		const organizationId = +req.url.searchParams.get('organizationId')

		return res(
			ctx.json(Response.success(where(Community, { organizationId })))
		)
	}),
	rest.get(`${BASE_URL}/authorized-directory/appointments/types`, (req, res, ctx) => {
		return res(
			ctx.json(Response.success(AppointmentType))
		)
	}),
	rest.get(`${BASE_URL}/authorized-directory/appointments/statuses`, (req, res, ctx) => {
		return res(
			ctx.json(Response.success(AppointmentStatus))
		)
	}),
	rest.get(`${BASE_URL}/authorized-directory/appointments/reminder-types`, (req, res, ctx) => {
		return res(
			ctx.json(Response.success(AppointmentReminderType))
		)
	}),
	rest.get(`${BASE_URL}/authorized-directory/appointments/notification-methods`, (req, res, ctx) => {
		return res(
			ctx.json(Response.success(NotificationMethods))
		)
	}),
	rest.get(`${BASE_URL}/clients/:clientId`, (req, res, ctx) => {
		return res(
			ctx.json(Response.success(findWhere(Client, { id: +req.params.clientId })))
		)
	})
)

function changeField(name, value) {
	data[name] = value
}

function changeFields(changes) {
	Object.assign(data, changes)
}

describe('<AppointmentForm>:', () => {
	beforeAll(() => {
		server.listen()
	})

	afterEach(() => {
		jest.clearAllMocks()
	})

	it('is visible on UI', async () => {
		let { getByTestId } = render(
			<AppointmentForm/>
		)

		const node = getByTestId('appointmentForm')

		expect(node).toBeInTheDocument()
		expect(node).toBeVisible()
	})

	describe('Appointment section:', () => {
		describe("has fields:", () => {
			it('Public Calendar', async () => {
				let { getByTestId } = render(<AppointmentForm/>)
				const node = getByTestId('isPublic_field')
				expect(node).toBeInTheDocument()
				expect(node).toBeVisible()
			})

			it('Title', async () => {
				let { getByTestId } = render(<AppointmentForm/>)
				const node = getByTestId('title_field')
				expect(node).toBeInTheDocument()
				expect(node).toBeVisible()
			})

			it('Appointment Status', async () => {
				let { getByTestId } = render(<AppointmentForm/>)
				const node = getByTestId('status_field')
				expect(node).toBeInTheDocument()
				expect(node).toBeVisible()
			})

			it('Organization Name', async () => {
				let { getByTestId } = render(<AppointmentForm/>)
				const node = getByTestId('organizationId_field')
				expect(node).toBeInTheDocument()
				expect(node).toBeVisible()
			})

			it('Community Name', async () => {
				let { getByTestId } = render(<AppointmentForm/>)
				const node = getByTestId('communityId_field')
				expect(node).toBeInTheDocument()
				expect(node).toBeVisible()
			})

			it('Location', async () => {
				let { getByTestId } = render(<AppointmentForm/>)
				const node = getByTestId('location_field')
				expect(node).toBeInTheDocument()
				expect(node).toBeVisible()
			})

			it('Appointment Type', async () => {
				let { getByTestId } = render(<AppointmentForm/>)
				const node = getByTestId('type_field')
				expect(node).toBeInTheDocument()
				expect(node).toBeVisible()
			})

			it('Service Category', async () => {
				let { getByTestId } = render(<AppointmentForm/>)
				const node = getByTestId('serviceCategory_field')
				expect(node).toBeInTheDocument()
				expect(node).toBeVisible()
			})

			it('Referral Source', async () => {
				let { getByTestId } = render(<AppointmentForm/>)
				const node = getByTestId('referralSource_field')
				expect(node).toBeInTheDocument()
				expect(node).toBeVisible()
			})

			it('Reason for Visit', async () => {
				let { getByTestId } = render(<AppointmentForm/>)
				const node = getByTestId('reasonForVisit_field')
				expect(node).toBeInTheDocument()
				expect(node).toBeVisible()
			})

			it('Appointment Direction & Instructions', async () => {
				let { getByTestId } = render(<AppointmentForm/>)
				const node = getByTestId('directionsInstructions_field')
				expect(node).toBeInTheDocument()
				expect(node).toBeVisible()
			})

			it('Notes', async () => {
				let { getByTestId } = render(<AppointmentForm/>)
				const node = getByTestId('notes_field')
				expect(node).toBeInTheDocument()
				expect(node).toBeVisible()
			})
		})

		describe("from which marked as required:", () => {
			it('Title', async () => {
				let { getByTestId } = render(<AppointmentForm/>)
				const node = getByTestId('title_field-label')
				expect(String(node.innerHTML).endsWith('*')).toBeTruthy()
			})

			it('Appointment Status', async () => {
				let { getByTestId } = render(<AppointmentForm/>)
				const node = getByTestId('status_field-label')
				expect(String(node.innerHTML).endsWith('*')).toBeTruthy()
			})

			it('Organization Name', async () => {
				let { getByTestId } = render(<AppointmentForm/>)
				const node = getByTestId('organizationId_field-label')
				expect(String(node.innerHTML).endsWith('*')).toBeTruthy()
			})

			it('Community Name', async () => {
				let { getByTestId } = render(<AppointmentForm/>)
				const node = getByTestId('communityId_field-label')
				expect(String(node.innerHTML).endsWith('*')).toBeTruthy()
			})

			it('Location', async () => {
				let { getByTestId } = render(<AppointmentForm/>)
				const node = getByTestId('location_field-label')
				expect(String(node.innerHTML).endsWith('*')).toBeTruthy()
			})

			it('Appointment Type', async () => {
				let { getByTestId } = render(<AppointmentForm/>)
				const node = getByTestId('type_field-label')
				expect(String(node.innerHTML).endsWith('*')).toBeTruthy()
			})
		})
	})

	describe('Create Mode:', () => {
		it('"Status" field initialized correctly', async () => {
			const {
				store,
				getByTestId
			} = render(<AppointmentForm/>)

			const user = findWhere(User, { organizationId: 3 })

			store.dispatch({ type: 'LOGIN_SUCCESS', payload: user })

			await waitFor(() => {
				const node = getByTestId('status_selected-text')
				expect(node).toHaveTextContent('Planned')
			})
		})

		it('"Organization" field initialized of a first organization correctly', async () => {
			const {
				store,
				getByTestId
			} = render(<AppointmentForm/>)

			store.dispatch({ type: 'LOGIN_SUCCESS', payload: null })

			await waitFor(() => {
				const node = getByTestId('organizationId_selected-text')
				const firstOrg = Organization[0]
				expect(node).toHaveTextContent(firstOrg.label)
			})
		})

		it('"Organization" field initialized of the user organization correctly', async () => {
			const {
				store,
				getByTestId
			} = render(<AppointmentForm/>)

			const user = findWhere(User, { organizationId: 3 })

			store.dispatch({ type: 'LOGIN_SUCCESS', payload: user })

			await waitFor(() => {
				const node = getByTestId('organizationId_selected-text')
				const userOrg = findWhere(Organization, { id: user.organizationId })
				expect(node).toHaveTextContent(userOrg.label)
			})
		})

		it('"Organization" field initialized of specific organization correctly', async () => {
			const organizationId = 2989

			const {
				store,
				getByTestId
			} = render(<AppointmentForm organizationId={organizationId}/>)

			const user = findWhere(User, { organizationId: 3 })

			store.dispatch({ type: 'LOGIN_SUCCESS', payload: user })

			await waitFor(() => {
				const node = getByTestId('organizationId_selected-text')
				const organization = findWhere(Organization, { id: organizationId })
				expect(node).toHaveTextContent(organization.label)
			})
		})

		it('"Community" field initialized correctly', async () => {
			const {
				store,
				getByTestId
			} = render(<AppointmentForm/>)

			const user = findWhere(User, { organizationId: 3 })

			store.dispatch({ type: 'LOGIN_SUCCESS', payload: user })

			await waitFor(() => {
				const node = getByTestId('communityId_selected-text')
				expect(node).toHaveTextContent('Select')
			})
		})

		it('"Community" field initialized of the user community correctly',
			async () => {
			const {
				store,
				queryByTestId
			} = render(<AppointmentForm/>)

			const user = findWhere(User, { organizationId: 217 })

			store.dispatch({ type: 'LOGIN_SUCCESS', payload: user })

			await waitFor(() => {
				const node = queryByTestId('communityId_selected-text')
				const userCommunity = findWhere(Community, { id: user.communityId })
				expect(node).toHaveTextContent('Select')
			})
		})

		it('"Community" field initialized of the single community correctly and disabled',
			async () => {
			const {
				store,
				getByTestId
			} = render(<AppointmentForm/>)

			const user = findWhere(User, { organizationId: 3023 })
			user.communityId = 28
			user.communityName = 'Arbor Terrace Retirement Center'

			store.dispatch({ type: 'LOGIN_SUCCESS', payload: user })

			await waitFor(() => {
				let node = getByTestId('communityId_selected-text')
				const userCommunity = findWhere(Community, { organizationId: 3023 })
				expect(node).toHaveTextContent(userCommunity.name)

				node = getByTestId('communityId_field')
				expect(node).toHaveClass('SelectField_disabled')
			})
		})

		it('"Client" field is disabled until a community is selected', async () => {
			const {
				store,
				getByTestId
			} = render(<AppointmentForm/>)

			const user = findWhere(User, { organizationId: 3016 })

			store.dispatch({ type: 'LOGIN_SUCCESS', payload: user })

			await waitFor(() => {
				const node = getByTestId('clientId_field')
				expect(node).toHaveClass('SelectField_disabled')
			})
		})

		it('"Creator" field is populated of current user', async () => {
			const {
				store,
				getByTestId
			} = render(<AppointmentForm/>)

			const user = findWhere(User, { organizationId: 3016 })

			store.dispatch({ type: 'LOGIN_SUCCESS', payload: user })

			await waitFor(() => {
				let node = getByTestId('creator_field-input')
				expect(node).toHaveValue(user.fullName)
			})
		})

		it('"Appointment date" field is empty by default', async () => {
			const {
				getByTestId
			} = render(<AppointmentForm/>)

			await waitFor(() => {
				const node = getByTestId('date_field-input')
				expect(node).toHaveValue('')
			})
		})

		it('"Appointment date" field is populated correctly', async () => {
			const date = add(Date.now(), 1, 'day')

			const {
				store,
				getByTestId
			} = render(<AppointmentForm appointmentDate={date.getTime()}/>)

			const user = findWhere(User, { organizationId: 3016 })

			store.dispatch({ type: 'LOGIN_SUCCESS', payload: user })

			await waitFor(() => {
				const node = getByTestId('date_field-input')
				expect(node).toHaveValue(format(date, formats.americanMediumDate))
			})
		})

		it('"From" field is papulated correctly', async () => {
			const {
				store,
				getByTestId
			} = render(<AppointmentForm/>)

			const user = findWhere(User, { organizationId: 3 })

			store.dispatch({ type: 'LOGIN_SUCCESS', payload: user })

			const { from } = getFormattedTimeRange()

			await waitFor(() => {
				const node = getByTestId('from_field-input')
				expect(node).toHaveValue(from)
			})
		})

		it('"To" field is papulated correctly', async () => {
			const {
				store,
				getByTestId
			} = render(<AppointmentForm/>)

			const user = findWhere(User, { organizationId: 3 })

			store.dispatch({ type: 'LOGIN_SUCCESS', payload: user })

			const { to } = getFormattedTimeRange()

			await waitFor(() => {
				const node = getByTestId('to_field-input')
				expect(node).toHaveValue(to)
			})
		})

		it('"Client Reminder" field is papulated correctly', async () => {
			const {
				store,
				getByTestId
			} = render(<AppointmentForm/>)

			const user = findWhere(User, { organizationId: 3016 })

			store.dispatch({ type: 'LOGIN_SUCCESS', payload: user })

			await waitFor(() => {
				const node = getByTestId('reminders_selected-text')
				expect(node).toHaveTextContent('Never')
			})
		})

		it('"Client Reminder" field is disabled', async () => {
			const {
				store,
				getByTestId
			} = render(<AppointmentForm/>)

			const user = findWhere(User, { organizationId: 3016 })

			store.dispatch({ type: 'LOGIN_SUCCESS', payload: user })

			await waitFor(() => {
				const node = getByTestId('reminders_field')
				expect(node).toHaveClass('SelectField_disabled')
			})
		})

		it('"Notification Method" field is empty', async () => {
			const {
				store,
				getByTestId
			} = render(<AppointmentForm/>)

			const user = findWhere(User, { organizationId: 3016 })

			store.dispatch({ type: 'LOGIN_SUCCESS', payload: user })

			await waitFor(() => {
				const node = getByTestId('notificationMethods_selected-text')
				expect(node).toHaveTextContent('Select')
			})
		})

		it('"Notification Method" field is disabled', async () => {
			const {
				store,
				getByTestId
			} = render(<AppointmentForm/>)

			const user = findWhere(User, { organizationId: 3016 })

			store.dispatch({ type: 'LOGIN_SUCCESS', payload: user })

			await waitFor(() => {
				const node = getByTestId('notificationMethods_field')
				expect(node).toHaveClass('SelectField_disabled')
			})
		})

		it('"Email" field is empty', async () => {
			const {
				getByTestId
			} = render(<AppointmentForm/>)

			await waitFor(() => {
				const node = getByTestId('email_field-input')
				expect(node).toHaveValue('')
			})
		})

		it('"Email" field is disabled', async () => {
			const {
				getByTestId
			} = render(<AppointmentForm/>)

			await waitFor(() => {
				const node = getByTestId('email_field')
				expect(node).toHaveClass('TextField_disabled')
			})
		})

		it('"Cell Phone #" field is empty', async () => {
			const {
				getByTestId
			} = render(<AppointmentForm/>)

			await waitFor(() => {
				const node = getByTestId('phone_field-input')
				expect(node).toHaveValue('+1')
			})
		})

		it('"Cell Phone #" field is disabled', async () => {
			const {
				getByTestId
			} = render(<AppointmentForm/>)

			await waitFor(() => {
				const node = getByTestId('phone_field')
				expect(node).toHaveClass('PhoneField_disabled')
			})
		})
	})

	describe('Create from Client Dashboard', function () {
		it('"Organization" field initialized correctly', async () => {
			const clientId = 4054

			const {
				store,
				getByTestId
			} = render(<AppointmentForm clientId={clientId}/>)

			const user = findWhere(User, { organizationId: 3 })

			store.dispatch({ type: 'LOGIN_SUCCESS', payload: user })

			const client = findWhere(Client, { id: clientId })

			await waitFor(() => {
				const node = getByTestId('organizationId_selected-text')
				expect(node).toHaveTextContent(client.organization.trim())
			})
		})

		it('"Community" field initialized correctly', async () => {
			const clientId = 4054

			const {
				store,
				getByTestId
			} = render(<AppointmentForm clientId={clientId}/>)

			const user = findWhere(User, { organizationId: 3 })

			store.dispatch({ type: 'LOGIN_SUCCESS', payload: user })

			const client = findWhere(Client, { id: clientId })

			await waitFor(() => {
				const node = getByTestId('communityId_selected-text')
				expect(node).toHaveTextContent(client.community.trim())
			})
		})

		it('"Email" field initialized correctly', async () => {
			const clientId = 4054

			const {
				store,
				getByTestId
			} = render(<AppointmentForm clientId={clientId}/>)

			const user = findWhere(User, { organizationId: 3 })

			store.dispatch({ type: 'LOGIN_SUCCESS', payload: user })

			const client = findWhere(Client, { id: clientId })

			await waitFor(() => {
				const node = getByTestId('email_field-input')
				expect(node).toHaveValue(client.email)
			})
		})

		it('"Phone" field initialized correctly', async () => {
			const clientId = 4054

			const {
				store,
				getByTestId
			} = render(<AppointmentForm clientId={clientId}/>)

			const user = findWhere(User, { organizationId: 3 })

			store.dispatch({ type: 'LOGIN_SUCCESS', payload: user })

			const client = findWhere(Client, { id: clientId })

			await waitFor(() => {
				const node = getByTestId('phone_field-input')
				expect(node.value.replace(/\s/g, '')).toEqual(client.cellPhone)
			})
		})

		it('"Reminders" field initialized correctly', async () => {
			const clientId = 4054

			const {
				store,
				getByTestId
			} = render(<AppointmentForm clientId={clientId}/>)

			const user = findWhere(User, { organizationId: 3 })

			store.dispatch({ type: 'LOGIN_SUCCESS', payload: user })

			await waitFor(() => {
				const node = getByTestId('reminders_selected-text')
				expect(node).toHaveTextContent('2 hours before, 1 day before')
			})
		})

		it('"Notification Method" field initialized correctly. clientId = 4054', async () => {
			const clientId = 4054

			const {
				store,
				getByTestId
			} = render(<AppointmentForm clientId={clientId}/>)

			const user = findWhere(User, { organizationId: 3 })

			store.dispatch({ type: 'LOGIN_SUCCESS', payload: user })

			await waitFor(() => {
				const node = getByTestId('notificationMethods_selected-text')
				expect(node).toHaveTextContent('Email')
			})
		})

		it('"Notification Method" field initialized correctly. clientId = 4337', async () => {
			const clientId = 4337

			const {
				store,
				getByTestId
			} = render(<AppointmentForm clientId={clientId}/>)

			const user = findWhere(User, { organizationId: 3 })

			store.dispatch({ type: 'LOGIN_SUCCESS', payload: user })

			await waitFor(() => {
				const node = getByTestId('notificationMethods_selected-text')
				expect(node).toHaveTextContent('SMS')
			})
		})
	})

	afterAll(() => {
		server.close()
	})
})