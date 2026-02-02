import React from 'react'

import { render } from 'lib/test-utils'

import CheckboxField from './CheckboxField'

describe('<CheckboxField>:', () => {
    it('is visible on UI', async () => {
        const { findByTestId } = render(
            <CheckboxField name="testCheckboxField"/>
        )

        const node = await findByTestId('testCheckboxField_field')

        expect(node).toBeInTheDocument()
        expect(node).toBeVisible()
    })

    it('Label is displayed correctly', async () => {
        const { findByText } = render(
            <CheckboxField
                label="Test CheckboxField Label"
                name="testCheckboxField"
            />
        )

        const node = await findByText('Test CheckboxField Label')

        expect(node).toBeVisible()
    })

    it('Label icon is displayed correctly', async () => {
        const { findByText } = render(
            <CheckboxField
                label="Test CheckboxField Label"
                name="testCheckboxField"
                renderLabelIcon={() => (
                    <span>CheckboxField Label Icon</span>
                )}
            />
        )

        const node = await findByText('CheckboxField Label Icon')

        expect(node).toBeVisible()
    })

    it('Error is displayed correctly', async () => {
        const { findByText } = render(
            <CheckboxField
                label="Test CheckboxField Label"
                name="testCheckboxField"
                hasError
                errorText="Test CheckboxField error text"
            />
        )

        const node = await findByText('Test CheckboxField error text')

        expect(node).toBeVisible()
    })
})