import React, { useState } from 'react'

import { first, omit } from 'underscore'

import { render, fireEvent } from 'lib/test-utils'

import BaseMultiSelect from './MultiSelect'

const testOptions = [
    { text: 'Option 1', value: 1 },
    { text: 'Option 2', value: 2 },
]

const  defaultPlaceholder = 'Select'

function SimpleSelect(props) {
    let [value, setValue] = useState(props.value || null)

    props = omit(props, 'value')

    return (
        <BaseMultiSelect
            name="simple"
            value={value}
            options={testOptions}
            onChange={(v) => setValue(v)}
            {...props}
        />
    )
}

describe('<MultiSelect> (simple):', () => {
    it('displays correctly', () => {
        let { getByTestId } = render(
            <SimpleSelect />
        )

        let element = getByTestId('simple_multi-select')

        expect(element).toBeInTheDocument()
        expect(element).toBeVisible()
    })

    it('renders lists correctly', () => {
        let { getByText } = render(
            <SimpleSelect
                options={testOptions}
            />
        )

        expect(getByText('Option 1')).toBeInTheDocument()
        expect(getByText('Option 2')).toBeInTheDocument()
    })

    it('renders options on click', () => {
        let { getByTestId } = render(<SimpleSelect />)

        let container = getByTestId('simple_multi-select')

        fireEvent.click(container.querySelector('.MultiSelect-SelectedText'))

        let optionsDropdown = container.querySelector('.MultiSelect-Options')

        expect(optionsDropdown).toBeVisible()
    })

    it('renders default placeholder', () => {
        let { getByTestId } = render(<SimpleSelect />)

        let container = getByTestId('simple_multi-select')

        expect(container.querySelector('.MultiSelect-SelectedText')).toHaveTextContent(defaultPlaceholder)
    })

    it('renders placeholder correctly', () => {
        let testPlaceholder = 'Test placeholder'

        let { getByTestId } = render(
            <SimpleSelect placeholder={testPlaceholder} />
        )

        let container = getByTestId('simple_multi-select')

        expect(container.querySelector('.MultiSelect-SelectedText')).toHaveTextContent(testPlaceholder)
    })

    it('renders default value', () => {
        let defaultOption = first(testOptions)

        let { getByTestId } = render(
            <SimpleSelect defaultValue={defaultOption.value} />
        )

        let container = getByTestId('simple_multi-select')

        expect(container.querySelector('.MultiSelect-SelectedText')).toHaveTextContent(defaultOption.text)
    })

    it('is disabled', () => {
        let { getByTestId } = render(
            <SimpleSelect isDisabled />
        )

        let container = getByTestId('simple_multi-select')

        expect(container.querySelector('.MultiSelect')).toHaveClass('MultiSelect_disabled')
    })

    it('is invalid', () => {
        let { getByTestId } = render(
            <SimpleSelect isInvalid />
        )

        let container = getByTestId('simple_multi-select')

        expect(container.querySelector('.MultiSelect')).toHaveClass('is-invalid')
    })

    it('renders class name correctly', () => {
        let testClassName = 'TestClass'

        let { getByTestId } = render(
            <SimpleSelect className={testClassName} />
        )

        let container = getByTestId('simple_multi-select')

        expect(container.querySelector('.MultiSelect')).toHaveClass(testClassName)
    })

    it('displays selected option correctly', () => {
        let option = first(testOptions)

        let { getByTestId, getByText } = render(
            <SimpleSelect />
        )

        let container = getByTestId('simple_multi-select')

        fireEvent.click(getByText(option.text))

        expect(container.querySelector('.MultiSelect-SelectedText')).toHaveTextContent(option.text)
    })

    it('automatically collapses by default', () => {
        let { getByTestId } = render(
            <SimpleSelect />
        )

        let container = getByTestId('simple_multi-select')

        fireEvent.click(container.querySelector('.MultiSelect-SelectedText'))

        let multiSelect = container.querySelector('.MultiSelect')

        expect(multiSelect.classList.contains('MultiSelect_expanded')).toBe(true)

        fireEvent.click(container.querySelector('.MultiSelect-SelectedText'))

        expect(multiSelect.classList.contains('MultiSelect_collapsed')).toBe(true)
    })
})
