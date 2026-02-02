import React from 'react'

import {
	map,
	filter
} from 'underscore'

import { rest } from 'msw'
import { setupServer } from 'msw/node'
import { waitFor, render } from 'lib/test-utils'

import { noop } from 'lib/utils/FuncUtils'

import Response from 'lib/mock/server/Response'

import {
	SystemRole,
	ContactStatus
} from 'lib/mock/db/DB'

import ContactFilter from './ContactFilter'

const BASE_URL = 'https://dev.simplyconnect.me/web-portal-mock-backend'

const data = {
	firstName: null,
	lastName: null,
	login: null,
	systemRoleIds: [],
	statuses: []
}

const server = setupServer(
	rest.get(`${BASE_URL}/directory/system-roles`, (req, res, ctx) => {
		return res(
			ctx.json(Response.success(SystemRole))
		)
	}),
	rest.get(`${BASE_URL}/directory/employee-statuses`, (req, res, ctx) => {
		return res(
			ctx.json(Response.success(ContactStatus))
		)
	})
)

function changeField(name, value) {
	data[name] = value
}

function changeFields(changes) {
	Object.assign(data, changes)
}

describe('<ContactFilter>:', () => {
	beforeAll(() => {
		server.listen()
	})

	describe('All fields are visible on UI:', function () {
		const organizationId = 3

		it('First name', () => {
			const { getByTestId } = render(
				<ContactFilter
					data={data}
					reset={noop}
					apply={noop}
					isSaved={noop}
					organizationId={organizationId}
					changeField={changeField}
					changeFields={changeFields}
				/>
			)

			const node = getByTestId('firstName_field')
			expect(node).toBeInTheDocument()
			expect(node).toBeVisible()
		})

		it('Last name', () => {
			const { getByTestId } = render(
				<ContactFilter
					data={data}
					reset={noop}
					apply={noop}
					isSaved={noop}
					organizationId={organizationId}
					changeField={changeField}
					changeFields={changeFields}
				/>
			)

			const node = getByTestId('lastName_field')
			expect(node).toBeInTheDocument()
			expect(node).toBeVisible()
		})

		it('Login', () => {
			const { getByTestId } = render(
				<ContactFilter
					data={data}
					reset={noop}
					apply={noop}
					isSaved={noop}
					organizationId={organizationId}
					changeField={changeField}
					changeFields={changeFields}
				/>
			)

			const node = getByTestId('email_field')
			expect(node).toBeInTheDocument()
			expect(node).toBeVisible()
		})

		it('System role', () => {
			const { getByTestId } = render(
				<ContactFilter
					data={data}
					reset={noop}
					apply={noop}
					isSaved={noop}
					organizationId={organizationId}
					changeField={changeField}
					changeFields={changeFields}
				/>
			)

			const node = getByTestId('systemRoleIds_field')
			expect(node).toBeInTheDocument()
			expect(node).toBeVisible()
		})

		it('Status', () => {
			const { getByTestId } = render(
				<ContactFilter
					data={data}
					reset={noop}
					apply={noop}
					isSaved={noop}
					organizationId={organizationId}
					changeField={changeField}
					changeFields={changeFields}
				/>
			)

			const node = getByTestId('statuses_field')
			expect(node).toBeInTheDocument()
			expect(node).toBeVisible()
		})

		it('Clear button', () => {
			const { getByTestId } = render(
				<ContactFilter
					data={data}
					reset={noop}
					apply={noop}
					isSaved={noop}
					organizationId={organizationId}
					changeField={changeField}
					changeFields={changeFields}
				/>
			)

			const node = getByTestId('clear-btn')
			expect(node).toBeInTheDocument()
			expect(node).toBeVisible()
		})

		it('Apply button', () => {
			const { getByTestId } = render(
				<ContactFilter
					data={data}
					reset={noop}
					apply={noop}
					isSaved={noop}
					organizationId={organizationId}
					changeField={changeField}
					changeFields={changeFields}
				/>
			)

			const node = getByTestId('apply-btn')
			expect(node).toBeInTheDocument()
			expect(node).toBeVisible()
		})
	})

	describe('Default Initialization:', function () {
		const organizationId = 3

		it('"First name" field initialized correctly', async () => {
			const {
				getByTestId
			} = render(
				<ContactFilter
					data={data}
					reset={noop}
					apply={noop}
					isSaved={noop}
					organizationId={organizationId}
					changeField={changeField}
					changeFields={changeFields}
				/>
			)

			await waitFor(() => {
				const node = getByTestId('firstName_field-input')
				expect(node).toHaveTextContent('')
			})
		})

		it('"Last name" field initialized correctly', async () => {
			const {
				getByTestId
			} = render(
				<ContactFilter
					data={data}
					reset={noop}
					apply={noop}
					isSaved={noop}
					organizationId={organizationId}
					changeField={changeField}
					changeFields={changeFields}
				/>
			)

			await waitFor(() => {
				const node = getByTestId('lastName_field-input')
				expect(node).toHaveTextContent('')
			})
		})

		it('"Login" field initialized correctly', async () => {
			const {
				getByTestId
			} = render(
				<ContactFilter
					data={data}
					reset={noop}
					apply={noop}
					isSaved={noop}
					organizationId={organizationId}
					changeField={changeField}
					changeFields={changeFields}
				/>
			)

			await waitFor(() => {
				const node = getByTestId('email_field-input')
				expect(node).toHaveTextContent('')
			})
		})

		it('"System role" field initialized correctly', async () => {
			const {
				getByTestId
			} = render(
				<ContactFilter
					data={data}
					reset={noop}
					apply={noop}
					isSaved={noop}
					organizationId={organizationId}
					changeField={changeField}
					changeFields={changeFields}
				/>
			)

			await waitFor(() => {
				const node = getByTestId('systemRoleIds_selected-text')
				expect(node).toHaveTextContent('All')
			})
		})

		it('"Status" field initialized correctly', async () => {
			const {
				getByTestId
			} = render(
				<ContactFilter
					data={data}
					reset={noop}
					apply={noop}
					isSaved={noop}
					organizationId={organizationId}
					changeField={changeField}
					changeFields={changeFields}
				/>
			)

			await waitFor(() => {
				const node = getByTestId('statuses_selected-text')
				expect(node).toHaveTextContent('Active, Pending')
			})
		})
	})

	describe('Initialization with data:', function () {
		const organizationId = 3
		const data = {
			firstName: 'Boris',
			lastName: 'Johnson',
			email: 'bjohnson@gmail.com',
			systemRoleIds: [22, 19],
			statuses: ['CONFIRMED', 'INACTIVE']
		}

		it('"First name" field initialized correctly', async () => {
			const {
				getByTestId
			} = render(
				<ContactFilter
					data={data}
					reset={noop}
					apply={noop}
					isSaved={noop}
					organizationId={organizationId}
					changeField={changeField}
					changeFields={changeFields}
				/>
			)

			await waitFor(() => {
				const node = getByTestId('firstName_field-input')
				expect(node).toHaveValue(data.firstName)
			})
		})

		it('"Last name" field initialized correctly', async () => {
			const {
				getByTestId
			} = render(
				<ContactFilter
					data={data}
					reset={noop}
					apply={noop}
					isSaved={noop}
					organizationId={organizationId}
					changeField={changeField}
					changeFields={changeFields}
				/>
			)

			await waitFor(() => {
				const node = getByTestId('lastName_field-input')
				expect(node).toHaveValue(data.lastName)
			})
		})

		it('"Login" field initialized correctly', async () => {
			const {
				getByTestId
			} = render(
				<ContactFilter
					data={data}
					reset={noop}
					apply={noop}
					isSaved={noop}
					organizationId={organizationId}
					changeField={changeField}
					changeFields={changeFields}
				/>
			)

			await waitFor(() => {
				const node = getByTestId('email_field-input')
				expect(node).toHaveValue(data.email)
			})
		})

		it('"System role" field initialized correctly', async () => {
			const {
				getByTestId
			} = render(
				<ContactFilter
					data={data}
					reset={noop}
					apply={noop}
					isSaved={noop}
					organizationId={organizationId}
					changeField={changeField}
					changeFields={changeFields}
				/>
			)

			const value = map(
				filter(SystemRole, o => data.systemRoleIds.includes(o.id)),
				o => o.title
			).join(', ')

			await waitFor(() => {
				const node = getByTestId('systemRoleIds_selected-text')
				expect(node).toHaveTextContent(value)
			})
		})

		it('"Status" field initialized correctly', async () => {
			const {
				getByTestId
			} = render(
				<ContactFilter
					data={data}
					reset={noop}
					apply={noop}
					isSaved={noop}
					organizationId={organizationId}
					changeField={changeField}
					changeFields={changeFields}
				/>
			)

			const value = map(
				filter(ContactStatus, o => data.statuses.includes(o.name)),
				o => o.title
			).join(', ')

			await waitFor(() => {
				const node = getByTestId('statuses_selected-text')
				expect(node).toHaveTextContent(value)
			})
		})
	})

	afterAll(() => {
		server.close()
	})
})