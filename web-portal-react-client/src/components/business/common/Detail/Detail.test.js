import React from 'react'

import { render } from 'lib/test-utils'

import Detail from './Detail'

describe('<Detail>:', () => {
    it('is visible on UI', async () => {
        const { findByTestId } = render(
            <Detail title="TestDetail's title">
                TestDetail's content
            </Detail>
        )

        const node = await findByTestId('Detail')

        expect(node).toBeInTheDocument()
        expect(node).toBeVisible()
    })

    it('Title is displayed correctly', async () => {
        const { findByText } = render(
            <Detail title="TestDetail's title">
                TestDetail's content
            </Detail>
        )

        expect(await findByText("TestDetail's title")).toBeVisible()
    })

    it('Content is displayed correctly', async () => {
        const { findByText } = render(
            <Detail title="TestDetail's title">
                TestDetail's content
            </Detail>
        )

        expect(await findByText("TestDetail's content")).toBeVisible()
    })

    it('All classNames are rendered correctly', async () => {
        const { findByText, findByTestId } = render(
            <Detail
                title="TestDetail's title"
                className="TestDetail"
                titleClassName="TestDetail-Title"
                valueClassName="TestDetail-Value"
            >
                TestDetail's content
            </Detail>
        )

        expect(await findByTestId('Detail')).toHaveClass('TestDetail')
        expect(await findByText("TestDetail's title")).toHaveClass('TestDetail-Title')
        expect(await findByText("TestDetail's content")).toHaveClass('TestDetail-Value')
    })
})