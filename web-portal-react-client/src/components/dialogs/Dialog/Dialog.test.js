import React from 'react'

import { render } from 'lib/test-utils'

import { ReactComponent as Cross } from 'images/cross.svg'

import BaseDialog from './Dialog'

const testTitle = 'Test title'
const textContent = 'Content'

const Dialog = (props) => {
    return (
        <BaseDialog
            isOpen
            title={testTitle}
            text={textContent}
            {...props}
        />
    )
}

function setup(props) {
    let result = render(
        <Dialog {...props} />
    )

    let container = result.queryByTestId('dialog')

    return {
        ...result,
        container,
    }
}

describe('<Dialog>', () => {
    it('displays self correctly', () => {
        let { container } = setup()

        expect(container).toBeInTheDocument()
        expect(container).toBeVisible()
    })

    it(`doesn't show up correctly`, () => {
        let { container } = setup({ isOpen: false })

        expect(container).toBeNull()
    })

    it('displays title', () => {
        let { getByText } = setup()

        expect(getByText(testTitle)).toBeInTheDocument()
    })

    it('displays text content', () => {
        let { getByText } = setup()

        expect(getByText(textContent)).toBeInTheDocument()
    })

    it('displays DOM content', () => {
        let { getByTestId } = setup({
            text: null,
            children: <div data-testid="dom-content">DOM Content</div>
        })

        expect(getByTestId('dom-content')).toBeInTheDocument()
    })

    it('renders buttons correctly', () => {
        let buttons = [
            { text: 'Test 1', 'data-testid': 'test-btn-1' },
            { text: 'Test 2', 'data-testid': 'test-btn-2' },
        ]

        let { getByTestId } = setup({ buttons })

        buttons.forEach(function forEachCb(o) {
            expect(getByTestId(o['data-testid'])).toBeInTheDocument()
        })
    })

    it('displays icon correctly', () => {
        let { container } = setup({ icon: Cross })
        let icon = container.querySelector('.Dialog-Icon')

        expect(icon).toBeInTheDocument()
    })

    it('renders icon correctly', () => {
        let { container } = setup({
            icon: Cross,
            renderIcon: () => <Cross className="Test-Icon" />
        })
        let icon = container.querySelector('.Test-Icon')

        expect(icon).toBeInTheDocument()
    })
})