import React from 'react'

import { render } from 'lib/test-utils'

import AlertPanel from './AlertPanel'

describe('<AlertPanel>:', () => {
    it('displays correctly', async () => {
        let testMessage = 'Test'

        let { findByTestId } = render(
            <AlertPanel>
                {testMessage}
            </AlertPanel>
        )

        const element = await findByTestId('alert-panel')

        expect(element).toBeInTheDocument()
        expect(element).toBeVisible()
    })

    it('renders a message correctly', async () => {
        let testMessage = 'Test'

        let { findByText } = render(
            <AlertPanel>
                {testMessage}
            </AlertPanel>
        )

        const element = await findByText(testMessage)

        expect(element).toBeVisible()
    })
})
