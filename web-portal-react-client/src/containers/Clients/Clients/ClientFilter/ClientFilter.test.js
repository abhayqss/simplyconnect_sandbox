import React from 'react'

import {
	first,
	findWhere
} from 'underscore'

import { rest } from 'msw'
import { Provider } from 'react-redux'
import { setupServer } from 'msw/node'
import { createBrowserHistory } from 'history'
import { waitFor, render } from 'lib/test-utils'

import configureStore from 'redux/configureStore'

import Response from 'lib/mock/server/Response'

import {
	format, formats
} from 'lib/utils/DateUtils'

import {
	User,
	Gender,
	ClientStatus,
	ClientPharmacyName,
	NetworkAggregatedName
} from 'lib/mock/db/DB'

import ClientFilter from './ClientFilter'

const BASE_URL = 'https://dev.simplyconnect.me/web-portal-mock-backend'

const server = setupServer(
	rest.get(`${BASE_URL}/directory/client-statuses`, (req, res, ctx) => {
		return res(
			ctx.json(Response.success(ClientStatus))
		)
	}),
	rest.get(`${BASE_URL}/directory/genders`, (req, res, ctx) => {
		return res(
			ctx.json(Response.success(Gender))
		)
	}),
	rest.get(`${BASE_URL}/authorized-directory/insurance/network-aggregated-names`, (req, res, ctx) => {
		return res(
			ctx.json(Response.success(NetworkAggregatedName))
		)
	}),
	rest.get(`${BASE_URL}/authorized-directory/client-pharmacy-names`, (req, res, ctx) => {
		return res(
			ctx.json(Response.success(ClientPharmacyName))
		)
	})
)

function renderAndConfigure({ userOrganizationId = 3 }) {
	const {
		store, ...config
	} = render(
		<ClientFilter/>
	)

	const user = findWhere(User, { organizationId: userOrganizationId })
	store.dispatch({ type: 'LOGIN_SUCCESS', payload: user })

	return { store, ...config }
}

describe('<ClientFilter>:', () => {
	beforeAll(() => {
		server.listen()
	})

	describe('All fields are visible on UI:', function () {
		const userOrganizationId = 3

		it('First name', () => {
			const { getByTestId } = renderAndConfigure({ userOrganizationId })

			const node = getByTestId('firstName_field')
			expect(node).toBeInTheDocument()
			expect(node).toBeVisible()
		})

		it('Last name', () => {
			const { getByTestId } = renderAndConfigure({ userOrganizationId })

			const node = getByTestId('lastName_field')
			expect(node).toBeInTheDocument()
			expect(node).toBeVisible()
		})

		it('Gender', () => {
			const { getByTestId } = renderAndConfigure({ userOrganizationId })

			const node = getByTestId('genderId_field')
			expect(node).toBeInTheDocument()
			expect(node).toBeVisible()
		})

		it('Date of Birth', () => {
			const { getByTestId } = renderAndConfigure({ userOrganizationId })

			const node = getByTestId('birthDate_field')
			expect(node).toBeInTheDocument()
			expect(node).toBeVisible()
		})

		it('SSN', () => {
			const { getByTestId } = renderAndConfigure({ userOrganizationId })

			const node = getByTestId('ssnLast4_field')
			expect(node).toBeInTheDocument()
			expect(node).toBeVisible()
		})

		it('Record status', () => {
			const { getByTestId } = renderAndConfigure({ userOrganizationId })

			const node = getByTestId('recordStatuses_field')
			expect(node).toBeInTheDocument()
			expect(node).toBeVisible()
		})

		it('Primary Care Physician', () => {
			const { getByTestId } = renderAndConfigure({ userOrganizationId })

			const node = getByTestId('primaryCarePhysician_field')
			expect(node).toBeInTheDocument()
			expect(node).toBeVisible()
		})

		it('Insurance Network', () => {
			const { getByTestId } = renderAndConfigure({ userOrganizationId })

			const node = getByTestId('insuranceNetworkAggregatedName_field')
			expect(node).toBeInTheDocument()
			expect(node).toBeVisible()
		})

		it('Unit #', () => {
			const { getByTestId } = renderAndConfigure({ userOrganizationId })

			const node = getByTestId('unit_field')
			expect(node).toBeInTheDocument()
			expect(node).toBeVisible()
		})

		it('Currently admitted', () => {
			const { getByTestId } = renderAndConfigure({ userOrganizationId })

			const node = getByTestId('isAdmitted_field')
			expect(node).toBeInTheDocument()
			expect(node).toBeVisible()
		})

		it('Pharmacy', () => {
			const { getByTestId } = renderAndConfigure({ userOrganizationId })

			const node = getByTestId('pharmacyNames_field')
			expect(node).toBeInTheDocument()
			expect(node).toBeVisible()
		})

		it('Medicaid #', () => {
			const { getByTestId } = renderAndConfigure({ userOrganizationId })

			const node = getByTestId('medicaidNumber_field')
			expect(node).toBeInTheDocument()
			expect(node).toBeVisible()
		})

		it('Medicare #', () => {
			const { getByTestId } = renderAndConfigure({ userOrganizationId })

			const node = getByTestId('medicareNumber_field')
			expect(node).toBeInTheDocument()
			expect(node).toBeVisible()
		})

		it('Clear button', () => {
			const { getByTestId } = renderAndConfigure({ userOrganizationId })

			const node = getByTestId('clear-btn')
			expect(node).toBeInTheDocument()
			expect(node).toBeVisible()
		})

		it('Apply button', () => {
			const { getByTestId } = renderAndConfigure({ userOrganizationId })

			const node = getByTestId('apply-btn')
			expect(node).toBeInTheDocument()
			expect(node).toBeVisible()
		})
	})

	describe('Default Initialization:', function () {
		const userOrganizationId = 3

		it('"First name" field initialized correctly', async () => {
			const { getByTestId } = renderAndConfigure({ userOrganizationId })

			await waitFor(() => {
				const node = getByTestId('firstName_field-input')
				expect(node).toHaveValue('')
			})
		})

		it('"Last name" field initialized correctly', async () => {
			const { getByTestId } = renderAndConfigure({ userOrganizationId })

			await waitFor(() => {
				const node = getByTestId('lastName_field-input')
				expect(node).toHaveValue('')
			})
		})

		it('"Gender" field initialized correctly', async () => {
			const { getByTestId } = renderAndConfigure({ userOrganizationId })

			await waitFor(() => {
				const node = getByTestId('genderId_selected-text')
				expect(node).toHaveTextContent('Gender')
			})
		})

		it('"Date of Birth" field initialized correctly', async () => {
			const { getByTestId } = renderAndConfigure({ userOrganizationId })

			await waitFor(() => {
				const node = getByTestId('birthDate_field-input')
				expect(node).toHaveValue('')
				expect(node).toHaveAttribute('placeholder', 'Select date')
			})
		})

		it('"SSN" field initialized correctly', async () => {
			const { getByTestId } = renderAndConfigure({ userOrganizationId })

			await waitFor(() => {
				const node = getByTestId('ssnLast4_field-input')
				expect(node).toHaveValue('')
				expect(node).toHaveAttribute('placeholder', 'Last 4 digits')
			})
		})

		it('"Record status" field initialized correctly', async () => {
			const { getByTestId } = renderAndConfigure({ userOrganizationId })

			await waitFor(() => {
				const node = getByTestId('recordStatuses_selected-text')
				expect(node).toHaveTextContent('Active')
			})
		})

		it('"Primary Care Physician" field initialized correctly', async () => {
			const { getByTestId } = renderAndConfigure({ userOrganizationId })

			await waitFor(() => {
				const node = getByTestId('primaryCarePhysician_field-input')
				expect(node).toHaveValue('')
				expect(node).toHaveAttribute('placeholder', 'Primary Care Physician')
			})
		})

		it('"Insurance Network" field initialized correctly', async () => {
			const { getByTestId } = renderAndConfigure({ userOrganizationId })

			await waitFor(() => {
				const node = getByTestId('insuranceNetworkAggregatedName_search-input')
				expect(node).toHaveValue('')
				expect(node).toHaveAttribute('placeholder', 'Insurance Network')
			})
		})

		it('"Unit #" field initialized correctly', async () => {
			const { getByTestId } = renderAndConfigure({ userOrganizationId })

			await waitFor(() => {
				const node = getByTestId('unit_field-input')
				expect(node).toHaveValue('')
				expect(node).toHaveAttribute('placeholder', 'Unit #')
			})
		})

		it('"Currently admitted" field initialized correctly', async () => {
			const { queryByTestId } = renderAndConfigure({ userOrganizationId })

			await waitFor(() => {
				const node = queryByTestId('isAdmitted_field-check-mark')
				expect(node).toBeNull()
			})
		})

		it('"Pharmacy" field initialized correctly', async () => {
			const { getByTestId } = renderAndConfigure({ userOrganizationId })

			await waitFor(() => {
				const node = getByTestId('pharmacyNames_search-input')
				expect(node).toHaveValue('')
				expect(node).toHaveAttribute('placeholder', 'Pharmacy')
			})
		})

		it('"Medicaid #" field initialized correctly', async () => {
			const { getByTestId } = renderAndConfigure({ userOrganizationId })

			await waitFor(() => {
				const node = getByTestId('medicaidNumber_field-input')
				expect(node).toHaveValue('')
				expect(node).toHaveAttribute('placeholder', 'Medicaid #')
			})
		})

		it('"Medicare #" field initialized correctly', async () => {
			const { getByTestId } = renderAndConfigure({ userOrganizationId })

			await waitFor(() => {
				const node = getByTestId('medicareNumber_field-input')
				expect(node).toHaveValue('')
				expect(node).toHaveAttribute('placeholder', 'Medicare #')
			})
		})
	})

	describe('Initialization with data:', function () {
		const userOrganizationId = 3

		const filter = {
			organizationId: 3,
			communityIds: [3,83666,4,107809,97037,1,97042,6,83667,97115,5,2,7,107793],
			firstName: 'Boris',
			lastName: 'Johnson',
			genderId: 3,
			birthDate: format(Date.now(), formats.americanMediumDate),
			ssnLast4: '1234',
			recordStatuses: ['INACTIVE'],
			primaryCarePhysician: 'Alan Piterson',
			insuranceNetworkAggregatedName: 'Advantra',
			unit: 43,
			isAdmitted: true,
			pharmacyNames: ['Omni DX', 'Omnicare'],
			medicaidNumber: 2413124,
			medicareNumber: 124123
		}

		it('"First name" field initialized correctly', async () => {
			const { store, getByTestId } = renderAndConfigure({ userOrganizationId })

			store.dispatch({ type: 'CHANGE_CLIENT_LIST_FILTER', payload: { changes: filter } })

			await waitFor(() => {
				const node = getByTestId('firstName_field-input')
				expect(node).toHaveValue(filter.firstName)
			})
		})

		it('"Last name" field initialized correctly', async () => {
			const { store, getByTestId } = renderAndConfigure({ userOrganizationId })

			store.dispatch({ type: 'CHANGE_CLIENT_LIST_FILTER', payload: { changes: filter } })

			await waitFor(() => {
				const node = getByTestId('lastName_field-input')
				expect(node).toHaveValue(filter.lastName)
			})
		})

		it('"Gender" field initialized correctly', async () => {
			const { store, getByTestId } = renderAndConfigure({ userOrganizationId })

			store.dispatch({ type: 'CHANGE_CLIENT_LIST_FILTER', payload: { changes: filter } })

			const gender = findWhere(Gender, { id: filter.genderId })

			await waitFor(() => {
				const node = getByTestId('genderId_selected-text')
				expect(node).toHaveTextContent(gender.title)
			})
		})

		it('"Date of Birth" field initialized correctly', async () => {
			const { store, getByTestId } = renderAndConfigure({ userOrganizationId })

			store.dispatch({ type: 'CHANGE_CLIENT_LIST_FILTER', payload: { changes: filter } })

			await waitFor(() => {
				const node = getByTestId('birthDate_field-input')
				expect(node).toHaveValue(filter.birthDate)
			})
		})

		it('"SSN" field initialized correctly', async () => {
			const { store, getByTestId } = renderAndConfigure({ userOrganizationId })

			store.dispatch({ type: 'CHANGE_CLIENT_LIST_FILTER', payload: { changes: filter } })

			await waitFor(() => {
				const node = getByTestId('ssnLast4_field-input')
				expect(node).toHaveValue(filter.ssnLast4)
			})
		})

		it('"Record status" field initialized correctly', async () => {
			const { store, getByTestId } = renderAndConfigure({ userOrganizationId })

			store.dispatch({ type: 'CHANGE_CLIENT_LIST_FILTER', payload: { changes: filter } })

			const status = findWhere(ClientStatus, { name: first(filter.recordStatuses) })

			await waitFor(() => {
				const node = getByTestId('recordStatuses_selected-text')
				expect(node).toHaveTextContent(status.title)
			})
		})

		it('"Primary Care Physician" field initialized correctly', async () => {
			const { store, getByTestId } = renderAndConfigure({ userOrganizationId })

			store.dispatch({ type: 'CHANGE_CLIENT_LIST_FILTER', payload: { changes: filter } })

			await waitFor(() => {
				const node = getByTestId('primaryCarePhysician_field-input')
				expect(node).toHaveValue(filter.primaryCarePhysician)
			})
		})

		it('"Insurance Network" field initialized correctly', async () => {
			const { store, getByTestId } = renderAndConfigure({ userOrganizationId })

			store.dispatch({ type: 'CHANGE_CLIENT_LIST_FILTER', payload: { changes: filter } })

			await waitFor(() => {
				const node = getByTestId('insuranceNetworkAggregatedName_selected-text')
				expect(node).toHaveTextContent(filter.insuranceNetworkAggregatedName)
			})
		})

		it('"Unit #" field initialized correctly', async () => {
			const { store, getByTestId } = renderAndConfigure({ userOrganizationId })

			store.dispatch({ type: 'CHANGE_CLIENT_LIST_FILTER', payload: { changes: filter } })

			await waitFor(() => {
				const node = getByTestId('unit_field-input')
				expect(node).toHaveValue(String(filter.unit))
			})
		})

		it('"Currently admitted" field initialized correctly', async () => {
			const { store, queryByTestId } = renderAndConfigure({ userOrganizationId })

			store.dispatch({ type: 'CHANGE_CLIENT_LIST_FILTER', payload: { changes: filter } })

			await waitFor(() => {
				const node = queryByTestId('isAdmitted_field-check-mark')

				if (filter.isAdmitted) {
					expect(node).toBeInTheDocument()
					expect(node).toBeVisible()
				} else {
					expect(node).toBeNull()
				}
			})
		})

		it('"Pharmacy" field initialized correctly', async () => {
			const { store, getByTestId } = renderAndConfigure({ userOrganizationId })

			store.dispatch({ type: 'CHANGE_CLIENT_LIST_FILTER', payload: { changes: filter } })

			await waitFor(() => {
				const node = getByTestId('pharmacyNames_selected-text')
				expect(node).toHaveTextContent(filter.pharmacyNames.join(', '))
			})
		})

		it('"Medicaid #" field initialized correctly', async () => {
			const { store, getByTestId } = renderAndConfigure({ userOrganizationId })

			store.dispatch({ type: 'CHANGE_CLIENT_LIST_FILTER', payload: { changes: filter } })

			await waitFor(() => {
				const node = getByTestId('medicaidNumber_field-input')
				expect(node).toHaveValue(String(filter.medicaidNumber))
			})
		})

		it('"Medicare #" field initialized correctly', async () => {
			const { store, getByTestId } = renderAndConfigure({ userOrganizationId })

			store.dispatch({ type: 'CHANGE_CLIENT_LIST_FILTER', payload: { changes: filter } })

			await waitFor(() => {
				const node = getByTestId('medicareNumber_field-input')
				expect(node).toHaveValue(String(filter.medicareNumber))
			})
		})
	})

	afterAll(() => {
		server.close()
	})
})