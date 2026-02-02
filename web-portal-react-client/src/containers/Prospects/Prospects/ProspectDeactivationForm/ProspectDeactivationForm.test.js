import React from 'react'

import { render } from 'lib/test-utils'

import {
	format, formats
} from 'lib/utils/DateUtils'

import ProspectDeactivationForm from './ProspectDeactivationForm'

describe('<ProspectDeactivationForm>:', function () {
	describe('All fields are visible:', function () {
		function testFieldToBeVisible(title, name) {
			it(title, () => {
				let { getByTestId } = render(<ProspectDeactivationForm/>)
				const node = getByTestId(`${name}_field`)
				expect(node).toBeInTheDocument()
				expect(node).toBeVisible()
			})
		}

		testFieldToBeVisible('Exit date', 'deactivationDate')
		testFieldToBeVisible('Reason', 'deactivationReason')
		testFieldToBeVisible('Comment', 'comment')
	})

	describe('Fields marked as required:', function () {
		function testFieldToBeRequired(title, name) {
			it(title, async function () {
				let { getByTestId } = render(<ProspectDeactivationForm/>)
				const node = getByTestId(`${name}_field-label`)
				expect(String(node.innerHTML).endsWith('*')).toBeTruthy()
			})
		}

		testFieldToBeRequired('Exit date', 'deactivationDate')
		testFieldToBeRequired('Reason', 'deactivationReason')
	})

	describe('Default Field values:', function () {
		function testTextFieldToHaveValue(title, name, value) {
			it(`"${title}" field initialized correctly`, async () => {
				let {
					getByTestId
				} = render(<ProspectDeactivationForm/>)

				const node = getByTestId(`${name}_field-input`)
				expect(node).toHaveValue(value)
			})
		}

		function testSelectFieldToHaveValue(title, name, value) {
			it(`"${title}" field initialized correctly`, async () => {
				let {
					getByTestId
				} = render(<ProspectDeactivationForm/>)
				const node = getByTestId(`${name}_selected-text`)
				expect(node).toHaveTextContent(value)
			})
		}

		testTextFieldToHaveValue('Exit date', 'deactivationDate', format(Date.now(), formats.americanMediumDate))
		testSelectFieldToHaveValue('Reason', 'deactivationReason', 'Select')
		testTextFieldToHaveValue('Comment', 'comment', '')
	})
})