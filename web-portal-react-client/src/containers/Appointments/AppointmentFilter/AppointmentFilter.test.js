import React from 'react'

import { rest } from 'msw'
import { setupServer } from 'msw/node'
import { waitFor, render } from 'lib/test-utils'

import {
	CONTACT_STATUSES
} from 'lib/Constants'

import { noop } from 'lib/utils/FuncUtils'

import Response from 'lib/mock/server/Response'

import AppointmentFilter from './AppointmentFilter'

const {
	ACTIVE
} = CONTACT_STATUSES

const BASE_URL = 'https://dev.simplyconnect.me/web-portal-mock-backend'

const data = {
	creatorIds: [1, 2, 3],
	clientIds: [1, 2, 3],
	clientStatuses: [ACTIVE],
	serviceProviderIds: [4, 5, 6],
	types: [],
	statuses: [],
	hasNoServiceProviders: false,
	isExternalProviderServiceProvider: false,
	clientsWithAccessibleAppointments: true,
}

const organizationId = 3

const server = setupServer(
	rest.get(`${BASE_URL}/directory/client-statuses`, (req, res, ctx) => {
		return res(
			ctx.json(Response.success([
				{ name: "ALL", title: "All" },
				{ name: "ACTIVE", title: "Active" },
				{ name: "INACTIVE", title: "Inactive" }
			]))
		)
	}),
	rest.get(`${BASE_URL}/authorized-directory/appointments/types`, (req, res, ctx) => {
		return res(
			ctx.json(Response.success([
				{ name: "FOLLOW_UP", title: "A follow up visit" },
				{ name: "CHECK_UP", title: "Check-up" },
				{ name: "EMERGENCY", title: "Emergency appointment" },
				{ name: "POST_OP", title: "Post op" },
				{ name: "ROUTINE", title: "Routine appointment" },
				{ name: "OTHER", title: "Other" }
			]))
		)
	}),
	rest.get(`${BASE_URL}/authorized-directory/appointments/statuses`, (req, res, ctx) => {
		return res(
			ctx.json(Response.success([
				{ name: "PLANNED", title: "Planned" },
				{ name: "RESCHEDULED", title: "Rescheduled" },
				{ name: "TRIAGED", title: "Triaged" },
				{ name: "COMPLETED", title: "Completed" },
				{ name: "CANCELLED", title: "Cancelled" },
				{ name: "ENTERED_IN_ERROR", title: "Entered in Error" }
			]))
		)
	}),
)

function changeField(name, value) {
	data[name] = value
}

function changeFields(changes) {
	Object.assign(data, changes)
}

describe('<AppointmentFilter>:', () => {
	beforeAll(() => {
		server.listen()
	})

	it('is visible on UI', async () => {
		let { findByTestId } = render(
			<AppointmentFilter
				data={data}
				reset={noop}
				apply={noop}
				isSaved={noop}
				organizationId={organizationId}
				changeField={changeField}
				changeFields={changeFields}
			/>
		)

		const node = await findByTestId('appointmentFilter')

		expect(node).toBeInTheDocument()
		expect(node).toBeVisible()
	})

	it('"Creator" field initialized correctly', async () => {
		const {
			getByTestId
		} = render(
			<AppointmentFilter
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
			const node = getByTestId('creatorIds_search-input')

			expect(node).toBeInTheDocument()
			expect(node).toBeVisible()
			expect(node).toHaveTextContent('')
			expect(node).toHaveAttribute('placeholder', 'Select')
		})
	})

	it('"Service Provider" field initialized correctly', async () => {
		const {
			getByTestId
		} = render(
			<AppointmentFilter
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
			const node = getByTestId('serviceProviderIds_search-input')

			expect(node).toBeInTheDocument()
			expect(node).toBeVisible()
			expect(node).toHaveTextContent('')
			expect(node).toHaveAttribute('placeholder', 'Select')
		})
	})

	it('"Client" field initialized correctly', async () => {
		const {
			getByTestId
		} = render(
			<AppointmentFilter
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
			const node = getByTestId('clientIds_search-input')

			expect(node).toBeInTheDocument()
			expect(node).toBeVisible()
			expect(node).toHaveTextContent('')
		})
	})

	it('"Client Status" field initialized correctly', async () => {
		const {
			getByTestId
		} = render(
			<AppointmentFilter
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
			const node = getByTestId('clientStatuses_selected-text')
			expect(node).toHaveTextContent('Active')
		})
	})

	it('"Types" field initialized correctly', async () => {
		const {
			getByTestId
		} = render(
			<AppointmentFilter
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
			const node = getByTestId('types_selected-text')
			expect(node).toHaveTextContent('All')
		})
	})

	it('"Statuses" field initialized correctly', async () => {
		const {
			getByTestId
		} = render(
			<AppointmentFilter
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
			expect(node).toHaveTextContent('Planned, Rescheduled, Triaged, Completed')
		})
	})

	afterAll(() => {
		server.close()
	})
})