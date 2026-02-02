import React from 'react'

import { render } from 'lib/test-utils'

import {
	format, formats
} from 'lib/utils/DateUtils'

import ProspectActivationForm from './ProspectActivationForm'

describe('<ProspectActivationForm>:', function () {
	describe('All fields are visible:', function () {
		function testFieldToBeVisible(title, name) {
			it(title, () => {
				let { getByTestId } = render(<ProspectActivationForm/>)
				const node = getByTestId(`${name}_field`)
				expect(node).toBeInTheDocument()
				expect(node).toBeVisible()
			})
		}

		testFieldToBeVisible('Activate date', 'activationDate')
		testFieldToBeVisible('Comment', 'comment')
	})

	describe('Fields marked as required:', function () {
		function testFieldToBeRequired(title, name) {
			it(title, async function () {
				let { getByTestId } = render(<ProspectActivationForm/>)
				const node = getByTestId(`${name}_field-label`)
				expect(String(node.innerHTML).endsWith('*')).toBeTruthy()
			})
		}

		testFieldToBeRequired('Activate date', 'activationDate')
	})

	describe('Default Field values:', function () {
		function testTextFieldToHaveValue(title, name, value) {
			it(`"${title}" field initialized correctly`, async () => {
				let {
					getByTestId
				} = render(<ProspectActivationForm/>)

				const node = getByTestId(`${name}_field-input`)
				expect(node).toHaveValue(value)
			})
		}

		testTextFieldToHaveValue('Activate date', 'activationDate', format(Date.now(), formats.americanMediumDate))
		testTextFieldToHaveValue('Comment', 'comment', '')
	})
})