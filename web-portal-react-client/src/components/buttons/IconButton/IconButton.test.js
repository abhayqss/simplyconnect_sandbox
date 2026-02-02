import React from 'react'

import { render } from 'lib/test-utils'

import { ReactComponent as Pencil } from 'images/pencil.svg'

import IconButton from './IconButton'

describe('<IconButton>:', () => {
    it('is visible on UI', async () => {
        const { findByTestId } = render(
            <IconButton
                name="testIconButton"
                Icon={Pencil}
            />
        )

        const node = await findByTestId('testIconButton')

        expect(node).toBeInTheDocument()
        expect(node).toBeVisible()
    })

    it('Icon is displayed correctly', async () => {
        const { findByTestId } = render(
            <IconButton
                name="testIconButton"
                Icon={Pencil}
            />
        )

        const node = await findByTestId('testIconButton')

        expect(node.querySelector('#testIconButton')).toBeVisible()
    })
})