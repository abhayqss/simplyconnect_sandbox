import React from 'react'

import { findWhere } from 'underscore'

import { rest } from 'msw'
import { setupServer } from 'msw/node'
import { waitFor, render } from 'lib/test-utils'

import { noop } from 'lib/utils/FuncUtils'

import {
	format,
	formats
} from 'lib/utils/DateUtils'

import Response from 'lib/mock/server/Response'

import {
	Gender,
	ProspectStatus
} from 'lib/mock/db/DB'

import ProspectFilter from './ProspectFilter'

const BASE_URL = 'https://dev.simplyconnect.me/web-portal-mock-backend'

const server = setupServer(
	rest.get(`${BASE_URL}/directory/genders`, (req, res, ctx) => {
		return res(
			ctx.json(Response.success(Gender))
		)
	}),
	rest.get(`${BASE_URL}/directory/prospect-statuses`, (req, res, ctx) => {
		return res(
			ctx.json(Response.success(ProspectStatus))
		)
	})
)

function Filter(defaultData) {
	const data = { ...defaultData }

	return {
		getData() {
			return data
		},
		changeField(name, value) {
			data[name] = value
		},
		changeFields(changes) {
			Object.assign(data, changes)
		}
	}
}

function testFieldToBeVisible(title, name) {
	const organizationId = 3

	return function (defaultData) {
		const filter = new Filter(defaultData)

		it(title, () => {
			const { getByTestId } = render(
				<ProspectFilter
					data={filter.getData()}
					reset={noop}
					apply={noop}
					isSaved={noop}
					organizationId={organizationId}
					changeField={filter.changeField}
					changeFields={filter.changeFields}
				/>
			)

			const node = getByTestId(`${name}_field`)
			expect(node).toBeInTheDocument()
			expect(node).toBeVisible()
		})
	}
}

function testTextFieldToHaveValue(title, name, value) {
	const organizationId = 3

	return function (defaultData) {
		const filter = new Filter(defaultData)

		it(`"${title}" field initialized correctly`, async () => {
			const { getByTestId } = render(
				<ProspectFilter
					data={filter.getData()}
					reset={noop}
					apply={noop}
					isSaved={noop}
					organizationId={organizationId}
					changeField={filter.changeField}
					changeFields={filter.changeFields}
				/>
			)

			await waitFor(() => {
				const node = getByTestId(`${name}_field-input`)
				expect(node).toHaveValue(value)
			})
		})
	}
}

function testSelectFieldToHaveValue(title, name, value) {
	const organizationId = 3

	return function (defaultData) {
		const filter = new Filter(defaultData)

		it(`"${title}" field initialized correctly`, async () => {
			const { getByTestId } = render(
				<ProspectFilter
					data={filter.getData()}
					reset={noop}
					apply={noop}
					isSaved={noop}
					organizationId={organizationId}
					changeField={filter.changeField}
					changeFields={filter.changeFields}
				/>
			)

			await waitFor(() => {
				const node = getByTestId(`${name}_selected-text`)
				expect(node).toHaveTextContent(value)
			})
		})
	}
}


describe('<ProspectFilter>:', function () {
	beforeAll(() => {
		server.listen()
	})

	describe('All fields are visible on UI:', function () {
		const defaultData = {
			firstName: null,
			lastName: null,
			genderId: null,
			birthDate: null,
			prospectStatus: null
		}

		testFieldToBeVisible('First Name', 'firstName')(defaultData)
		testFieldToBeVisible('Last Name', 'lastName')(defaultData)
		testFieldToBeVisible('Gender', 'genderId')(defaultData)
		testFieldToBeVisible('Date of Birth', 'birthDate')(defaultData)
		testFieldToBeVisible('Prospect status', 'prospectStatus')(defaultData)
	})

	describe('Default Initialization:', function () {
		const defaultData = {
			firstName: null,
			lastName: null,
			genderId: null,
			birthDate: null,
			prospectStatus: null
		}

		testTextFieldToHaveValue('First Name', 'firstName', '')(defaultData)
		testTextFieldToHaveValue('Last Name', 'lastName', '')(defaultData)
		testSelectFieldToHaveValue('Gender', 'genderId', 'Select')(defaultData)
		testTextFieldToHaveValue('Date of Birth', 'birthDate', '')(defaultData)
		testSelectFieldToHaveValue('Prospect status', 'prospectStatus', 'Active')(defaultData)
	})

	describe('Initialization with data:', function () {
		const defaultData = {
			firstName: 'Boris',
			lastName: 'Johnson',
			genderId: 2,
			birthDate: 1669496400000,
			prospectStatus: 'ACTIVE'
		}

		const gender = findWhere(Gender, { id: defaultData.genderId })
		const status = findWhere(ProspectStatus, { name: defaultData.prospectStatus })

		testTextFieldToHaveValue('First Name', 'firstName', defaultData.firstName)(defaultData)
		testTextFieldToHaveValue('Last Name', 'lastName', defaultData.lastName)(defaultData)
		testSelectFieldToHaveValue('Gender', 'genderId', gender.title)(defaultData)
		testTextFieldToHaveValue('Date of Birth', 'birthDate', format(defaultData.birthDate, formats.americanMediumDate))(defaultData)
		testSelectFieldToHaveValue('Prospect status', 'prospectStatus', status.title)(defaultData)
	})
})