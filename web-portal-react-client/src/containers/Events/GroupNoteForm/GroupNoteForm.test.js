import React from 'react'

import {
	where,
	findWhere
} from 'underscore'

import moment from 'moment'
import { rest } from 'msw'
import { setupServer } from 'msw/node'
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
	ContactDetails,
	ClientProgramNoteType, Client
} from 'lib/mock/db/DB'

import NoteForm from './GroupNoteForm'

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
	rest.get(`${BASE_URL}/organizations/:organizationId/notes/contacts`, (req, res, ctx) => {
		return res(ctx.json(Response.success(Contact)))
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
	rest.get(`${BASE_URL}/authorized-directory/clients`, (req, res, ctx) => {
		return res(ctx.json(Response.success(Client)))
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

function renderAndConfigure({ noteId, userOrganizationId = 3 }) {

	const {
		store, ...config
	} = render(
		<NoteForm
			noteId={noteId}
		/>
	)

	const user = findWhere(User, { organizationId: userOrganizationId })
	store.dispatch({ type: 'LOGIN_SUCCESS', payload: user })

	return { store, ...config }
}

describe('<GroupNoteForm>:', function () {
	beforeAll(() => {
		server.listen()
	})

	describe('All fields are visible:', function () {
		const userOrganizationId = 3

		it('Person Submitting Note', async () => {
			let { getByTestId } = render(
				<NoteForm/>
			)

			const node = getByTestId('author_field')
			expect(node).toBeInTheDocument()
			expect(node).toBeVisible()
		})

		it('Note Date and Time', async () => {
			let { getByTestId } = render(
				<NoteForm/>
			)

			const node = getByTestId('noteDate_field')
			expect(node).toBeInTheDocument()
			expect(node).toBeVisible()
		})

		it('Note Type', async () => {
			let { getByTestId } = render(
				<NoteForm/>
			)

			const node = getByTestId('subTypeId_field')
			expect(node).toBeInTheDocument()
			expect(node).toBeVisible()
		})

		it('Note Name', async () => {
			let { getByTestId } = render(
				<NoteForm/>
			)

			const node = getByTestId('noteName_field')
			expect(node).toBeInTheDocument()
			expect(node).toBeVisible()
		})

		it('Clients', async () => {
			let { getByTestId } = render(
				<NoteForm/>
			)

			const node = getByTestId('clients_field')
			expect(node).toBeInTheDocument()
			expect(node).toBeVisible()
		})

		describe('Program note type:', function () {
			const userOrganizationId = 3

			it('Program sub type', async function () {
				const {
					store, getByTestId
				} = renderAndConfigure({ userOrganizationId })

				store.dispatch({
					type: 'CHANGE_GROUP_NOTE_FORM_FIELD',
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
				} = renderAndConfigure({ userOrganizationId })

				store.dispatch({
					type: 'CHANGE_GROUP_NOTE_FORM_FIELD',
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
				} = renderAndConfigure({ userOrganizationId })

				store.dispatch({
					type: 'CHANGE_GROUP_NOTE_FORM_FIELD',
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
				} = renderAndConfigure({ userOrganizationId })

				store.dispatch({
					type: 'CHANGE_GROUP_NOTE_FORM_FIELD',
					payload: { name: 'subTypeId', value: 10030 }
				})

				await waitFor(() => {
					const node = getByTestId('clientProgram.endDate_field')
					expect(node).toBeInTheDocument()
					expect(node).toBeVisible()
				})
			})
		})

		it('Person Completing the Encounter', async () => {
			let { getByTestId } = renderAndConfigure({ userOrganizationId })

			await waitFor(() => {
				const node = getByTestId('clinicianId_field')
				expect(node).toBeInTheDocument()
				expect(node).toBeVisible()
			})

		})

		it('Encounter Date From', async () => {
			let { getByTestId } = renderAndConfigure({ userOrganizationId })

			await waitFor(() => {
				const node = getByTestId('fromDate_field')
				expect(node).toBeInTheDocument()
				expect(node).toBeVisible()
			})

		})

		it('Time From', async () => {
			let { getByTestId } = renderAndConfigure({ userOrganizationId })

			await waitFor(() => {
				const node = getByTestId('fromTime_field')
				expect(node).toBeInTheDocument()
				expect(node).toBeVisible()
			})

		})

		it('Encounter Date To', async () => {
			let { getByTestId } = renderAndConfigure({ userOrganizationId })

			await waitFor(() => {
				const node = getByTestId('toDate_field')
				expect(node).toBeInTheDocument()
				expect(node).toBeVisible()
			})

		})

		it('Time To', async () => {
			let { getByTestId } = renderAndConfigure({ userOrganizationId })

			await waitFor(() => {
				const node = getByTestId('toTime_field')
				expect(node).toBeInTheDocument()
				expect(node).toBeVisible()
			})
		})

		it('Total time spent', async () => {
			let { getByTestId } = renderAndConfigure({ userOrganizationId })

			await waitFor(() => {
				const node = getByTestId('totalTime_field')
				expect(node).toBeInTheDocument()
				expect(node).toBeVisible()
			})
		})

		it('Range', async () => {
			let { getByTestId } = renderAndConfigure({ userOrganizationId })

			await waitFor(() => {
				const node = getByTestId('range_field')
				expect(node).toBeInTheDocument()
				expect(node).toBeVisible()
			})
		})

		it('Units', async () => {
			let { getByTestId } = renderAndConfigure({ userOrganizationId })

			await waitFor(() => {
				const node = getByTestId('units_field')
				expect(node).toBeInTheDocument()
				expect(node).toBeVisible()
			})
		})

		it('Subjective', async () => {
			let { getByTestId } = renderAndConfigure({ userOrganizationId })

			await waitFor(() => {
				const node = getByTestId('subjective_field')
				expect(node).toBeInTheDocument()
				expect(node).toBeVisible()
			})
		})

		it('Objective', async () => {
			let { getByTestId } = renderAndConfigure({ userOrganizationId })

			await waitFor(() => {
				const node = getByTestId('objective_field')
				expect(node).toBeInTheDocument()
				expect(node).toBeVisible()
			})
		})

		it('Assessment', async () => {
			let { getByTestId } = renderAndConfigure({ userOrganizationId })

			await waitFor(() => {
				const node = getByTestId('assessment_field')
				expect(node).toBeInTheDocument()
				expect(node).toBeVisible()
			})
		})

		it('Plan', async () => {
			let { getByTestId } = renderAndConfigure({ userOrganizationId })

			await waitFor(() => {
				const node = getByTestId('plan_field')
				expect(node).toBeInTheDocument()
				expect(node).toBeVisible()
			})
		})
	})

	describe('Fields marked as required:', function () {
		it('Person Submitting Note', async () => {
			let { getByTestId } = render(
				<NoteForm/>
			)
			const node = getByTestId('author_field-label')
			expect(String(node.innerHTML).endsWith('*')).toBeTruthy()
		})

		it('Note Date and Time', async () => {
			let { getByTestId } = render(
				<NoteForm/>
			)
			const node = getByTestId('noteDate_field-label')
			expect(String(node.innerHTML).endsWith('*')).toBeTruthy()
		})

		it('Note Type', async () => {
			let { getByTestId } = render(
				<NoteForm/>
			)
			const node = getByTestId('subTypeId_field-label')
			expect(String(node.innerHTML).endsWith('*')).toBeTruthy()
		})

		it('Clients', async () => {
			let { getByTestId } = render(
				<NoteForm/>
			)
			const node = getByTestId('clients_field-label')
			expect(String(node.innerHTML).endsWith('*')).toBeTruthy()
		})

		describe('Program note type:', function () {
			const userOrganizationId = 3

			it('Program sub type', async function () {
				const {
					store, getByTestId
				} = render(
					<NoteForm/>
				)

				const user = findWhere(User, { organizationId: userOrganizationId })
				store.dispatch({ type: 'LOGIN_SUCCESS', payload: user })

				store.dispatch({
					type: 'CHANGE_GROUP_NOTE_FORM_FIELD',
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
					<NoteForm/>
				)

				const user = findWhere(User, { organizationId: userOrganizationId })
				store.dispatch({ type: 'LOGIN_SUCCESS', payload: user })

				store.dispatch({
					type: 'CHANGE_GROUP_NOTE_FORM_FIELD',
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
					<NoteForm/>
				)

				const user = findWhere(User, { organizationId: userOrganizationId })
				store.dispatch({ type: 'LOGIN_SUCCESS', payload: user })

				store.dispatch({
					type: 'CHANGE_GROUP_NOTE_FORM_FIELD',
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
					<NoteForm/>
				)

				const user = findWhere(User, { organizationId: userOrganizationId })
				store.dispatch({ type: 'LOGIN_SUCCESS', payload: user })

				store.dispatch({
					type: 'CHANGE_GROUP_NOTE_FORM_FIELD',
					payload: { name: 'subTypeId', value: 10030 }
				})

				await waitFor(() => {
					const node = getByTestId('clientProgram.endDate_field-label')
					expect(String(node.innerHTML).endsWith('*')).toBeTruthy()
				})
			})
		})

		describe('Face to Face encounter note type:', function () {
			const userOrganizationId = 3

			it('Encounter type', async () => {
				const {
					store, getByTestId
				} = renderAndConfigure({ userOrganizationId })

				store.dispatch({
					type: 'CHANGE_GROUP_NOTE_FORM_FIELD',
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
				} = renderAndConfigure({ userOrganizationId })

				store.dispatch({
					type: 'CHANGE_GROUP_NOTE_FORM_FIELD',
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
				} = renderAndConfigure({ userOrganizationId })

				store.dispatch({
					type: 'CHANGE_GROUP_NOTE_FORM_FIELD',
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
				} = renderAndConfigure({ userOrganizationId })

				store.dispatch({
					type: 'CHANGE_GROUP_NOTE_FORM_FIELD',
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
				} = renderAndConfigure({ userOrganizationId })

				store.dispatch({
					type: 'CHANGE_GROUP_NOTE_FORM_FIELD',
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
				} = renderAndConfigure({ userOrganizationId })

				store.dispatch({
					type: 'CHANGE_GROUP_NOTE_FORM_FIELD',
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
				} = renderAndConfigure({ userOrganizationId })

				store.dispatch({
					type: 'CHANGE_GROUP_NOTE_FORM_FIELD',
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
				} = renderAndConfigure({ userOrganizationId })

				store.dispatch({
					type: 'CHANGE_GROUP_NOTE_FORM_FIELD',
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
				} = renderAndConfigure({ userOrganizationId })

				store.dispatch({
					type: 'CHANGE_GROUP_NOTE_FORM_FIELD',
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
				} = renderAndConfigure({ userOrganizationId })

				store.dispatch({
					type: 'CHANGE_GROUP_NOTE_FORM_FIELD',
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
				} = renderAndConfigure({ userOrganizationId })

				store.dispatch({
					type: 'CHANGE_GROUP_NOTE_FORM_FIELD',
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
				} = renderAndConfigure({ userOrganizationId })

				store.dispatch({
					type: 'CHANGE_GROUP_NOTE_FORM_FIELD',
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
				} = renderAndConfigure({ userOrganizationId })

				store.dispatch({
					type: 'CHANGE_GROUP_NOTE_FORM_FIELD',
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
				} = renderAndConfigure({ userOrganizationId })

				store.dispatch({
					type: 'CHANGE_GROUP_NOTE_FORM_FIELD',
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
				} = renderAndConfigure({ userOrganizationId })

				store.dispatch({
					type: 'CHANGE_GROUP_NOTE_FORM_FIELD',
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
				} = renderAndConfigure({ userOrganizationId })

				store.dispatch({
					type: 'CHANGE_GROUP_NOTE_FORM_FIELD',
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
				} = renderAndConfigure({ userOrganizationId })

				store.dispatch({
					type: 'CHANGE_GROUP_NOTE_FORM_FIELD',
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
				} = renderAndConfigure({ userOrganizationId })

				store.dispatch({
					type: 'CHANGE_GROUP_NOTE_FORM_FIELD',
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
				<NoteForm/>
			)
			const node = getByTestId('subjective_field-label')
			expect(String(node.innerHTML).endsWith('*')).toBeTruthy()
		})
	})

	describe('Fields disabled by default:', function () {
		const clientId = 32041
		const userOrganizationId = 3

		it('Person Submitting Note', async function () {
			const {
				getByTestId
			} = renderAndConfigure({ userOrganizationId })

			await waitFor(() => {
				const node = getByTestId('author_field')
				expect(node).toHaveClass('TextField_disabled')
			})
		})

		it('Total time spent', async function () {
			const {
				getByTestId
			} = renderAndConfigure({ userOrganizationId })

			await waitFor(() => {
				const node = getByTestId('totalTime_field')
				expect(node).toHaveClass('TextField_disabled')
			})
		})

		it('Range', async function () {
			const {
				getByTestId
			} = renderAndConfigure({ userOrganizationId })

			await waitFor(() => {
				const node = getByTestId('range_field')
				expect(node).toHaveClass('TextField_disabled')
			})
		})

		it('Units', async function () {
			const {
				getByTestId
			} = renderAndConfigure({ userOrganizationId })

			await waitFor(() => {
				const node = getByTestId('units_field')
				expect(node).toHaveClass('TextField_disabled')
			})
		})
	})

	describe('Add new Note. Default Field values:', function () {
		const clientId = 32041
		const userOrganizationId = 3

		const user = findWhere(User, { organizationId: userOrganizationId })

		it('Person Submitting Note', async function () {
			const {
				getByTestId
			} = renderAndConfigure({ userOrganizationId })

			await waitFor(() => {
				const node = getByTestId('author_field-input')
				expect(node).toHaveValue(user.fullName)
			})
		})

		it('Note Date and Time', async function () {
			const {
				getByTestId
			} = renderAndConfigure({ userOrganizationId })

			await waitFor(() => {
				const node = getByTestId('noteDate_field-input')
				expect(node).toHaveValue(format(new Date(), formats.longDateMediumTime12))
			})
		})

		it('Note Type', async function () {
			const {
				getByTestId
			} = renderAndConfigure({ userOrganizationId })

			await waitFor(() => {
				const node = getByTestId('subTypeId_search-input')
				expect(node).toBeInTheDocument()
				expect(node).toBeVisible()
				expect(node).toHaveAttribute('placeholder', 'Select')
			})
		})

		it('Note Name', async function () {
			const {
				getByTestId
			} = renderAndConfigure({ userOrganizationId })

			await waitFor(() => {
				const node = getByTestId('noteName_field-input')
				expect(node).toHaveValue('')
			})
		})

		it('Client', async function () {
			const {
				getByTestId
			} = renderAndConfigure({ userOrganizationId })

			await waitFor(() => {
				const node = getByTestId('clients_search-input')
				expect(node).toBeInTheDocument()
				expect(node).toBeVisible()
				expect(node).toHaveAttribute('placeholder', 'Select Client')
			})
		})

		describe('Program note type:', function () {
			const userOrganizationId = 3

			it('Program sub type', async function () {
				const {
					store, getByTestId
				} = renderAndConfigure({ userOrganizationId })

				store.dispatch({
					type: 'CHANGE_GROUP_NOTE_FORM_FIELD',
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
				} = renderAndConfigure({ userOrganizationId })

				store.dispatch({
					type: 'CHANGE_GROUP_NOTE_FORM_FIELD',
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
				} = renderAndConfigure({ userOrganizationId })

				store.dispatch({
					type: 'CHANGE_GROUP_NOTE_FORM_FIELD',
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
				} = renderAndConfigure({ userOrganizationId })

				store.dispatch({
					type: 'CHANGE_GROUP_NOTE_FORM_FIELD',
					payload: { name: 'subTypeId', value: 10030 }
				})

				await waitFor(() => {
					const node = getByTestId('clientProgram.endDate_field-input')
					expect(node).toHaveValue('')
				})
			})
		})

		it('Person Completing the Encounter', async function () {
			const {
				getByTestId
			} = renderAndConfigure({ userOrganizationId })

			await waitFor(() => {
				const node = getByTestId('clinicianId_selected-text')
				expect(node).toHaveTextContent(user.fullName)
			})
		})

		it('Encounter Date From', async function () {
			const {
				getByTestId
			} = renderAndConfigure({ userOrganizationId })

			await waitFor(() => {
				const node = getByTestId('fromDate_field-input')
				expect(node).toHaveValue(format(new Date(), formats.americanMediumDate))
			})
		})

		it('Time From', async function () {
			const {
				getByTestId
			} = renderAndConfigure({ userOrganizationId })

			await waitFor(() => {
				const node = getByTestId('fromTime_field-input')
				expect(node).toHaveValue(format(new Date(), formats.time2))
			})
		})

		it('Encounter Date To', async function () {
			const {
				getByTestId
			} = renderAndConfigure({ userOrganizationId })

			await waitFor(() => {
				const node = getByTestId('toDate_field-input')
				expect(node).toHaveValue('')
			})
		})

		it('Time To', async function () {
			const {
				getByTestId
			} = renderAndConfigure({ userOrganizationId })

			await waitFor(() => {
				const node = getByTestId('toTime_field-input')
				expect(node).toHaveValue('')
			})
		})

		it('Total time spent', async function () {
			const {
				getByTestId
			} = renderAndConfigure({ userOrganizationId })

			await waitFor(() => {
				const node = getByTestId('totalTime_field-input')
				expect(node).toHaveValue('')
			})
		})

		it('Range', async function () {
			const {
				getByTestId
			} = renderAndConfigure({ userOrganizationId })

			await waitFor(() => {
				const node = getByTestId('totalTime_field-input')
				expect(node).toHaveValue('')
			})
		})

		it('Units', async function () {
			const {
				getByTestId
			} = renderAndConfigure({ userOrganizationId })

			await waitFor(() => {
				const node = getByTestId('totalTime_field-input')
				expect(node).toHaveValue('')
			})
		})

		describe('Face to Face encounter note type', function () {
			it('"Encounter type" field is visible', async function () {
				const {
					store, getByTestId
				} = renderAndConfigure({ userOrganizationId })

				store.dispatch({
					type: 'CHANGE_GROUP_NOTE_FORM_FIELD',
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
				} = renderAndConfigure({ userOrganizationId })

				store.dispatch({
					type: 'CHANGE_GROUP_NOTE_FORM_FIELD',
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
				} = renderAndConfigure({ userOrganizationId })

				store.dispatch({
					type: 'CHANGE_GROUP_NOTE_FORM_FIELD',
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
				} = renderAndConfigure({ userOrganizationId })

				store.dispatch({
					type: 'CHANGE_GROUP_NOTE_FORM_FIELD',
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
				} = renderAndConfigure({ userOrganizationId })

				store.dispatch({
					type: 'CHANGE_GROUP_NOTE_FORM_FIELD',
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
				} = renderAndConfigure({ userOrganizationId })

				store.dispatch({
					type: 'CHANGE_GROUP_NOTE_FORM_FIELD',
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
				} = renderAndConfigure({ userOrganizationId })

				store.dispatch({
					type: 'CHANGE_GROUP_NOTE_FORM_FIELD',
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
				} = renderAndConfigure({ userOrganizationId })

				store.dispatch({
					type: 'CHANGE_GROUP_NOTE_FORM_FIELD',
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
			} = renderAndConfigure({ userOrganizationId })

			await waitFor(() => {
				const node = getByTestId('subjective_field-input')
				expect(node).toHaveValue('')
			})
		})

		it('Objective', async function () {
			const {
				getByTestId
			} = renderAndConfigure({ userOrganizationId })

			await waitFor(() => {
				const node = getByTestId('objective_field-input')
				expect(node).toHaveValue('')
			})
		})

		it('Assessment', async function () {
			const {
				getByTestId
			} = renderAndConfigure({ userOrganizationId })

			await waitFor(() => {
				const node = getByTestId('assessment_field-input')
				expect(node).toHaveValue('')
			})
		})

		it('Plan', async function () {
			const {
				getByTestId
			} = renderAndConfigure({ userOrganizationId })

			await waitFor(() => {
				const node = getByTestId('plan_field-input')
				expect(node).toHaveValue('')
			})
		})
	})

	describe('Editing Note. Field initialization:', function () {
		const userOrganizationId = 3

		const note1Id = 25699
		const note1 = findWhere(NoteDetails, { id: note1Id })

		const note2Id = 25700
		const note2 = findWhere(NoteDetails, { id: note2Id })

		it('Person Submitting Note', async function () {
			const {
				store, getByTestId
			} = renderAndConfigure({ noteId: note1Id, userOrganizationId })

			store.dispatch({ type: 'LOAD_NOTE_DETAILS_SUCCESS', payload: { data: note1 } })

			await waitFor(() => {
				const node = getByTestId('author_field-input')
				expect(node).toHaveValue(note1.author)
			})
		})

		it('Note Date and Time', async function () {
			const {
				store, getByTestId
			} = renderAndConfigure({ noteId: note1Id, userOrganizationId })

			store.dispatch({ type: 'LOAD_NOTE_DETAILS_SUCCESS', payload: { data: note1 } })

			await waitFor(() => {
				const node = getByTestId('noteDate_field-input')
				expect(node).toHaveValue(format(note1.noteDate, formats.longDateMediumTime12))
			})
		})

		it('Note Type', async function () {
			const {
				store, getByTestId
			} = renderAndConfigure({ noteId: note1Id,  userOrganizationId })

			store.dispatch({ type: 'LOAD_NOTE_DETAILS_SUCCESS', payload: { data: note1 } })

			await waitFor(() => {
				const node = getByTestId('subTypeId_selected-text')
				expect(node).toHaveTextContent(note1.subTypeTitle)
			})
		})

		it('Note Name', async function () {
			const {
				store, getByTestId
			} = renderAndConfigure({ noteId: note1Id,  userOrganizationId })

			store.dispatch({ type: 'LOAD_NOTE_DETAILS_SUCCESS', payload: { data: note1 } })

			await waitFor(() => {
				const node = getByTestId('noteName_field-input')
				expect(node).toHaveValue(note1.noteName)
			})
		})

		it('Clients', async function () {
			const {
				store, getByTestId
			} = renderAndConfigure({ noteId: note1Id, userOrganizationId })

			store.dispatch({ type: 'LOAD_NOTE_DETAILS_SUCCESS', payload: { data: note1 } })

			await waitFor(() => {
				for (let i = 0; i < note1.clients.length; i++) {
					const node = getByTestId(`clients_field-tag-${note1.clients[i].id}`)
					expect(node).toBeInTheDocument()
					expect(node).toBeVisible()
				}
			})
		})

		describe('Program note type:', function () {
			it('Program sub type', async function () {
				const {
					store, getByTestId
				} = renderAndConfigure({ noteId: note1Id, userOrganizationId })

				store.dispatch({ type: 'LOAD_NOTE_DETAILS_SUCCESS', payload: { data: note1 } })

				await waitFor(() => {
					const node = getByTestId('clientProgram.typeId_search-input')
					expect(node).toHaveValue(note1.clientProgram.typeTitle)
				})
			})

			it('Service Provider for Program', async function () {
				const {
					store, getByTestId
				} = renderAndConfigure({ noteId: note1Id, userOrganizationId })

				store.dispatch({ type: 'LOAD_NOTE_DETAILS_SUCCESS', payload: { data: note1 } })

				await waitFor(() => {
					const node = getByTestId('clientProgram.serviceProvider_field-input')
					expect(node).toHaveValue(note1.clientProgram.serviceProvider)
				})
			})

			it('Start Date of Program', async function () {
				const {
					store, getByTestId
				} = renderAndConfigure({ noteId: note1Id, userOrganizationId })

				store.dispatch({ type: 'LOAD_NOTE_DETAILS_SUCCESS', payload: { data: note1 } })

				await waitFor(() => {
					const node = getByTestId('clientProgram.startDate_field-input')
					expect(node).toHaveValue(format(note1.clientProgram.startDate, formats.americanMediumDate))
				})
			})

			it('End Date of Program', async function () {
				const {
					store, getByTestId
				} = renderAndConfigure({ noteId: note1Id, userOrganizationId })

				store.dispatch({ type: 'LOAD_NOTE_DETAILS_SUCCESS', payload: { data: note1 } })

				await waitFor(() => {
					const node = getByTestId('clientProgram.endDate_field-input')
					expect(node).toHaveValue(format(note1.clientProgram.endDate, formats.americanMediumDate))
				})
			})
		})

		it('Person Completing the Encounter', async function () {
			const {
				store, getByTestId
			} = renderAndConfigure({ noteId: note1Id, userOrganizationId })

			store.dispatch({ type: 'LOAD_NOTE_DETAILS_SUCCESS', payload: { data: note1 } })

			await waitFor(() => {
				const node = getByTestId('clinicianId_selected-text')
				expect(node).toHaveTextContent(note1.encounter.clinicianTitle)
			})
		})

		it('Encounter Date From', async function () {
			const {
				store, getByTestId
			} = renderAndConfigure({ noteId: note1Id, userOrganizationId })

			store.dispatch({ type: 'LOAD_NOTE_DETAILS_SUCCESS', payload: { data: note1 } })

			await waitFor(() => {
				const node = getByTestId('fromDate_field-input')
				expect(node).toHaveValue(format(note1.encounter.fromDate, formats.americanMediumDate))
			})
		})

		it('Time From', async function () {
			const {
				store, getByTestId
			} = renderAndConfigure({ noteId: note1Id, userOrganizationId })

			store.dispatch({ type: 'LOAD_NOTE_DETAILS_SUCCESS', payload: { data: note1 } })

			await waitFor(() => {
				const node = getByTestId('fromTime_field-input')
				expect(node).toHaveValue(format(note1.encounter.fromDate, formats.time2))
			})
		})

		it('Encounter Date To', async function () {
			const {
				store, getByTestId
			} = renderAndConfigure({ noteId: note2Id, userOrganizationId })

			store.dispatch({ type: 'LOAD_NOTE_DETAILS_SUCCESS', payload: { data: note2 } })

			await waitFor(() => {
				const node = getByTestId('toDate_field-input')
				expect(node).toHaveValue(format(note2.encounter.toDate, formats.americanMediumDate))
			})
		})

		it('Time To', async function () {
			const {
				store, getByTestId
			} = renderAndConfigure({ noteId: note2Id, userOrganizationId })

			store.dispatch({ type: 'LOAD_NOTE_DETAILS_SUCCESS', payload: { data: note2 } })

			await waitFor(() => {
				const node = getByTestId('toTime_field-input')
				expect(node).toHaveValue(format(note2.encounter.toDate, formats.time2))
			})
		})

		it('Total time spent', async function () {
			const {
				store, getByTestId
			} = renderAndConfigure({ noteId: note1Id, userOrganizationId })

			store.dispatch({ type: 'LOAD_NOTE_DETAILS_SUCCESS', payload: { data: note1 } })

			await waitFor(() => {
				const node = getByTestId('totalTime_field-input')
				expect(node).toHaveValue('1455')
			})
		})

		it('Range', async function () {
			const {
				store, getByTestId
			} = renderAndConfigure({ noteId: note1Id, userOrganizationId })

			store.dispatch({ type: 'LOAD_NOTE_DETAILS_SUCCESS', payload: { data: note1 } })

			await waitFor(() => {
				const node = getByTestId('range_field-input')
				expect(node).toHaveValue('1448 mins - 1462 mins')
			})
		})

		it('Units', async function () {
			const {
				store, getByTestId
			} = renderAndConfigure({ noteId: note1Id, userOrganizationId })

			store.dispatch({ type: 'LOAD_NOTE_DETAILS_SUCCESS', payload: { data: note1 } })

			await waitFor(() => {
				const node = getByTestId('units_field-input')
				expect(node).toHaveValue('97')
			})
		})

		it('Subjective', async function () {
			const {
				store, getByTestId
			} = renderAndConfigure({ noteId: note1Id, userOrganizationId })

			store.dispatch({ type: 'LOAD_NOTE_DETAILS_SUCCESS', payload: { data: note1 } })

			await waitFor(() => {
				const node = getByTestId('subjective_field-input')
				expect(node).toHaveValue(note1.subjective)
			})
		})

		it('Objective', async function () {
			const {
				store, getByTestId
			} = renderAndConfigure({ noteId: note1Id, userOrganizationId })

			store.dispatch({ type: 'LOAD_NOTE_DETAILS_SUCCESS', payload: { data: note1 } })

			await waitFor(() => {
				const node = getByTestId('objective_field-input')
				expect(node).toHaveValue(note1.objective)
			})
		})

		it('Assessment', async function () {
			const {
				store, getByTestId
			} = renderAndConfigure({ noteId: note1Id, userOrganizationId })

			store.dispatch({ type: 'LOAD_NOTE_DETAILS_SUCCESS', payload: { data: note1 } })

			await waitFor(() => {
				const node = getByTestId('assessment_field-input')
				expect(node).toHaveValue(note1.assessment)
			})
		})

		it('Plan', async function () {
			const {
				store, getByTestId
			} = renderAndConfigure({ noteId: note1Id, userOrganizationId })

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