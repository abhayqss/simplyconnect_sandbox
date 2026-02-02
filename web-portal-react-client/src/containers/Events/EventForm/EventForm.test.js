import React from 'react'

import {
	where,
	findWhere
} from 'underscore'

import { rest } from 'msw'
import { setupServer } from 'msw/node'
import { Provider } from 'react-redux'
import { createBrowserHistory } from 'history'
import { waitFor, render } from 'lib/test-utils'

import Response from 'lib/mock/server/Response'

import { format, formats } from 'lib/utils/DateUtils'

import {
	User,
	State,
	Community,
	SystemRole,
	Organization,
	ClientDetails,
	ContactDetails
} from 'lib/mock/db/DB'

import configureStore from 'redux/configureStore'

import EventForm from './EventForm'

const BASE_URL = 'https://dev.simplyconnect.me/web-portal-mock-backend'

const server = setupServer(
	rest.get(`${BASE_URL}/clients/:clientId`, (req, res, ctx) => {
		return res(ctx.json(Response.success(
			findWhere(ClientDetails, { id: +req.params.clientId })
		)))
	}),
	rest.get(`${BASE_URL}/contacts/:contactId`, (req, res, ctx) => {
		return res(ctx.json(Response.success(
			findWhere(ContactDetails, { id: +req.params.contactId })
		)))
	}),
	rest.get(`${BASE_URL}/authorized-directory/organizations`, (req, res, ctx) => {
		return res(ctx.json(Response.success(Organization)))
	}),
	rest.get(`${BASE_URL}/authorized-directory/communities`, (req, res, ctx) => {
		return res(ctx.json(Response.success(
			where(Community, { organizationId: +req.url.searchParams.get('organizationId') })
		)))
	}),
	rest.get(`${BASE_URL}/directory/states`, (req, res, ctx) => {
		return res(ctx.json(Response.success(State)))
	}),
	rest.get(`${BASE_URL}/authorized-directory/editable-system-roles`, (req, res, ctx) => {
		return res(ctx.json(Response.success(SystemRole)))
	})
)

function renderAndConfigure({ clientId, userOrganizationId = 3 }) {
	const {
		store, ...config
	} = render(
		<EventForm clientId={clientId}/>
	)

	const user = findWhere(User, { organizationId: userOrganizationId })
	store.dispatch({ type: 'LOGIN_SUCCESS', payload: user })

	const client = findWhere(ClientDetails, { id: clientId })
	store.dispatch({ type: 'LOAD_CLIENT_DETAILS_SUCCESS', payload: client })

	return { store, ...config }
}

describe('<EventForm>:', function () {
	beforeAll(() => {
		server.listen()
	})

	describe('All fields are visible:', function () {
		const clientId = 32041

		describe('Client Info:', function () {
			it('Community', async () => {
				let { getByTestId } = render(
					<EventForm clientId={clientId}/>
				)
				const node = getByTestId('community_field')
				expect(node).toBeInTheDocument()
				expect(node).toBeVisible()
			})

			it('Organization', async () => {
				let { getByTestId } = render(
					<EventForm clientId={clientId}/>
				)
				const node = getByTestId('organization_field')
				expect(node).toBeInTheDocument()
				expect(node).toBeVisible()
			})

			it('First Name', async () => {
				let { getByTestId } = render(
					<EventForm clientId={clientId}/>
				)
				const node = getByTestId('firstName_field')
				expect(node).toBeInTheDocument()
				expect(node).toBeVisible()
			})

			it('Last Name', async () => {
				let { getByTestId } = render(
					<EventForm clientId={clientId}/>
				)
				const node = getByTestId('lastName_field')
				expect(node).toBeInTheDocument()
				expect(node).toBeVisible()
			})

			it('Social Security Number', async () => {
				let { getByTestId } = render(
					<EventForm
						canEditRole
						clientId={clientId}
					/>
				)
				const node = getByTestId('ssn_field')
				expect(node).toBeInTheDocument()
				expect(node).toBeVisible()
			})
		})

		describe('Event Essentials:', function () {
			it('Person Submitting Event', async () => {
				let { getByTestId } = render(
					<EventForm clientId={clientId}/>
				)
				const node = getByTestId('essentials.author_field')
				expect(node).toBeInTheDocument()
				expect(node).toBeVisible()
			})

			it('Care Team Role', async () => {
				let { getByTestId } = render(
					<EventForm clientId={clientId}/>
				)
				const node = getByTestId('essentials.authorRole_field')
				expect(node).toBeInTheDocument()
				expect(node).toBeVisible()
			})

			it('Event Date and Time', async () => {
				let { getByTestId } = render(
					<EventForm clientId={clientId}/>
				)
				const node = getByTestId('essentials.date_field')
				expect(node).toBeInTheDocument()
				expect(node).toBeVisible()
			})

			it('Event Type', async () => {
				let { getByTestId } = render(
					<EventForm clientId={clientId}/>
				)
				const node = getByTestId('essentials.typeId_field')
				expect(node).toBeInTheDocument()
				expect(node).toBeVisible()
			})

			it('Emergency Department Visit', async () => {
				let { getByTestId } = render(
					<EventForm clientId={clientId}/>
				)
				const node = getByTestId('essentials.isEmergencyDepartmentVisit_field')
				expect(node).toBeInTheDocument()
				expect(node).toBeVisible()
			})

			it('Overnight In-patient', async () => {
				let { getByTestId } = render(
					<EventForm clientId={clientId}/>
				)
				const node = getByTestId('essentials.isOvernightInpatient_field')
				expect(node).toBeInTheDocument()
				expect(node).toBeVisible()
			})
		})

		describe('Event Description:', function () {
			it('Location', async () => {
				let { getByTestId } = render(
					<EventForm clientId={clientId}/>
				)
				const node = getByTestId('description.location_field')
				expect(node).toBeInTheDocument()
				expect(node).toBeVisible()
			})

			it('Situation', async () => {
				let { getByTestId } = render(
					<EventForm clientId={clientId}/>
				)
				const node = getByTestId('description.situation_field')
				expect(node).toBeInTheDocument()
				expect(node).toBeVisible()
			})

			it('Background', async () => {
				let { getByTestId } = render(
					<EventForm clientId={clientId}/>
				)
				const node = getByTestId('description.background_field')
				expect(node).toBeInTheDocument()
				expect(node).toBeVisible()
			})

			it('Assessment', async () => {
				let { getByTestId } = render(
					<EventForm clientId={clientId}/>
				)
				const node = getByTestId('description.assessment_field')
				expect(node).toBeInTheDocument()
				expect(node).toBeVisible()
			})

			it('Injury', async () => {
				let { getByTestId } = render(
					<EventForm clientId={clientId}/>
				)
				const node = getByTestId('description.hasInjury_field')
				expect(node).toBeInTheDocument()
				expect(node).toBeVisible()
			})

			it('Follow up expected', async () => {
				let { getByTestId } = render(
					<EventForm clientId={clientId}/>
				)
				const node = getByTestId('description.isFollowUpExpected_field')
				expect(node).toBeInTheDocument()
				expect(node).toBeVisible()
			})
		})

		describe('Treatment Details:', function () {
			it('Include details of treating physician', async () => {
				let { getByTestId } = render(
					<EventForm clientId={clientId}/>
				)
				const node = getByTestId('treatment.hasPhysician_field')
				expect(node).toBeInTheDocument()
				expect(node).toBeVisible()
			})

			it('Include details of treating hospital', async () => {
				let { getByTestId } = render(
					<EventForm clientId={clientId}/>
				)
				const node = getByTestId('description.isFollowUpExpected_field')
				expect(node).toBeInTheDocument()
				expect(node).toBeVisible()
			})
		})

		describe('Details of Responsible Manager:', function () {
			it('Include details of responsible manager', async () => {
				let { getByTestId } = render(
					<EventForm clientId={clientId}/>
				)
				const node = getByTestId('hasResponsibleManager_field')
				expect(node).toBeInTheDocument()
				expect(node).toBeVisible()
			})
		})

		describe('Details of Registered Nurse (RN):', function () {
			it('Include details of registered nurse', async () => {
				let { getByTestId } = render(
					<EventForm clientId={clientId}/>
				)
				const node = getByTestId('hasRegisteredNurse_field')
				expect(node).toBeInTheDocument()
				expect(node).toBeVisible()
			})
		})
	})

	describe('Fields marked as required:', function () {
		const clientId = 32041

		describe('Event Essentials:', function () {
			it('Care Team Role', async () => {
				let { getByTestId } = render(
					<EventForm clientId={clientId}/>
				)
				const node = getByTestId('essentials.authorRole_field-label')
				expect(String(node.innerHTML).endsWith('*')).toBeTruthy()
			})

			it('Event Date and Time', async () => {
				let { getByTestId } = render(
					<EventForm clientId={clientId}/>
				)
				const node = getByTestId('essentials.date_field-label')
				expect(String(node.innerHTML).endsWith('*')).toBeTruthy()
			})

			it('Event Type', async () => {
				let { getByTestId } = render(
					<EventForm clientId={clientId}/>
				)
				const node = getByTestId('essentials.typeId_field-label')
				expect(String(node.innerHTML).endsWith('*')).toBeTruthy()
			})
		})
	})

	describe('Fields disabled by default:', function () {
		const clientId = 32041

		describe('Client Info:', function () {
			it('Community', async () => {
				let { getByTestId } = render(
					<EventForm clientId={clientId}/>
				)
				const node = getByTestId('community_field')
				expect(node).toHaveClass('TextField_disabled')
			})

			it('Organization', async () => {
				let { getByTestId } = render(
					<EventForm clientId={clientId}/>
				)
				const node = getByTestId('organization_field')
				expect(node).toHaveClass('TextField_disabled')
			})

			it('First Name', async () => {
				let { getByTestId } = render(
					<EventForm clientId={clientId}/>
				)
				const node = getByTestId('firstName_field')
				expect(node).toHaveClass('TextField_disabled')
			})

			it('Last Name', async () => {
				let { getByTestId } = render(
					<EventForm clientId={clientId}/>
				)
				const node = getByTestId('lastName_field')
				expect(node).toHaveClass('TextField_disabled')
			})

			it('Social Security Number', async () => {
				let { getByTestId } = render(
					<EventForm
						canEditRole
						clientId={clientId}
					/>
				)
				const node = getByTestId('ssn_field')
				expect(node).toHaveClass('TextField_disabled')
			})
		})

		describe('Event Essentials:', function () {
			it('Person Submitting Event', async () => {
				let { getByTestId } = render(
					<EventForm clientId={clientId}/>
				)
				const node = getByTestId('essentials.author_field')
				expect(node).toHaveClass('TextField_disabled')
			})
		})
	})

	describe('Add new Event. Default Field values:', function () {
		const clientId = 32041
		const organizationId = 3
		const userOrganizationId = 3

		const client = findWhere(ClientDetails, { id: clientId })
		const user = findWhere(User, { organizationId: userOrganizationId })

		describe('Client Info:', function () {
			it('Community', async () => {
				let { getByTestId } = renderAndConfigure({ clientId })

				await waitFor(() => {
					const node = getByTestId('community_field-input')
					expect(node).toHaveValue(client.community)
				})
			})

			it('Organization', async () => {
				let { getByTestId } = renderAndConfigure({ clientId })

				await waitFor(() => {
					const node = getByTestId('organization_field-input')
					expect(node).toHaveValue(client.organization)
				})
			})

			it('First Name', async () => {
				let { getByTestId } = renderAndConfigure({ clientId })

				await waitFor(() => {
					const node = getByTestId('firstName_field-input')
					expect(node).toHaveValue(client.firstName)
				})
			})

			it('Last Name', async () => {
				let { getByTestId } = renderAndConfigure({ clientId })

				await waitFor(() => {
					const node = getByTestId('lastName_field-input')
					expect(node).toHaveValue(client.lastName)
				})
			})

			it('Social Security Number', async () => {
				let { getByTestId } = renderAndConfigure({ clientId })

				await waitFor(() => {
					const node = getByTestId('ssn_field-input')
					expect(node).toHaveValue(`###-##-${client.ssn.substr(-4, 4)}`)
				})
			})
		})

		describe('Event Essentials:', function () {
			it('Person Submitting Event', async () => {
				let { getByTestId } = renderAndConfigure({ clientId })

				await waitFor(() => {
					const node = getByTestId('essentials.author_field-input')
					expect(node).toHaveValue(user.fullName)
				})
			})

			it('Care Team Role', async () => {
				let { getByTestId } = renderAndConfigure({ clientId })

				await waitFor(() => {
					const node = getByTestId('essentials.authorRole_selected-text')
					expect(node).toHaveTextContent('Select')
				})
			})

			it('Event Date and Time', async () => {
				let { getByTestId } = renderAndConfigure({ clientId })

				await waitFor(() => {
					const node = getByTestId('essentials.date_field-input')
					expect(node).toHaveValue(format(Date.now(), formats.longDateMediumTime12))
				})
			})

			it('Event Type', async () => {
				let { getByTestId } = renderAndConfigure({ clientId })

				await waitFor(() => {
					const node = getByTestId('essentials.typeId_selected-text')
					expect(node).toHaveTextContent('Select')
				})
			})

			it('Emergency Department Visit', async () => {
				let { queryByTestId } = renderAndConfigure({ clientId })

				await waitFor(() => {
					const node = queryByTestId('essentials.isEmergencyDepartmentVisit_field-check-mark')
					expect(node).toBeNull()
				})
			})

			it('Overnight In-patient', async () => {
				let { queryByTestId } = renderAndConfigure({ clientId })

				await waitFor(() => {
					const node = queryByTestId('essentials.isOvernightInpatient_field-check-mark')
					expect(node).toBeNull()
				})
			})
		})

		describe('Event Description:', function () {
			it('Location', async () => {
				let { getByTestId } = renderAndConfigure({ clientId })

				await waitFor(() => {
					const node = getByTestId('description.location_field-input')
					expect(node).toHaveValue('')
				})
			})

			it('Situation', async () => {
				let { getByTestId } = renderAndConfigure({ clientId })

				await waitFor(() => {
					const node = getByTestId('description.situation_field-input')
					expect(node).toHaveValue('')
				})
			})

			it('Background', async () => {
				let { getByTestId } = renderAndConfigure({ clientId })

				await waitFor(() => {
					const node = getByTestId('description.background_field-input')
					expect(node).toHaveValue('')
				})
			})

			it('Assessment', async () => {
				let { getByTestId } = renderAndConfigure({ clientId })

				await waitFor(() => {
					const node = getByTestId('description.assessment_field-input')
					expect(node).toHaveValue('')
				})
			})

			it('Injury', async () => {
				let { queryByTestId } = renderAndConfigure({ clientId })

				await waitFor(() => {
					const node = queryByTestId('description.hasInjury_field-check-mark')
					expect(node).toBeNull()
				})
			})

			it('Follow up expected', async () => {
				let { queryByTestId } = renderAndConfigure({ clientId })

				await waitFor(() => {
					const node = queryByTestId('description.isFollowUpExpected_field-check-mark')
					expect(node).toBeNull()
				})
			})
		})

		describe('Treatment Details:', function () {
			it('Include details of treating physician', async () => {
				let { queryByTestId } = renderAndConfigure({ clientId })

				await waitFor(() => {
					const node = queryByTestId('treatment.hasPhysician_field-check-mark')
					expect(node).toBeNull()
				})
			})

			it('Include details of treating hospital', async () => {
				let { queryByTestId } = renderAndConfigure({ clientId })

				await waitFor(() => {
					const node = queryByTestId('description.isFollowUpExpected_field-check-mark')
					expect(node).toBeNull()
				})
			})
		})

		describe('Details of Responsible Manager:', function () {
			it('Include details of responsible manager', async () => {
				let { queryByTestId } = renderAndConfigure({ clientId })

				await waitFor(() => {
					const node = queryByTestId('hasResponsibleManager_field-check-mark')
					expect(node).toBeNull()
				})
			})
		})

		describe('Details of Registered Nurse (RN):', function () {
			it('Include details of registered nurse', async () => {
				let { queryByTestId } = renderAndConfigure({ clientId })

				await waitFor(() => {
					const node = queryByTestId('hasRegisteredNurse_field-check-mark')
					expect(node).toBeNull()
				})
			})
		})
	})

	afterAll(() => {
		server.close()
	})
})