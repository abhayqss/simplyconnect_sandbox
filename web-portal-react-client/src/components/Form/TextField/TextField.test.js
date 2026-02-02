import React, { useState } from 'react'

import { render } from 'lib/test-utils'

import BaseTextField from './TextField'

function TextField(props) {
    let [value, setValue] = useState(props.value ?? '')

    return (
        <BaseTextField
            {...props}
            value={value}
            onChange={(_, value) => setValue(value)}
        />
    )
}

describe('<TextField>:', () => {
    it('is visible on UI', async () => {
        const { findByTestId } = render(
            <TextField name="testTextField"/>
        )

        const node = await findByTestId('testTextField_field')

        expect(node).toBeInTheDocument()
        expect(node).toBeVisible()
    })

    it('Label is displayed correctly', async () => {
        const { findByText } = render(
            <TextField
                label="Test TextField Label"
                name="testTextField"
            />
        )

        const node = await findByText('Test TextField Label')

        expect(node).toBeVisible()
    })

    it('Label icon is displayed correctly', async () => {
        const { findByText } = render(
            <TextField
                label="Test TextField Label"
                name="testTextField"
                renderLabelIcon={() => (
                    <span>TextField Label Icon</span>
                )}
            />
        )

        const node = await findByText('TextField Label Icon')

        expect(node).toBeVisible()
    })

    it('Icon is displayed correctly', async () => {
        const { findByText } = render(
            <TextField
                label="Test TextField Label"
                name="testTextField"
                renderIcon={() => (
                    <span>TextField Icon</span>
                )}
            />
        )

        const node = await findByText('TextField Icon')

        expect(node).toBeVisible()
    })

    it('Error is displayed correctly', async () => {
        const { findByText } = render(
            <TextField
                label="Test TextField Label"
                name="testTextField"
                hasError
                errorText="Test TextField error text"
            />
        )

        const node = await findByText('Test TextField error text')

        expect(node).toBeVisible()
    })
})