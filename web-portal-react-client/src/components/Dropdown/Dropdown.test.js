import React from 'react'

import { render } from 'lib/test-utils'

import Dropdown from './Dropdown'

const ITEMS = [
    { value: 0, text: 'Dropdown Item 0' },
    { value: 1, text: 'Dropdown Item 1' },
    { value: 2, text: 'Dropdown Item 2' },
    { value: 3, text: 'Dropdown Item 3' },
    { value: 4, text: 'Dropdown Item 4' }
]

describe('<Dropdown>:', () => {
    it('Toggle text is displayed correctly', async () => {
        const { findByText } = render(
            <Dropdown
                items={ITEMS}
                toggleText="Dropdown's test toggle text"
            />
        )

        expect(await findByText("Dropdown's test toggle text")).toBeVisible()
    })

    it('Items are displayed correctly', async () => {
        const { findByText } = render(
            <Dropdown items={ITEMS}/>
        )

        expect(await findByText('Dropdown Item 0')).toBeVisible()
        expect(await findByText('Dropdown Item 1')).toBeVisible()
        expect(await findByText('Dropdown Item 2')).toBeVisible()
        expect(await findByText('Dropdown Item 3')).toBeVisible()
        expect(await findByText('Dropdown Item 4')).toBeVisible()
    })

    it('Item indicator is displayed correctly', async () => {
        const { findByText } = render(
            <Dropdown
                items={[
                    {
                        value: 0,
                        text: 'Dropdown Test Item',
                        hasIndicator: true,
                        indicatorClassName: 'Dropdown-TestItemIndicator',
                        indicatorIconClassName: 'Dropdown-TestItemIndicatorIcon'
                    }
                ]}
            />
        )

        const itemNode = await findByText('Dropdown Test Item')

        expect(itemNode.querySelector('.Dropdown-TestItemIndicator')).toBeVisible()
        expect(itemNode.querySelector('.Dropdown-TestItemIndicatorIcon')).toBeVisible()
    })
})