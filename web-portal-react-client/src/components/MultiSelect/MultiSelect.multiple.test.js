import React, { useState } from 'react'

import { first, last, omit } from 'underscore'

import { render, fireEvent, doManualInput } from 'lib/test-utils'

import BaseMultiSelect from './MultiSelect'

const testOptions = [
    { text: 'Option 1', value: 1 },
    { text: 'Option 2', value: 2 },
    { text: 'Option 3', value: 3 },
]

function MultiSelect(props) {
    let [value, setValue] = useState(props.value || null)

    props = omit(props, 'value')

    return (
        <BaseMultiSelect
            isMultiple
            value={value}
            options={testOptions}
            onChange={(v) => setValue(v)}
            name="multiple"
            {...props}
        />
    )
}

describe('<MultiSelect> (multiple):', () => {
    it('displays correctly', () => {
        let { getByTestId } = render(
            <MultiSelect />
        )

        let element = getByTestId('multiple_multi-select')

        expect(element).toBeInTheDocument()
        expect(element).toBeVisible()
    })

    it('renders lists correctly', () => {
        let { getByText } = render(
            <MultiSelect
                options={testOptions}
            />
        )

        expect(getByText('Option 1')).toBeInTheDocument()
        expect(getByText('Option 2')).toBeInTheDocument()
        expect(getByText('Option 3')).toBeInTheDocument()
    })

    it('displays preselected options correctly', () => {
        let defaultOptions = testOptions.slice(0, 2)

        let { getByTestId } = render(
            <MultiSelect value={defaultOptions.map(o => o.value)} />
        )

        let container = getByTestId('multiple_multi-select')

        let optionsText = defaultOptions.map(o => o.text).join(', ')

        expect(container.querySelector('.MultiSelect-SelectedText')).toHaveTextContent(optionsText)
    })

    it('displays selected option correctly', () => {
        let options = testOptions.slice(0, 2)

        let { getByTestId, getByText } = render(
            <MultiSelect />
        )

        let container = getByTestId('multiple_multi-select')

        options.forEach(option => {
            fireEvent.click(getByText(option.text))
        })

        let optionsText = options.map(o => o.text).join(', ')

        expect(container.querySelector('.MultiSelect-SelectedText')).toHaveTextContent(optionsText)
    })

    it('unselects an option correctly', () => {
        let defaultOptions = testOptions.slice(0, 2)

        let { getByTestId, getByText } = render(
            <MultiSelect value={defaultOptions.map(o => o.value)} />
        )

        fireEvent.click(getByText(first(defaultOptions).text))

        let container = getByTestId('multiple_multi-select')

        let optionsText = defaultOptions.slice(1, 2).map(o => o.text).join(', ')

        expect(container.querySelector('.MultiSelect-SelectedText')).toHaveTextContent(optionsText)
    })

    it('selects multiple options correctly', () => {
        let options = [first(testOptions), last(testOptions)]
        let { getByTestId, getByText } = render(
            <MultiSelect />
        )

        options.forEach(option => {
            fireEvent.click(getByText(option.text))
        })

        let container = getByTestId('multiple_multi-select')
        let placeholderText = options.map(o => o.text).join(', ')

        expect(container.querySelector('.MultiSelect-SelectedText')).toHaveTextContent(placeholderText)
    })

    it('selects all options correctly', () => {
        let { getByTestId, getByText } = render(
            <MultiSelect />
        )

        testOptions.forEach(option => {
            fireEvent.click(getByText(option.text))
        })

        let container = getByTestId('multiple_multi-select')

        expect(container.querySelector('.MultiSelect-SelectedText')).toHaveTextContent('All')
    })

    it('selects all options correctly by choosing All option', () => {
        let { getByTestId, getByText } = render(
            <MultiSelect />
        )

        fireEvent.click(getByText('All'))

        let container = getByTestId('multiple_multi-select')

        expect(container.querySelector('.MultiSelect-SelectedText')).toHaveTextContent('All')
    })

    it('selects all options correctly when multiple options have been checked-off', () => {
        let options = [first(testOptions), last(testOptions)]
        let { getByTestId, getByText } = render(
            <MultiSelect value={options.map(o => o.value)} />
        )

        fireEvent.click(getByText('All'))

        let container = getByTestId('multiple_multi-select')

        expect(container.querySelector('.MultiSelect-SelectedText')).toHaveTextContent('All')
    })

    it('displays all options in placeholder if selected', () => {
        let { getByTestId, getByText } = render(
            <MultiSelect hasAllOption={false} />
        )

        testOptions.forEach(option => {
            fireEvent.click(getByText(option.text))
        })

        let container = getByTestId('multiple_multi-select')

        let optionsText = testOptions.map(o => o.text).join(', ')

        expect(container.querySelector('.MultiSelect-SelectedText')).toHaveTextContent(optionsText)
    })

    it('renders copy-pasted search text correctly', () => {
        let searchText = last(testOptions).text
        let { getByTestId } = render(
            <MultiSelect
                hasKeyboardSearch
                hasKeyboardSearchText
            />
        )

        let container = getByTestId('multiple_multi-select')
        let searchBox = container.querySelector('.MultiSelect-KeyboardSearchInput')

        fireEvent.change(searchBox, { target: { value: searchText } })

        expect(searchBox.value).toBe(searchText)
    })

    it('renders manually typed search text correctly', () => {
        let searchText = last(testOptions).text
        let { getByTestId } = render(
            <MultiSelect
                hasKeyboardSearch
                hasKeyboardSearchText
            />
        )

        let container = getByTestId('multiple_multi-select')
        let searchBox = container.querySelector('.MultiSelect-KeyboardSearchInput')

        doManualInput(searchText, searchBox)

        expect(searchBox.value).toBe(searchText)
    })

    it('filters list correctly', () => {
        let searchText = last(testOptions).text
        let { getByTestId } = render(
            <MultiSelect
                hasAllOption={false}
                hasKeyboardSearch
                hasKeyboardSearchText
            />
        )

        let container = getByTestId('multiple_multi-select')
        let searchBox = container.querySelector('.MultiSelect-KeyboardSearchInput')

        doManualInput(searchText, searchBox)

        expect(container.querySelectorAll('.MultiSelect-Option').length).toBe(1)
    })

    it('unselects multiple if None is checked-off', () => {
        let options = [first(testOptions), last(testOptions)]

        let { getByTestId, getByText } = render(
            <MultiSelect
                hasNoneOption
                value={options.map(o => o.value)}
            />
        )

        let container = getByTestId('multiple_multi-select')

        fireEvent.click(getByText('None'))

        expect(container.querySelector('.MultiSelect-SelectedText')).toHaveTextContent('Select')
    })

    it(`doesn't get collapsed correctly`, () => {
        let { getByTestId } = render(
            <MultiSelect isAutoCollapsible={false} />
        )

        let container = getByTestId('multiple_multi-select')

        fireEvent.click(container.querySelector('.MultiSelect-SelectedText'))

        let multiSelect = container.querySelector('.MultiSelect')

        expect(multiSelect.classList.contains('MultiSelect_expanded')).toBe(true)

        fireEvent.click(container.querySelector('.MultiSelect-SelectedText'))

        expect(multiSelect.classList.contains('MultiSelect_collapsed')).toBe(true)
    })
})
