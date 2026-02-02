import React from 'react'

import { render } from 'lib/test-utils'

import DateField from './DateField'

describe('<DateField>:', () => {
    it('is visible on UI', async () => {
        const { findByTestId } = render(
            <DateField name="testDateField"/>
        )

        const node = await findByTestId('testDateField_field')

        expect(node).toBeInTheDocument()
        expect(node).toBeVisible()
    })

    it('Label is displayed correctly', async () => {
        const { findByText } = render(
            <DateField
                label="Test DateField Label"
                name="testDateField"
            />
        )

        const node = await findByText('Test DateField Label')

        expect(node).toBeVisible()
    })

    it('Error is displayed correctly', async () => {
        const { findByText } = render(
            <DateField
                label="Test DateField Label"
                name="testDateField"
                hasError
                errorText="Test DateField error text"
            />
        )

        const node = await findByText('Test DateField error text')

        expect(node).toBeVisible()
    })
})