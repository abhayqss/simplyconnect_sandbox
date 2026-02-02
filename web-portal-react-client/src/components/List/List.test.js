import React from 'react'

import { render } from 'lib/test-utils'

import List from './List'

describe('<List>:', () => {
    it('is visible on UI', async () => {
        const { findByTestId } = render(
            <List
                type="simple"
                length={0}
                renderItem={() => ''}
            />
        )

        const node = await findByTestId('List')

        expect(node).toBeInTheDocument()
        expect(node).toBeVisible()
    })

    it('Items are displayed correctly', async () => {
        const { findByText } = render(
            <List
                length={3}
                renderItem={i => <div key={i}>List Item {i + 1}</div>}
            />
        )

        expect(await findByText('List Item 1')).toBeVisible()
        expect(await findByText('List Item 2')).toBeVisible()
        expect(await findByText('List Item 3')).toBeVisible()
    })
})