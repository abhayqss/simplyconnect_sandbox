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
	Contact,
	NoteType,
	Community,
	SystemRole,
	NoteDetails,
	Organization,
	ClientDetails,
	ContactDetails, ClientProgramNoteType
} from 'lib/mock/db/DB'

import configureStore from 'redux/configureStore'

import NoteForm from './NoteForm'
import moment from 'moment'

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
	}),
	rest.get(`${BASE_URL}/authorized-directory/contacts`, (req, res, ctx) => {
		return res(ctx.json(Response.success(
			where(Contact, { organizationId: +req.url.searchParams.get('organizationId') })
		)))
	}),
	rest.get(`${BASE_URL}/directory/note-types`, (req, res, ctx) => {
		return res(ctx.json(Response.success(NoteType)))
	}),
	rest.get(`${BASE_URL}/directory/client-program-note-types`, (req, res, ctx) => {
		return res(ctx.json(Response.success(ClientProgramNoteType)))
	}),
	rest.get(`${BASE_URL}/clients/:clientId/service-plans/controlled`, (req, res, ctx) => {
		return res(ctx.json(Response.success({
			"id": 1745,
			"dateCreated": new Date(2020, 1, 13, 11, 40).getTime()
		})))
	}),
	rest.get(`${BASE_URL}/clients/:clientId/service-plans/controlled/resource-names`, (req, res, ctx) => {
		return res(ctx.json(Response.success([
			{
				"resourceName": "ere",
				"providerName": "ege"
			}
		])))
	}),
	rest.get(`${BASE_URL}/clients/:clientId/notes/admit-dates`, (req, res, ctx) => {
		return res(ctx.json(Response.success([
			{
				"id": 0,
				"date": 1648631037861,
				"takenNoteTypeIds": [17]
			}
		])))
	}),
	rest.get(`${BASE_URL}/clients/:clientId/notes/contacts`, (req, res, ctx) => {
		return res(ctx.json(Response.success(Contact)))
	})
)

function renderAndConfigure({ noteId, clientId, userOrganizationId = 3 }) {
	const client = findWhere(ClientDetails, { id: clientId })

	const {
		store, ...config
	} = render(
		<NoteForm
			noteId={noteId}
			clientId={clientId}
			clientName={client.fullName}
		/>
	)

	const user = findWhere(User, { organizationId: userOrganizationId })
	store.dispatch({ type: 'LOGIN_SUCCESS', payload: user })

	store.dispatch({ type: 'LOAD_CLIENT_DETAILS_SUCCESS', payload: client })

	return { store, ...config }
}

describe('<NoteForm>:', function () {
	beforeAll(() => {
		server.listen()
	})

	describe('All fields are visible:', function () {
		const clientId = 32041

		it('Person Submitting Note', async () => {
			let { getByTestId } = render(
				<NoteForm clientId={clientId}/>
			)
			const node = getByTestId('author_field')
			expect(node).toBeInTheDocument()
			expect(node).toBeVisible()
		})

		it('Client', async () => {
			let { getByTestId } = render(
				<NoteForm clientId={clientId}/>
			)
			const node = getByTestId('clientName_field')
			expect(node).toBeInTheDocument()
			expect(node).toBeVisible()
		})

		it('Note Date and Time', async () => {
			let { getByTestId } = render(
				<NoteForm clientId={clientId}/>
			)
			const node = getByTestId('noteDate_field')
			expect(node).toBeInTheDocument()
			expect(node).toBeVisible()
		})

		it('Note Type', async () => {
			let { getByTestId } = render(
				<NoteForm clientId={clientId}/>
			)
			const node = getByTestId('subTypeId_field')
			expect(node).toBeInTheDocument()
			expect(node).toBeVisible()
		})

		it('Admit / Intake Date', async () => {
			let { getByTestId } = render(
				<NoteForm clientId={clientId}/>
			)
			const node = getByTestId('admitDateId_field')
			expect(node).toBeInTheDocument()
			expect(node).toBeVisible()
		})

		describe('Program note type:', function () {
			const userOrganizationId = 3

			it('Program sub type', async function () {
				const {
					store, getByTestId
				} = render(
					<NoteForm clientId={clientId}/>
				)

				const user = findWhere(User, { organizationId: userOrganizationId })
				store.dispatch({ type: 'LOGIN_SUCCESS', payload: user })

				const client = findWhere(ClientDetails, { id: clientId })
				store.dispatch({ type: 'LOAD_CLIENT_DETAILS_SUCCESS', payload: client })

				store.dispatch({
					type: 'CHANGE_NOTE_FORM_FIELD',
					payload: { name: 'subTypeId', value: 10030 }
				})

				await waitFor(() => {
					const node = getByTestId('clientProgram.typeId_field')
					expect(node).toBeInTheDocument()
					expect(node).toBeVisible()
				})
			})

			it('Service Provider for Program', async function () {
				const {
					store, getByTestId
				} = render(
					<NoteForm clientId={clientId}/>
				)

				const user = findWhere(User, { organizationId: userOrganizationId })
				store.dispatch({ type: 'LOGIN_SUCCESS', payload: user })

				const client = findWhere(ClientDetails, { id: clientId })
				store.dispatch({ type: 'LOAD_CLIENT_DETAILS_SUCCESS', payload: client })

				store.dispatch({
					type: 'CHANGE_NOTE_FORM_FIELD',
					payload: { name: 'subTypeId', value: 10030 }
				})

				await waitFor(() => {
					const node = getByTestId('clientProgram.serviceProvider_field')
					expect(node).toBeInTheDocument()
					expect(node).toBeVisible()
				})
			})

			it('Start Date of Program', async function () {
				const {
					store, getByTestId
				} = render(
					<NoteForm clientId={clientId}/>
				)

				const user = findWhere(User, { organizationId: userOrganizationId })
				store.dispatch({ type: 'LOGIN_SUCCESS', payload: user })

				const client = findWhere(ClientDetails, { id: clientId })
				store.dispatch({ type: 'LOAD_CLIENT_DETAILS_SUCCESS', payload: client })

				store.dispatch({
					type: 'CHANGE_NOTE_FORM_FIELD',
					payload: { name: 'subTypeId', value: 10030 }
				})

				await waitFor(() => {
					const node = getByTestId('clientProgram.startDate_field')
					expect(node).toBeInTheDocument()
					expect(node).toBeVisible()
				})
			})

			it('End Date of Program', async function () {
				const {
					store, getByTestId
				} = render(
					<NoteForm clientId={clientId}/>
				)

				const user = findWhere(User, { organizationId: userOrganizationId })
				store.dispatch({ type: 'LOGIN_SUCCESS', payload: user })

				const client = findWhere(ClientDetails, { id: clientId })
				store.dispatch({ type: 'LOAD_CLIENT_DETAILS_SUCCESS', payload: client })

				store.dispatch({
					type: 'CHANGE_NOTE_FORM_FIELD',
					payload: { name: 'subTypeId', value: 10030 }
				})

				await waitFor(() => {
					const node = getByTestId('clientProgram.endDate_field')
					expect(node).toBeInTheDocument()
					expect(node).toBeVisible()
				})
			})
		})

		describe('Service Status Check note type', function () {
			const userOrganizationId = 3

			it('Service Plan', async function () {
				const {
					store, getByTestId
				} = render(
					<NoteForm clientId={clientId}/>
				)

				const user = findWhere(User, { organizationId: userOrganizationId })
				store.dispatch({ type: 'LOGIN_SUCCESS', payload: user })

				const client = findWhere(ClientDetails, { id: clientId })
				store.dispatch({ type: 'LOAD_CLIENT_DETAILS_SUCCESS', payload: client })

				store.dispatch({
					type: 'CHANGE_NOTE_FORM_FIELD',
					payload: { name: 'subTypeId', value: 10029 }
				})

				await waitFor(() => {
					const node = getByTestId('serviceStatusCheck.servicePlanCreatedDate_field')
					expect(node).toBeInTheDocument()
					expect(node).toBeVisible()
				})
			})

			it('Resource Name', async function () {
				const {
					store, getByTestId
				} = render(
					<NoteForm clientId={clientId}/>
				)

				const user = findWhere(User, { organizationId: userOrganizationId })
				store.dispatch({ type: 'LOGIN_SUCCESS', payload: user })

				const client = findWhere(ClientDetails, { id: clientId })
				store.dispatch({ type: 'LOAD_CLIENT_DETAILS_SUCCESS', payload: client })

				store.dispatch({
					type: 'CHANGE_NOTE_FORM_FIELD',
					payload: { name: 'subTypeId', value: 10029 }
				})

				await waitFor(() => {
					const node = getByTestId('serviceStatusCheck.resourceName_field')
					expect(node).toBeInTheDocument()
					expect(node).toBeVisible()
				})
			})

			it('Person Who Did the Audit / Check', async function () {
				const {
					store, getByTestId
				} = render(
					<NoteForm clientId={clientId}/>
				)

				const user = findWhere(User, { organizationId: userOrganizationId })
				store.dispatch({ type: 'LOGIN_SUCCESS', payload: user })

				const client = findWhere(ClientDetails, { id: clientId })
				store.dispatch({ type: 'LOAD_CLIENT_DETAILS_SUCCESS', payload: client })

				store.dispatch({
					type: 'CHANGE_NOTE_FORM_FIELD',
					payload: { name: 'subTypeId', value: 10029 }
				})

				await waitFor(() => {
					const node = getByTestId('serviceStatusCheck.auditPerson_field')
					expect(node).toBeInTheDocument()
					expect(node).toBeVisible()
				})
			})

			it('Date of Check', async function () {
				const {
					store, getByTestId
				} = render(
					<NoteForm clientId={clientId}/>
				)

				const user = findWhere(User, { organizationId: userOrganizationId })
				store.dispatch({ type: 'LOGIN_SUCCESS', payload: user })

				const client = findWhere(ClientDetails, { id: clientId })
				store.dispatch({ type: 'LOAD_CLIENT_DETAILS_SUCCESS', payload: client })

				store.dispatch({
					type: 'CHANGE_NOTE_FORM_FIELD',
					payload: { name: 'subTypeId', value: 10029 }
				})

				await waitFor(() => {
					const node = getByTestId('serviceStatusCheck.checkDate_field')
					expect(node).toBeInTheDocument()
					expect(node).toBeVisible()
				})
			})

			it('Next Date of Check', async function () {
				const {
					store, getByTestId
				} = render(
					<NoteForm clientId={clientId}/>
				)

				const user = findWhere(User, { organizationId: userOrganizationId })
				store.dispatch({ type: 'LOGIN_SUCCESS', payload: user })

				const client = findWhere(ClientDetails, { id: clientId })
				store.dispatch({ type: 'LOAD_CLIENT_DETAILS_SUCCESS', payload: client })

				store.dispatch({
					type: 'CHANGE_NOTE_FORM_FIELD',
					payload: { name: 'subTypeId', value: 10029 }
				})

				await waitFor(() => {
					const node = getByTestId('serviceStatusCheck.nextCheckDate_field')
					expect(node).toBeInTheDocument()
					expect(node).toBeVisible()
				})
			})

			it('Is the service being provided?', async function () {
				const {
					store, getByTestId
				} = render(
					<NoteForm clientId={clientId}/>
				)

				const user = findWhere(User, { organizationId: userOrganizationId })
				store.dispatch({ type: 'LOGIN_SUCCESS', payload: user })

				const client = findWhere(ClientDetails, { id: clientId })
				store.dispatch({ type: 'LOAD_CLIENT_DETAILS_SUCCESS', payload: client })

				store.dispatch({
					type: 'CHANGE_NOTE_FORM_FIELD',
					payload: { name: 'subTypeId', value: 10029 }
				})

				await waitFor(() => {
					const node = getByTestId('serviceStatusCheck.serviceProvided_field')
					expect(node).toBeInTheDocument()
					expect(node).toBeVisible()
				})
			})
		})

		it('Person Completing the Encounter', async () => {
			let { getByTestId } = render(
				<NoteForm clientId={clientId}/>
			)
			const node = getByTestId('clinicianId_field')
			expect(node).toBeInTheDocument()
			expect(node).toBeVisible()
		})

		it('Encounter Date From', async () => {
			let { getByTestId } = render(
				<NoteForm clientId={clientId}/>
			)
			const node = getByTestId('fromDate_field')
			expect(node).toBeInTheDocument()
			expect(node).toBeVisible()
		})

		it('Time From', async () => {
			let { getByTestId } = render(
				<NoteForm clientId={clientId}/>
			)
			const node = getByTestId('fromTime_field')
			expect(node).toBeInTheDocument()
			expect(node).toBeVisible()
		})

		it('Encounter Date To', async () => {
			let { getByTestId } = render(
				<NoteForm clientId={clientId}/>
			)
			const node = getByTestId('toDate_field')
			expect(node).toBeInTheDocument()
			expect(node).toBeVisible()
		})

		it('Time To', async () => {
			let { getByTestId } = render(
				<NoteForm clientId={clientId}/>
			)
			const node = getByTestId('toTime_field')
			expect(node).toBeInTheDocument()
			expect(node).toBeVisible()
		})

		it('Total time spent', async () => {
			let { getByTestId } = render(
				<NoteForm clientId={clientId}/>
			)
			const node = getByTestId('totalTime_field')
			expect(node).toBeInTheDocument()
			expect(node).toBeVisible()
		})

		it('Range', async () => {
			let { getByTestId } = render(
				<NoteForm clientId={clientId}/>
			)
			const node = getByTestId('range_field')
			expect(node).toBeInTheDocument()
			expect(node).toBeVisible()
		})

		it('Units', async () => {
			let { getByTestId } = render(
				<NoteForm clientId={clientId}/>
			)
			const node = getByTestId('units_field')
			expect(node).toBeInTheDocument()
			expect(node).toBeVisible()
		})

		it('Subjective', async () => {
			let { getByTestId } = render(
				<NoteForm clientId={clientId}/>
			)
			const node = getByTestId('subjective_field')
			expect(node).toBeInTheDocument()
			expect(node).toBeVisible()
		})

		it('Objective', async () => {
			let { getByTestId } = render(
				<NoteForm clientId={clientId}/>
			)
			const node = getByTestId('objective_field')
			expect(node).toBeInTheDocument()
			expect(node).toBeVisible()
		})

		it('Assessment', async () => {
			let { getByTestId } = render(
				<NoteForm clientId={clientId}/>
			)
			const node = getByTestId('assessment_field')
			expect(node).toBeInTheDocument()
			expect(node).toBeVisible()
		})

		it('Plan', async () => {
			let { getByTestId } = render(
				<NoteForm clientId={clientId}/>
			)
			const node = getByTestId('plan_field')
			expect(node).toBeInTheDocument()
			expect(node).toBeVisible()
		})
	})

	describe('Fields marked as required:', function () {
		const clientId = 32041

		it('Person Submitting Note', async () => {
			let { getByTestId } = render(
				<NoteForm clientId={clientId}/>
			)
			const node = getByTestId('author_field-label')
			expect(String(node.innerHTML).endsWith('*')).toBeTruthy()
		})

		it('Client', async () => {
			let { getByTestId } = render(
				<NoteForm clientId={clientId}/>
			)
			const node = getByTestId('clientName_field-label')
			expect(String(node.innerHTML).endsWith('*')).toBeTruthy()
		})

		it('Note Date and Time', async () => {
			let { getByTestId } = render(
				<NoteForm clientId={clientId}/>
			)
			const node = getByTestId('noteDate_field-label')
			expect(String(node.innerHTML).endsWith('*')).toBeTruthy()
		})

		it('Note Type', async () => {
			let { getByTestId } = render(
				<NoteForm clientId={clientId}/>
			)
			const node = getByTestId('subTypeId_field-label')
			expect(String(node.innerHTML).endsWith('*')).toBeTruthy()
		})

		it('Admit / Intake Date', async () => {
			let { getByTestId } = render(
				<NoteForm clientId={clientId}/>
			)
			const node = getByTestId('admitDateId_field-label')
			expect(String(node.innerHTML).endsWith('*')).toBeTruthy()
		})

		describe('Program note type:', function () {
			const userOrganizationId = 3

			it('Program sub type', async function () {
				const {
					store, getByTestId
				} = render(
					<NoteForm clientId={clientId}/>
				)

				const user = findWhere(User, { organizationId: userOrganizationId })
				store.dispatch({ type: 'LOGIN_SUCCESS', payload: user })

				const client = findWhere(ClientDetails, { id: clientId })
				store.dispatch({ type: 'LOAD_CLIENT_DETAILS_SUCCESS', payload: client })

				store.dispatch({
					type: 'CHANGE_NOTE_FORM_FIELD',
					payload: { name: 'subTypeId', value: 10030 }
				})

				await waitFor(() => {
					const node = getByTestId('clientProgram.typeId_field-label')
					expect(String(node.innerHTML).endsWith('*')).toBeTruthy()
				})
			})

			it('Service Provider for Program', async function () {
				const {
					store, getByTestId
				} = render(
					<NoteForm clientId={clientId}/>
				)

				const user = findWhere(User, { organizationId: userOrganizationId })
				store.dispatch({ type: 'LOGIN_SUCCESS', payload: user })

				const client = findWhere(ClientDetails, { id: clientId })
				store.dispatch({ type: 'LOAD_CLIENT_DETAILS_SUCCESS', payload: client })

				store.dispatch({
					type: 'CHANGE_NOTE_FORM_FIELD',
					payload: { name: 'subTypeId', value: 10030 }
				})

				await waitFor(() => {
					const node = getByTestId('clientProgram.serviceProvider_field-label')
					expect(String(node.innerHTML).endsWith('*')).toBeTruthy()
				})
			})

			it('Start Date of Program', async function () {
				const {
					store, getByTestId
				} = render(
					<NoteForm clientId={clientId}/>
				)

				const user = findWhere(User, { organizationId: userOrganizationId })
				store.dispatch({ type: 'LOGIN_SUCCESS', payload: user })

				const client = findWhere(ClientDetails, { id: clientId })
				store.dispatch({ type: 'LOAD_CLIENT_DETAILS_SUCCESS', payload: client })

				store.dispatch({
					type: 'CHANGE_NOTE_FORM_FIELD',
					payload: { name: 'subTypeId', value: 10030 }
				})

				await waitFor(() => {
					const node = getByTestId('clientProgram.startDate_field-label')
					expect(String(node.innerHTML).endsWith('*')).toBeTruthy()
				})
			})

			it('End Date of Program', async function () {
				const {
					store, getByTestId
				} = render(
					<NoteForm clientId={clientId}/>
				)

				const user = findWhere(User, { organizationId: userOrganizationId })
				store.dispatch({ type: 'LOGIN_SUCCESS', payload: user })

				const client = findWhere(ClientDetails, { id: clientId })
				store.dispatch({ type: 'LOAD_CLIENT_DETAILS_SUCCESS', payload: client })

				store.dispatch({
					type: 'CHANGE_NOTE_FORM_FIELD',
					payload: { name: 'subTypeId', value: 10030 }
				})

				await waitFor(() => {
					const node = getByTestId('clientProgram.endDate_field-label')
					expect(String(node.innerHTML).endsWith('*')).toBeTruthy()
				})
			})
		})

		describe('Service Status Check note type', function () {
			const userOrganizationId = 3

			it('Service Plan', async function () {
				const {
					store, getByTestId
				} = render(
					<NoteForm clientId={clientId}/>
				)

				const user = findWhere(User, { organizationId: userOrganizationId })
				store.dispatch({ type: 'LOGIN_SUCCESS', payload: user })

				const client = findWhere(ClientDetails, { id: clientId })
				store.dispatch({ type: 'LOAD_CLIENT_DETAILS_SUCCESS', payload: client })

				store.dispatch({
					type: 'CHANGE_NOTE_FORM_FIELD',
					payload: { name: 'subTypeId', value: 10029 }
				})

				await waitFor(() => {
					const node = getByTestId('serviceStatusCheck.servicePlanCreatedDate_field-label')
					expect(String(node.innerHTML).endsWith('*')).toBeTruthy()
				})
			})

			it('Resource Name', async function () {
				const {
					store, getByTestId
				} = render(
					<NoteForm clientId={clientId}/>
				)

				const user = findWhere(User, { organizationId: userOrganizationId })
				store.dispatch({ type: 'LOGIN_SUCCESS', payload: user })

				const client = findWhere(ClientDetails, { id: clientId })
				store.dispatch({ type: 'LOAD_CLIENT_DETAILS_SUCCESS', payload: client })

				store.dispatch({
					type: 'CHANGE_NOTE_FORM_FIELD',
					payload: { name: 'subTypeId', value: 10029 }
				})

				await waitFor(() => {
					const node = getByTestId('serviceStatusCheck.resourceName_field-label')
					expect(String(node.innerHTML).endsWith('*')).toBeTruthy()
				})
			})

			it('Person Who Did the Audit / Check', async function () {
				const {
					store, getByTestId
				} = render(
					<NoteForm clientId={clientId}/>
				)

				const user = findWhere(User, { organizationId: userOrganizationId })
				store.dispatch({ type: 'LOGIN_SUCCESS', payload: user })

				const client = findWhere(ClientDetails, { id: clientId })
				store.dispatch({ type: 'LOAD_CLIENT_DETAILS_SUCCESS', payload: client })

				store.dispatch({
					type: 'CHANGE_NOTE_FORM_FIELD',
					payload: { name: 'subTypeId', value: 10029 }
				})

				await waitFor(() => {
					const node = getByTestId('serviceStatusCheck.auditPerson_field-label')
					expect(String(node.innerHTML).endsWith('*')).toBeTruthy()
				})
			})

			it('Date of Check', async function () {
				const {
					store, getByTestId
				} = render(
					<NoteForm clientId={clientId}/>
				)

				const user = findWhere(User, { organizationId: userOrganizationId })
				store.dispatch({ type: 'LOGIN_SUCCESS', payload: user })

				const client = findWhere(ClientDetails, { id: clientId })
				store.dispatch({ type: 'LOAD_CLIENT_DETAILS_SUCCESS', payload: client })

				store.dispatch({
					type: 'CHANGE_NOTE_FORM_FIELD',
					payload: { name: 'subTypeId', value: 10029 }
				})

				await waitFor(() => {
					const node = getByTestId('serviceStatusCheck.checkDate_field-label')
					expect(String(node.innerHTML).endsWith('*')).toBeTruthy()
				})
			})

			it('Is the service being provided?', async function () {
				const {
					store, getByTestId
				} = render(
					<NoteForm clientId={clientId}/>
				)

				const user = findWhere(User, { organizationId: userOrganizationId })
				store.dispatch({ type: 'LOGIN_SUCCESS', payload: user })

				const client = findWhere(ClientDetails, { id: clientId })
				store.dispatch({ type: 'LOAD_CLIENT_DETAILS_SUCCESS', payload: client })

				store.dispatch({
					type: 'CHANGE_NOTE_FORM_FIELD',
					payload: { name: 'subTypeId', value: 10029 }
				})

				await waitFor(() => {
					const node = getByTestId('serviceStatusCheck.serviceProvided_field-label')
					expect(String(node.innerHTML).endsWith('*')).toBeTruthy()
				})
			})
		})

		describe('Face to Face encounter note type:', function () {
			const userOrganizationId = 3

			it('Encounter type', async () => {
				const {
					store, getByTestId
				} = renderAndConfigure({ clientId, userOrganizationId })

				store.dispatch({
					type: 'CHANGE_NOTE_FORM_FIELD',
					payload: { name: 'subTypeId', value: 19 }
				})

				await waitFor(() => {
					const node = getByTestId('typeId_field-label')
					expect(String(node.innerHTML).endsWith('*')).toBeTruthy()
				})
			})

			it('Person Completing the Encounter', async () => {
				const {
					store, getByTestId
				} = renderAndConfigure({ clientId, userOrganizationId })

				store.dispatch({
					type: 'CHANGE_NOTE_FORM_FIELD',
					payload: { name: 'subTypeId', value: 19 }
				})

				await waitFor(() => {
					const node = getByTestId('clinicianId_field-label')
					expect(String(node.innerHTML).endsWith('*')).toBeTruthy()
				})
			})

			it('Encounter Date From', async () => {
				const {
					store, getByTestId
				} = renderAndConfigure({ clientId, userOrganizationId })

				store.dispatch({
					type: 'CHANGE_NOTE_FORM_FIELD',
					payload: { name: 'subTypeId', value: 19 }
				})

				await waitFor(() => {
					const node = getByTestId('fromDate_field-label')
					expect(String(node.innerHTML).endsWith('*')).toBeTruthy()
				})
			})

			it('Time From', async () => {
				const {
					store, getByTestId
				} = renderAndConfigure({ clientId, userOrganizationId })

				store.dispatch({
					type: 'CHANGE_NOTE_FORM_FIELD',
					payload: { name: 'subTypeId', value: 19 }
				})

				await waitFor(() => {
					const node = getByTestId('fromTime_field-label')
					expect(String(node.innerHTML).endsWith('*')).toBeTruthy()
				})
			})

			it('Encounter Date To', async () => {
				const {
					store, getByTestId
				} = renderAndConfigure({ clientId, userOrganizationId })

				store.dispatch({
					type: 'CHANGE_NOTE_FORM_FIELD',
					payload: { name: 'subTypeId', value: 19 }
				})

				await waitFor(() => {
					const node = getByTestId('toDate_field-label')
					expect(String(node.innerHTML).endsWith('*')).toBeTruthy()
				})
			})

			it('Time To', async () => {
				const {
					store, getByTestId
				} = renderAndConfigure({ clientId, userOrganizationId })

				store.dispatch({
					type: 'CHANGE_NOTE_FORM_FIELD',
					payload: { name: 'subTypeId', value: 19 }
				})

				await waitFor(() => {
					const node = getByTestId('toTime_field-label')
					expect(String(node.innerHTML).endsWith('*')).toBeTruthy()
				})
			})

			it('Total time spent', async () => {
				const {
					store, getByTestId
				} = renderAndConfigure({ clientId, userOrganizationId })

				store.dispatch({
					type: 'CHANGE_NOTE_FORM_FIELD',
					payload: { name: 'subTypeId', value: 19 }
				})

				await waitFor(() => {
					const node = getByTestId('totalTime_field-label')
					expect(String(node.innerHTML).endsWith('*')).toBeTruthy()
				})
			})

			it('Range', async () => {
				const {
					store, getByTestId
				} = renderAndConfigure({ clientId, userOrganizationId })

				store.dispatch({
					type: 'CHANGE_NOTE_FORM_FIELD',
					payload: { name: 'subTypeId', value: 19 }
				})

				await waitFor(() => {
					const node = getByTestId('range_field-label')
					expect(String(node.innerHTML).endsWith('*')).toBeTruthy()
				})
			})

			it('Units', async () => {
				const {
					store, getByTestId
				} = renderAndConfigure({ clientId, userOrganizationId })

				store.dispatch({
					type: 'CHANGE_NOTE_FORM_FIELD',
					payload: { name: 'subTypeId', value: 19 }
				})

				await waitFor(() => {
					const node = getByTestId('units_field-label')
					expect(String(node.innerHTML).endsWith('*')).toBeTruthy()
				})
			})
		})

		describe('Non Face to Face encounter note type:', function () {
			const userOrganizationId = 3

			it('Encounter type', async () => {
				const {
					store, getByTestId
				} = renderAndConfigure({ clientId, userOrganizationId })

				store.dispatch({
					type: 'CHANGE_NOTE_FORM_FIELD',
					payload: { name: 'subTypeId', value: 20 }
				})

				await waitFor(() => {
					const node = getByTestId('typeId_field-label')
					expect(String(node.innerHTML).endsWith('*')).toBeTruthy()
				})
			})

			it('Person Completing the Encounter', async () => {
				const {
					store, getByTestId
				} = renderAndConfigure({ clientId, userOrganizationId })

				store.dispatch({
					type: 'CHANGE_NOTE_FORM_FIELD',
					payload: { name: 'subTypeId', value: 20 }
				})

				await waitFor(() => {
					const node = getByTestId('clinicianId_field-label')
					expect(String(node.innerHTML).endsWith('*')).toBeTruthy()
				})
			})

			it('Encounter Date From', async () => {
				const {
					store, getByTestId
				} = renderAndConfigure({ clientId, userOrganizationId })

				store.dispatch({
					type: 'CHANGE_NOTE_FORM_FIELD',
					payload: { name: 'subTypeId', value: 20 }
				})

				await waitFor(() => {
					const node = getByTestId('fromDate_field-label')
					expect(String(node.innerHTML).endsWith('*')).toBeTruthy()
				})
			})

			it('Time From', async () => {
				const {
					store, getByTestId
				} = renderAndConfigure({ clientId, userOrganizationId })

				store.dispatch({
					type: 'CHANGE_NOTE_FORM_FIELD',
					payload: { name: 'subTypeId', value: 20 }
				})

				await waitFor(() => {
					const node = getByTestId('fromTime_field-label')
					expect(String(node.innerHTML).endsWith('*')).toBeTruthy()
				})
			})

			it('Encounter Date To', async () => {
				const {
					store, getByTestId
				} = renderAndConfigure({ clientId, userOrganizationId })

				store.dispatch({
					type: 'CHANGE_NOTE_FORM_FIELD',
					payload: { name: 'subTypeId', value: 20 }
				})

				await waitFor(() => {
					const node = getByTestId('toDate_field-label')
					expect(String(node.innerHTML).endsWith('*')).toBeTruthy()
				})
			})

			it('Time To', async () => {
				const {
					store, getByTestId
				} = renderAndConfigure({ clientId, userOrganizationId })

				store.dispatch({
					type: 'CHANGE_NOTE_FORM_FIELD',
					payload: { name: 'subTypeId', value: 20 }
				})

				await waitFor(() => {
					const node = getByTestId('toTime_field-label')
					expect(String(node.innerHTML).endsWith('*')).toBeTruthy()
				})
			})

			it('Total time spent', async () => {
				const {
					store, getByTestId
				} = renderAndConfigure({ clientId, userOrganizationId })

				store.dispatch({
					type: 'CHANGE_NOTE_FORM_FIELD',
					payload: { name: 'subTypeId', value: 20 }
				})

				await waitFor(() => {
					const node = getByTestId('totalTime_field-label')
					expect(String(node.innerHTML).endsWith('*')).toBeTruthy()
				})
			})

			it('Range', async () => {
				const {
					store, getByTestId
				} = renderAndConfigure({ clientId, userOrganizationId })

				store.dispatch({
					type: 'CHANGE_NOTE_FORM_FIELD',
					payload: { name: 'subTypeId', value: 20 }
				})

				await waitFor(() => {
					const node = getByTestId('range_field-label')
					expect(String(node.innerHTML).endsWith('*')).toBeTruthy()
				})
			})

			it('Units', async () => {
				const {
					store, getByTestId
				} = renderAndConfigure({ clientId, userOrganizationId })

				store.dispatch({
					type: 'CHANGE_NOTE_FORM_FIELD',
					payload: { name: 'subTypeId', value: 20 }
				})

				await waitFor(() => {
					const node = getByTestId('units_field-label')
					expect(String(node.innerHTML).endsWith('*')).toBeTruthy()
				})
			})
		})

		it('Subjective', async () => {
			let { getByTestId } = render(
				<NoteForm clientId={clientId}/>
			)
			const node = getByTestId('subjective_field-label')
			expect(String(node.innerHTML).endsWith('*')).toBeTruthy()
		})
	})

	describe('Fields disabled by default:', function () {
		const clientId = 32041
		const userOrganizationId = 3

		it('Program sub type', async function () {
			const {
				getByTestId
			} = renderAndConfigure({ clientId, userOrganizationId })

			await waitFor(() => {
				const node = getByTestId('author_field')
				expect(node).toHaveClass('TextField_disabled')
			})
		})

		it('Client', async function () {
			const {
				getByTestId
			} = renderAndConfigure({ clientId, userOrganizationId })

			await waitFor(() => {
				const node = getByTestId('clientName_field')
				expect(node).toHaveClass('TextField_disabled')
			})
		})

		it('Admit / Intake Date', async function () {
			const {
				getByTestId
			} = renderAndConfigure({ clientId, userOrganizationId })

			await waitFor(() => {
				const node = getByTestId('admitDateId_field')
				expect(node).toHaveClass('SelectField_disabled')
			})
		})

		it('Total time spent', async function () {
			const {
				getByTestId
			} = renderAndConfigure({ clientId, userOrganizationId })

			await waitFor(() => {
				const node = getByTestId('totalTime_field')
				expect(node).toHaveClass('TextField_disabled')
			})
		})

		it('Range', async function () {
			const {
				getByTestId
			} = renderAndConfigure({ clientId, userOrganizationId })

			await waitFor(() => {
				const node = getByTestId('range_field')
				expect(node).toHaveClass('TextField_disabled')
			})
		})

		it('Units', async function () {
			const {
				getByTestId
			} = renderAndConfigure({ clientId, userOrganizationId })

			await waitFor(() => {
				const node = getByTestId('units_field')
				expect(node).toHaveClass('TextField_disabled')
			})
		})
	})

	describe('Add new Note. Default Field values:', function () {
		const clientId = 32041
		const organizationId = 3
		const userOrganizationId = 3

		const client = findWhere(ClientDetails, { id: clientId })
		const user = findWhere(User, { organizationId: userOrganizationId })

		it('Person Submitting Note', async function () {
			const {
				getByTestId
			} = renderAndConfigure({ clientId, userOrganizationId })

			await waitFor(() => {
				const node = getByTestId('author_field-input')
				expect(node).toHaveValue(user.fullName)
			})
		})

		it('Client', async function () {
			const {
				getByTestId
			} = renderAndConfigure({ clientId, userOrganizationId })

			await waitFor(() => {
				const node = getByTestId('clientName_field-input')
				expect(node).toHaveValue(client.fullName)
			})
		})

		it('Note Date and Time', async function () {
			const {
				getByTestId
			} = renderAndConfigure({ clientId, userOrganizationId })

			await waitFor(() => {
				const node = getByTestId('noteDate_field-input')
				expect(node).toHaveValue(format(new Date(), formats.longDateMediumTime12))
			})
		})

		it('Note Type', async function () {
			const {
				getByTestId
			} = renderAndConfigure({ clientId, userOrganizationId })

			await waitFor(() => {
				const node = getByTestId('subTypeId_search-input')
				expect(node).toBeInTheDocument()
				expect(node).toBeVisible()
				expect(node).toHaveAttribute('placeholder', 'Select')
			})
		})

		it('Admit / Intake Date', async function () {
			const {
				getByTestId
			} = renderAndConfigure({ clientId, userOrganizationId })

			await waitFor(() => {
				const node = getByTestId('admitDateId_selected-text')
				expect(node).toHaveTextContent('Select')
			})
		})

		describe('Program note type:', function () {
			const userOrganizationId = 3

			it('Program sub type', async function () {
				const {
					store, getByTestId
				} = renderAndConfigure({ clientId, userOrganizationId })

				store.dispatch({
					type: 'CHANGE_NOTE_FORM_FIELD',
					payload: { name: 'subTypeId', value: 10030 }
				})

				await waitFor(() => {
					const node = getByTestId('clientProgram.typeId_search-input')
					expect(node).toBeInTheDocument()
					expect(node).toBeVisible()
					expect(node).toHaveAttribute('placeholder', 'Select')
				})
			})

			it('Service Provider for Program', async function () {
				const {
					store, getByTestId
				} = renderAndConfigure({ clientId, userOrganizationId })

				store.dispatch({
					type: 'CHANGE_NOTE_FORM_FIELD',
					payload: { name: 'subTypeId', value: 10030 }
				})

				await waitFor(() => {
					const node = getByTestId('clientProgram.serviceProvider_field-input')
					expect(node).toHaveValue('')
				})
			})

			it('Start Date of Program', async function () {
				const {
					store, getByTestId
				} = renderAndConfigure({ clientId, userOrganizationId })

				store.dispatch({
					type: 'CHANGE_NOTE_FORM_FIELD',
					payload: { name: 'subTypeId', value: 10030 }
				})

				await waitFor(() => {
					const node = getByTestId('clientProgram.startDate_field-input')
					expect(node).toHaveValue('')
				})
			})

			it('End Date of Program', async function () {
				const {
					store, getByTestId
				} = renderAndConfigure({ clientId, userOrganizationId })

				store.dispatch({
					type: 'CHANGE_NOTE_FORM_FIELD',
					payload: { name: 'subTypeId', value: 10030 }
				})

				await waitFor(() => {
					const node = getByTestId('clientProgram.endDate_field-input')
					expect(node).toHaveValue('')
				})
			})
		})

		describe('Service Status Check note type', function () {
			const userOrganizationId = 3

			it('Service Plan', async function () {
				const {
					store, getByTestId
				} = renderAndConfigure({ clientId, userOrganizationId })

				store.dispatch({
					type: 'CHANGE_NOTE_FORM_FIELD',
					payload: { name: 'subTypeId', value: 10029 }
				})

				await waitFor(() => {
					const node = getByTestId('serviceStatusCheck.servicePlanCreatedDate_field-input')
					expect(node).toHaveValue(format(
						new Date(2020, 1, 13, 11, 40),
						formats.longDateMediumTime12
					))
				})
			})

			it('Resource Name', async function () {
				const {
					store, getByTestId
				} = renderAndConfigure({ clientId, userOrganizationId })

				store.dispatch({
					type: 'CHANGE_NOTE_FORM_FIELD',
					payload: { name: 'subTypeId', value: 10029 }
				})

				await waitFor(() => {
					const node = getByTestId('serviceStatusCheck.resourceName_field-input')
					expect(node).toHaveValue('ege, ere')
				})
			})

			it('Person Who Did the Audit / Check', async function () {
				const {
					store, getByTestId
				} = renderAndConfigure({ clientId, userOrganizationId })

				store.dispatch({
					type: 'CHANGE_NOTE_FORM_FIELD',
					payload: { name: 'subTypeId', value: 10029 }
				})

				await waitFor(() => {
					const node = getByTestId('serviceStatusCheck.auditPerson_field-input')
					expect(node).toHaveValue(user.fullName)
				})
			})

			it('Date of Check', async function () {
				const {
					store, getByTestId
				} = renderAndConfigure({ clientId, userOrganizationId })

				store.dispatch({
					type: 'CHANGE_NOTE_FORM_FIELD',
					payload: { name: 'subTypeId', value: 10029 }
				})

				await waitFor(() => {
					const node = getByTestId('serviceStatusCheck.checkDate_field-input')
					expect(node).toHaveValue(format(new Date(), formats.longDateMediumTime12))
				})
			})

			it('Next Date of Check', async function () {
				const {
					store, getByTestId
				} = renderAndConfigure({ clientId, userOrganizationId })

				store.dispatch({
					type: 'CHANGE_NOTE_FORM_FIELD',
					payload: { name: 'subTypeId', value: 10029 }
				})

				await waitFor(() => {
					const node = getByTestId('serviceStatusCheck.nextCheckDate_field-input')
					expect(node).toHaveValue('')
				})
			})

			it('Is the service being provided?', async function () {
				const {
					store, getByTestId
				} = render(
					<NoteForm clientId={clientId}/>
				)

				const user = findWhere(User, { organizationId: userOrganizationId })
				store.dispatch({ type: 'LOGIN_SUCCESS', payload: user })

				const client = findWhere(ClientDetails, { id: clientId })
				store.dispatch({ type: 'LOAD_CLIENT_DETAILS_SUCCESS', payload: client })

				store.dispatch({
					type: 'CHANGE_NOTE_FORM_FIELD',
					payload: { name: 'subTypeId', value: 10029 }
				})

				await waitFor(() => {
					const node = getByTestId('serviceStatusCheck.serviceProvided_field-true-check-mark')
					expect(node).not.toHaveClass('Radio-CheckMark_checked')
				})

				await waitFor(() => {
					const node = getByTestId('serviceStatusCheck.serviceProvided_field-false-check-mark')
					expect(node).not.toHaveClass('Radio-CheckMark_checked')
				})
			})
		})

		it('Person Completing the Encounter', async function () {
			const {
				getByTestId
			} = renderAndConfigure({ clientId, userOrganizationId })

			await waitFor(() => {
				const node = getByTestId('clinicianId_selected-text')
				expect(node).toHaveTextContent(user.fullName)
			})
		})

		it('Encounter Date From', async function () {
			const {
				getByTestId
			} = renderAndConfigure({ clientId, userOrganizationId })

			await waitFor(() => {
				const node = getByTestId('fromDate_field-input')
				expect(node).toHaveValue(format(new Date(), formats.americanMediumDate))
			})
		})

		it('Time From', async function () {
			const {
				getByTestId
			} = renderAndConfigure({ clientId, userOrganizationId })

			await waitFor(() => {
				const node = getByTestId('fromTime_field-input')
				expect(node).toHaveValue(format(new Date(), formats.time2))
			})
		})

		it('Encounter Date To', async function () {
			const {
				getByTestId
			} = renderAndConfigure({ clientId, userOrganizationId })

			await waitFor(() => {
				const node = getByTestId('toDate_field-input')
				expect(node).toHaveValue('')
			})
		})

		it('Time To', async function () {
			const {
				getByTestId
			} = renderAndConfigure({ clientId, userOrganizationId })

			await waitFor(() => {
				const node = getByTestId('toTime_field-input')
				expect(node).toHaveValue('')
			})
		})

		it('Total time spent', async function () {
			const {
				getByTestId
			} = renderAndConfigure({ clientId, userOrganizationId })

			await waitFor(() => {
				const node = getByTestId('totalTime_field-input')
				expect(node).toHaveValue('')
			})
		})

		it('Range', async function () {
			const {
				getByTestId
			} = renderAndConfigure({ clientId, userOrganizationId })

			await waitFor(() => {
				const node = getByTestId('totalTime_field-input')
				expect(node).toHaveValue('')
			})
		})

		it('Units', async function () {
			const {
				getByTestId
			} = renderAndConfigure({ clientId, userOrganizationId })

			await waitFor(() => {
				const node = getByTestId('totalTime_field-input')
				expect(node).toHaveValue('')
			})
		})

		describe('Face to Face encounter note type', function () {
			it('"Encounter type" field is visible', async function () {
				const {
					store, getByTestId
				} = renderAndConfigure({ clientId, userOrganizationId })

				store.dispatch({
					type: 'CHANGE_NOTE_FORM_FIELD',
					payload: { name: 'subTypeId', value: 19 }
				})

				await waitFor(() => {
					const node = getByTestId('typeId_field')
					expect(node).toBeInTheDocument()
					expect(node).toBeVisible()
				})
			})

			it('"Encounter type" field is empty', async function () {
				const {
					store, getByTestId
				} = renderAndConfigure({ clientId, userOrganizationId })

				store.dispatch({
					type: 'CHANGE_NOTE_FORM_FIELD',
					payload: { name: 'subTypeId', value: 19 }
				})

				await waitFor(() => {
					const node = getByTestId('typeId_selected-text')
					expect(node).toHaveTextContent('Select')
				})
			})

			it('"Encounter Date To" field initialized correctly', async function () {
				const {
					store, getByTestId
				} = renderAndConfigure({ clientId, userOrganizationId })

				store.dispatch({
					type: 'CHANGE_NOTE_FORM_FIELD',
					payload: { name: 'subTypeId', value: 19 }
				})

				await waitFor(() => {
					const node = getByTestId('toDate_field-input')
					expect(node).toHaveValue(format(new Date(), formats.americanMediumDate))
				})
			})

			it('"Time To" field initialized correctly', async function () {
				const {
					store, getByTestId
				} = renderAndConfigure({ clientId, userOrganizationId })

				store.dispatch({
					type: 'CHANGE_NOTE_FORM_FIELD',
					payload: { name: 'subTypeId', value: 19 }
				})

				const HALF_HOUR = 30
				const QUARTER_HOUR = 15
				let minutes = moment().minutes()
				const remainder = minutes % HALF_HOUR

				if (remainder) {
					minutes = minutes < HALF_HOUR ? HALF_HOUR : HALF_HOUR * 2
				}

				await waitFor(() => {
					const node = getByTestId('toTime_field-input')
					expect(node).toHaveValue(format(
						moment().clone()
							.set('minutes', minutes + QUARTER_HOUR)
							.startOf('minute')
							.valueOf(),
						formats.time2
					))
				})
			})
		})

		describe('Non Face to Face encounter note type', function () {
			it('"Encounter type" field is visible', async function () {
				const {
					store, getByTestId
				} = renderAndConfigure({ clientId, userOrganizationId })

				store.dispatch({
					type: 'CHANGE_NOTE_FORM_FIELD',
					payload: { name: 'subTypeId', value: 20 }
				})

				await waitFor(() => {
					const node = getByTestId('typeId_field')
					expect(node).toBeInTheDocument()
					expect(node).toBeVisible()
				})
			})

			it('"Encounter type" field is empty', async function () {
				const {
					store, getByTestId
				} = renderAndConfigure({ clientId, userOrganizationId })

				store.dispatch({
					type: 'CHANGE_NOTE_FORM_FIELD',
					payload: { name: 'subTypeId', value: 20 }
				})

				await waitFor(() => {
					const node = getByTestId('typeId_selected-text')
					expect(node).toHaveTextContent('Select')
				})
			})

			it('"Encounter Date To" field initialized correctly', async function () {
				const {
					store, getByTestId
				} = renderAndConfigure({ clientId, userOrganizationId })

				store.dispatch({
					type: 'CHANGE_NOTE_FORM_FIELD',
					payload: { name: 'subTypeId', value: 20 }
				})

				await waitFor(() => {
					const node = getByTestId('toDate_field-input')
					expect(node).toHaveValue(format(new Date(), formats.americanMediumDate))
				})
			})

			it('"Time To" field initialized correctly', async function () {
				const {
					store, getByTestId
				} = renderAndConfigure({ clientId, userOrganizationId })

				store.dispatch({
					type: 'CHANGE_NOTE_FORM_FIELD',
					payload: { name: 'subTypeId', value: 20 }
				})

				const HALF_HOUR = 30
				const QUARTER_HOUR = 15
				let minutes = moment().minutes()
				const remainder = minutes % HALF_HOUR

				if (remainder) {
					minutes = minutes < HALF_HOUR ? HALF_HOUR : HALF_HOUR * 2
				}

				await waitFor(() => {
					const node = getByTestId('toTime_field-input')
					expect(node).toHaveValue(format(
						moment().clone()
							.set('minutes', minutes + QUARTER_HOUR)
							.startOf('minute')
							.valueOf(),
						formats.time2
					))
				})
			})
		})

		it('Subjective', async function () {
			const {
				getByTestId
			} = renderAndConfigure({ clientId, userOrganizationId })

			await waitFor(() => {
				const node = getByTestId('subjective_field-input')
				expect(node).toHaveValue('')
			})
		})

		it('Objective', async function () {
			const {
				getByTestId
			} = renderAndConfigure({ clientId, userOrganizationId })

			await waitFor(() => {
				const node = getByTestId('objective_field-input')
				expect(node).toHaveValue('')
			})
		})

		it('Assessment', async function () {
			const {
				getByTestId
			} = renderAndConfigure({ clientId, userOrganizationId })

			await waitFor(() => {
				const node = getByTestId('assessment_field-input')
				expect(node).toHaveValue('')
			})
		})

		it('Plan', async function () {
			const {
				getByTestId
			} = renderAndConfigure({ clientId, userOrganizationId })

			await waitFor(() => {
				const node = getByTestId('plan_field-input')
				expect(node).toHaveValue('')
			})
		})
	})

	describe('Editing Note. Field initialization:', function () {
		const clientId = 20594
		const userOrganizationId = 3

		const note1Id = 25692
		const note1 = findWhere(NoteDetails, { id: note1Id })

		const note2Id = 25693
		const note2 = findWhere(NoteDetails, { id: note2Id })

		const note3Id = 25695
		const note3 = findWhere(NoteDetails, { id: note3Id })

		it('Person Submitting Note', async function () {
			const {
				store, getByTestId
			} = renderAndConfigure({ noteId: note1Id, clientId, userOrganizationId })

			store.dispatch({ type: 'LOAD_NOTE_DETAILS_SUCCESS', payload: { data: note1 } })

			await waitFor(() => {
				const node = getByTestId('author_field-input')
				expect(node).toHaveValue(note1.author)
			})
		})

		it('Client', async function () {
			const {
				store, getByTestId
			} = renderAndConfigure({ noteId: note1Id, clientId, userOrganizationId })

			store.dispatch({ type: 'LOAD_NOTE_DETAILS_SUCCESS', payload: { data: note1 } })

			await waitFor(() => {
				const node = getByTestId('clientName_field-input')
				expect(node).toHaveValue(note1.clientName)
			})
		})

		it('Note Date and Time', async function () {
			const {
				store, getByTestId
			} = renderAndConfigure({ noteId: note1Id, clientId, userOrganizationId })

			store.dispatch({ type: 'LOAD_NOTE_DETAILS_SUCCESS', payload: { data: note1 } })

			await waitFor(() => {
				const node = getByTestId('noteDate_field-input')
				expect(node).toHaveValue(format(note1.noteDate, formats.longDateMediumTime12))
			})
		})

		it('Note Type', async function () {
			const {
				store, getByTestId
			} = renderAndConfigure({ noteId: note3Id,  clientId, userOrganizationId })

			store.dispatch({ type: 'LOAD_NOTE_DETAILS_SUCCESS', payload: { data: note3 } })

			await waitFor(() => {
				const node = getByTestId('subTypeId_selected-text')
				expect(node).toHaveTextContent(note3.subTypeTitle)
			})
		})

		it('Admit / Intake Date', async function () {
			const {
				store, getByTestId
			} = renderAndConfigure({ noteId: note3Id,  clientId, userOrganizationId })

			store.dispatch({ type: 'LOAD_NOTE_DETAILS_SUCCESS', payload: { data: note3 } })

			await waitFor(() => {
				const node = getByTestId('admitDateId_selected-text')
				expect(node).toHaveTextContent(format(note3.admitDate, formats.longDateMediumTime12))
			})
		})

		describe('Program note type:', function () {
			it('Program sub type', async function () {
				const {
					store, getByTestId
				} = renderAndConfigure({ noteId: note1Id, clientId, userOrganizationId })

				store.dispatch({ type: 'LOAD_NOTE_DETAILS_SUCCESS', payload: { data: note1 } })

				await waitFor(() => {
					const node = getByTestId('clientProgram.typeId_search-input')
					expect(node).toHaveValue(note1.clientProgram.typeTitle)
				})
			})

			it('Service Provider for Program', async function () {
				const {
					store, getByTestId
				} = renderAndConfigure({ noteId: note1Id, clientId, userOrganizationId })

				store.dispatch({ type: 'LOAD_NOTE_DETAILS_SUCCESS', payload: { data: note1 } })

				await waitFor(() => {
					const node = getByTestId('clientProgram.serviceProvider_field-input')
					expect(node).toHaveValue(note1.clientProgram.serviceProvider)
				})
			})

			it('Start Date of Program', async function () {
				const {
					store, getByTestId
				} = renderAndConfigure({ noteId: note1Id, clientId, userOrganizationId })

				store.dispatch({ type: 'LOAD_NOTE_DETAILS_SUCCESS', payload: { data: note1 } })

				await waitFor(() => {
					const node = getByTestId('clientProgram.startDate_field-input')
					expect(node).toHaveValue(format(note1.clientProgram.startDate, formats.americanMediumDate))
				})
			})

			it('End Date of Program', async function () {
				const {
					store, getByTestId
				} = renderAndConfigure({ noteId: note1Id, clientId, userOrganizationId })

				store.dispatch({ type: 'LOAD_NOTE_DETAILS_SUCCESS', payload: { data: note1 } })

				await waitFor(() => {
					const node = getByTestId('clientProgram.endDate_field-input')
					expect(node).toHaveValue(format(note1.clientProgram.endDate, formats.americanMediumDate))
				})
			})
		})

		describe('Service Status Check note type', function () {
			it('Service Plan', async function () {
				const {
					store, getByTestId
				} = renderAndConfigure({ noteId: note2Id, clientId, userOrganizationId })

				store.dispatch({ type: 'LOAD_NOTE_DETAILS_SUCCESS', payload: { data: note2 } })

				await waitFor(() => {
					const node = getByTestId('serviceStatusCheck.servicePlanCreatedDate_field-input')
					expect(node).toHaveValue(format(
						note2.serviceStatusCheck.servicePlanCreatedDate,
						formats.longDateMediumTime12
					))
				})
			})

			it('Resource Name', async function () {
				const {
					store, getByTestId
				} = renderAndConfigure({ noteId: note2Id, clientId, userOrganizationId })

				store.dispatch({ type: 'LOAD_NOTE_DETAILS_SUCCESS', payload: { data: note2 } })

				await waitFor(() => {
					const node = getByTestId('serviceStatusCheck.resourceName_field-input')
					expect(node).toHaveValue(
						`${note2.serviceStatusCheck.providerName}, ${note2.serviceStatusCheck.resourceName}`
					)
				})
			})

			it('Person Who Did the Audit / Check', async function () {
				const {
					store, getByTestId
				} = renderAndConfigure({ noteId: note2Id, clientId, userOrganizationId })

				store.dispatch({ type: 'LOAD_NOTE_DETAILS_SUCCESS', payload: { data: note2 } })

				await waitFor(() => {
					const node = getByTestId('serviceStatusCheck.auditPerson_field-input')
					expect(node).toHaveValue(note2.serviceStatusCheck.auditPerson)
				})
			})

			it('Date of Check', async function () {
				const {
					store, getByTestId
				} = renderAndConfigure({ noteId: note2Id, clientId, userOrganizationId })

				store.dispatch({ type: 'LOAD_NOTE_DETAILS_SUCCESS', payload: { data: note2 } })

				await waitFor(() => {
					const node = getByTestId('serviceStatusCheck.checkDate_field-input')
					expect(node).toHaveValue(format(note2.serviceStatusCheck.checkDate, formats.longDateMediumTime12))
				})
			})

			it('Next Date of Check', async function () {
				const {
					store, getByTestId
				} = renderAndConfigure({ noteId: note2Id, clientId, userOrganizationId })

				store.dispatch({ type: 'LOAD_NOTE_DETAILS_SUCCESS', payload: { data: note2 } })

				await waitFor(() => {
					const node = getByTestId('serviceStatusCheck.nextCheckDate_field-input')
					expect(node).toHaveValue(format(note2.serviceStatusCheck.nextCheckDate, formats.longDateMediumTime12))
				})
			})

			it('Is the service being provided?', async function () {
				const {
					store, getByTestId
				} = renderAndConfigure({ noteId: note2Id, clientId, userOrganizationId })

				store.dispatch({ type: 'LOAD_NOTE_DETAILS_SUCCESS', payload: { data: note2 } })

				await waitFor(() => {
					const node = getByTestId('serviceStatusCheck.serviceProvided_field-true-check-mark')

					if (note2.serviceStatusCheck.serviceProvided) {
						expect(node).toHaveClass('Radio-CheckMark_checked')
					} else expect(node).not.toHaveClass('Radio-CheckMark_checked')
				})

				await waitFor(() => {
					const node = getByTestId('serviceStatusCheck.serviceProvided_field-false-check-mark')

					if (note2.serviceStatusCheck.serviceProvided === false) {
						expect(node).toHaveClass('Radio-CheckMark_checked')
					} else expect(node).not.toHaveClass('Radio-CheckMark_checked')
				})
			})
		})

		it('Person Completing the Encounter', async function () {
			const {
				store, getByTestId
			} = renderAndConfigure({ noteId: note1Id, clientId, userOrganizationId })

			store.dispatch({ type: 'LOAD_NOTE_DETAILS_SUCCESS', payload: { data: note1 } })

			await waitFor(() => {
				const node = getByTestId('clinicianId_selected-text')
				expect(node).toHaveTextContent(note1.encounter.clinicianTitle)
			})
		})

		it('Encounter Date From', async function () {
			const {
				store, getByTestId
			} = renderAndConfigure({ noteId: note1Id, clientId, userOrganizationId })

			store.dispatch({ type: 'LOAD_NOTE_DETAILS_SUCCESS', payload: { data: note1 } })

			await waitFor(() => {
				const node = getByTestId('fromDate_field-input')
				expect(node).toHaveValue(format(note1.encounter.fromDate, formats.americanMediumDate))
			})
		})

		it('Time From', async function () {
			const {
				store, getByTestId
			} = renderAndConfigure({ noteId: note1Id, clientId, userOrganizationId })

			store.dispatch({ type: 'LOAD_NOTE_DETAILS_SUCCESS', payload: { data: note1 } })

			await waitFor(() => {
				const node = getByTestId('fromTime_field-input')
				expect(node).toHaveValue(format(note1.encounter.fromDate, formats.time2))
			})
		})

		it('Encounter Date To', async function () {
			const {
				store, getByTestId
			} = renderAndConfigure({ noteId: note2Id, clientId, userOrganizationId })

			store.dispatch({ type: 'LOAD_NOTE_DETAILS_SUCCESS', payload: { data: note2 } })

			await waitFor(() => {
				const node = getByTestId('toDate_field-input')
				expect(node).toHaveValue(format(note2.encounter.toDate, formats.americanMediumDate))
			})
		})

		it('Time To', async function () {
			const {
				store, getByTestId
			} = renderAndConfigure({ noteId: note2Id, clientId, userOrganizationId })

			store.dispatch({ type: 'LOAD_NOTE_DETAILS_SUCCESS', payload: { data: note2 } })

			await waitFor(() => {
				const node = getByTestId('toTime_field-input')
				expect(node).toHaveValue(format(note2.encounter.toDate, formats.time2))
			})
		})

		it('Total time spent', async function () {
			const {
				store, getByTestId
			} = renderAndConfigure({ noteId: note1Id, clientId, userOrganizationId })

			store.dispatch({ type: 'LOAD_NOTE_DETAILS_SUCCESS', payload: { data: note1 } })

			await waitFor(() => {
				const node = getByTestId('totalTime_field-input')
				expect(node).toHaveValue('1455')
			})
		})

		it('Range', async function () {
			const {
				store, getByTestId
			} = renderAndConfigure({ noteId: note1Id, clientId, userOrganizationId })

			store.dispatch({ type: 'LOAD_NOTE_DETAILS_SUCCESS', payload: { data: note1 } })

			await waitFor(() => {
				const node = getByTestId('range_field-input')
				expect(node).toHaveValue('1448 mins - 1462 mins')
			})
		})

		it('Units', async function () {
			const {
				store, getByTestId
			} = renderAndConfigure({ noteId: note1Id, clientId, userOrganizationId })

			store.dispatch({ type: 'LOAD_NOTE_DETAILS_SUCCESS', payload: { data: note1 } })

			await waitFor(() => {
				const node = getByTestId('units_field-input')
				expect(node).toHaveValue('97')
			})
		})

		it('Subjective', async function () {
			const {
				store, getByTestId
			} = renderAndConfigure({ noteId: note1Id, clientId, userOrganizationId })

			store.dispatch({ type: 'LOAD_NOTE_DETAILS_SUCCESS', payload: { data: note1 } })

			await waitFor(() => {
				const node = getByTestId('subjective_field-input')
				expect(node).toHaveValue(note1.subjective)
			})
		})

		it('Objective', async function () {
			const {
				store, getByTestId
			} = renderAndConfigure({ noteId: note1Id, clientId, userOrganizationId })

			store.dispatch({ type: 'LOAD_NOTE_DETAILS_SUCCESS', payload: { data: note1 } })

			await waitFor(() => {
				const node = getByTestId('objective_field-input')
				expect(node).toHaveValue(note1.objective)
			})
		})

		it('Assessment', async function () {
			const {
				store, getByTestId
			} = renderAndConfigure({ noteId: note1Id, clientId, userOrganizationId })

			store.dispatch({ type: 'LOAD_NOTE_DETAILS_SUCCESS', payload: { data: note1 } })

			await waitFor(() => {
				const node = getByTestId('assessment_field-input')
				expect(node).toHaveValue(note1.assessment)
			})
		})

		it('Plan', async function () {
			const {
				store, getByTestId
			} = renderAndConfigure({ noteId: note1Id, clientId, userOrganizationId })

			store.dispatch({ type: 'LOAD_NOTE_DETAILS_SUCCESS', payload: { data: note1 } })

			await waitFor(() => {
				const node = getByTestId('plan_field-input')
				expect(node).toHaveValue(note1.plan)
			})
		})
	})

	afterAll(() => {
		server.close()
	})
})