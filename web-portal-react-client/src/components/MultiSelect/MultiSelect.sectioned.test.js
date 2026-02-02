import React, { useState } from 'react'

import { first, last, omit, flatten } from 'underscore'

import { render, fireEvent, doManualInput } from 'lib/test-utils'

import BaseMultiSelect from './MultiSelect'

const  defaultPlaceholder = 'Select'

const testSections = [
    {
        id: 1,
        title: 'Section 1',
        options: [
            { text: 'Option 1-1', value: 11},
            { text: 'Option 1-2', value: 12},
            { text: 'Option 1-3', value: 13},
        ]
    },
    {
        id: 2,
        title: 'Section 2',
        options: [
            { text: 'Option 2-1', value: 21},
            { text: 'Option 2-2', value: 22},
            { text: 'Option 2-3', value: 23},
        ]
    },
    {
        id: 3,
        title: 'Section 3',
        options: [
            { text: 'Option 3-1', value: 31},
            { text: 'Option 3-2', value: 32},
            { text: 'Option 3-3', value: 33},
        ]
    }
]

const testOptions = flatten(testSections.map(o => o.options))

function SectionedMultiSelect(props) {
    let [value, setValue] = useState(props.value || null)

    props = omit(props, 'value')

    return (
        <BaseMultiSelect
            isMultiple
            isSectioned
            value={value}
            sections={testSections}
            onChange={(v) => setValue(v)}
            name="sectioned"
            {...props}
        />
    )
}

describe('<MultiSelect> (sectioned):', () => {
    it('displays correctly', () => {
        let { getByTestId } = render(
            <SectionedMultiSelect />
        )

        let element = getByTestId('sectioned_multi-select')

        expect(element).toBeInTheDocument()
        expect(element).toBeVisible()
    })

    it('renders lists correctly', () => {
        let { getByText } = render(
            <SectionedMultiSelect  />
        )

        let optionTexts = testOptions.map(o => o.text)

        optionTexts.forEach(function callback(text) {
            expect(getByText(text)).toBeInTheDocument()
        })
    })

    it('selects all options correctly', () => {
        let { getByTestId, getByText } = render(
            <SectionedMultiSelect />
        )

        testOptions.forEach(option => {
            fireEvent.click(getByText(option.text))
        })

        let container = getByTestId('sectioned_multi-select')

        expect(container.querySelector('.MultiSelect-SelectedText')).toHaveTextContent('All')
    })

    it('selects all options correctly by choosing All option', () => {
        let { getByTestId, getByText } = render(
            <SectionedMultiSelect />
        )

        fireEvent.click(getByText('All'))

        let container = getByTestId('sectioned_multi-select')

        expect(container.querySelector('.MultiSelect-SelectedText')).toHaveTextContent('All')
    })

    it('renders options on click', () => {
        let { getByTestId } = render(<SectionedMultiSelect />)

        let container = getByTestId('sectioned_multi-select')

        fireEvent.click(container.querySelector('.MultiSelect-SelectedText'))

        let optionsDropdown = container.querySelector('.MultiSelect-Options')

        expect(optionsDropdown).toBeVisible()
    })

    it('renders default placeholder', () => {
        let { getByTestId } = render(<SectionedMultiSelect />)

        let container = getByTestId('sectioned_multi-select')

        expect(container.querySelector('.MultiSelect-SelectedText')).toHaveTextContent(defaultPlaceholder)
    })

    it('renders placeholder correctly', () => {
        let testPlaceholder = 'Test placeholder'

        let { getByTestId } = render(
            <SectionedMultiSelect placeholder={testPlaceholder} />
        )

        let container = getByTestId('sectioned_multi-select')

        expect(container.querySelector('.MultiSelect-SelectedText')).toHaveTextContent(testPlaceholder)
    })

    it('selects all options correctly when multiple options have been checked-off', () => {
        let options = [first(testOptions), last(testOptions)]

        let { getByTestId, getByText } = render(
            <SectionedMultiSelect value={options.map(o => o.value)} />
        )

        fireEvent.click(getByText('All'))

        let container = getByTestId('sectioned_multi-select')

        expect(container.querySelector('.MultiSelect-SelectedText')).toHaveTextContent('All')
    })

    it('renders default value', () => {
        let defaultOption = first(testOptions)

        let { getByTestId } = render(
            <SectionedMultiSelect value={[defaultOption.value]} />
        )

        let container = getByTestId('sectioned_multi-select')

        expect(container.querySelector('.MultiSelect-SelectedText')).toHaveTextContent(defaultOption.text)
    })

    it('displays selected option correctly', () => {
        let option = first(testOptions)

        let { getByTestId, getByText } = render(
            <SectionedMultiSelect />
        )

        let container = getByTestId('sectioned_multi-select')

        fireEvent.click(getByText(option.text))

        expect(container.querySelector('.MultiSelect-SelectedText')).toHaveTextContent(option.text)
    })

    it('displays all options in placeholder if selected', () => {
        let { getByTestId, getByText } = render(
            <SectionedMultiSelect hasAllOption={false} />
        )

        testOptions.forEach(option => {
            fireEvent.click(getByText(option.text))
        })

        let container = getByTestId('sectioned_multi-select')

        let optionsText = testOptions.map(o => o.text).join(', ')

        expect(container.querySelector('.MultiSelect-SelectedText')).toHaveTextContent(optionsText)
    })

    it('renders section titles correctly', () => {
        let { getByTestId, getByText } = render(
            <SectionedMultiSelect />
        )

        let container = getByTestId('sectioned_multi-select')

        testSections.forEach(function callback(o) {
            expect(getByText(o.title)).toBeInTheDocument()
        })
    })

    it(`doesn't render section titles if needed`, () => {
        let { getByTestId, queryByText } = render(
            <SectionedMultiSelect hasSectionTitle={false} />
        )

        let container = getByTestId('sectioned_multi-select')

        testSections.forEach(function callback(o) {
            expect(queryByText(o.title)).not.toBeInTheDocument()
        })
    })

    it('renders copy-pasted search text correctly', () => {
        let searchText = last(testOptions).text
        let { getByTestId } = render(
            <SectionedMultiSelect
                hasKeyboardSearch
                hasKeyboardSearchText
            />
        )

        let container = getByTestId('sectioned_multi-select')
        let searchBox = container.querySelector('.MultiSelect-KeyboardSearchInput')

        fireEvent.change(searchBox, { target: { value: searchText } })

        expect(searchBox.value).toBe(searchText)
    })

    it('renders manually typed search text correctly', () => {
        let searchText = last(testOptions).text
        let { getByTestId } = render(
            <SectionedMultiSelect
                hasKeyboardSearch
                hasKeyboardSearchText
            />
        )

        let container = getByTestId('sectioned_multi-select')
        let searchBox = container.querySelector('.MultiSelect-KeyboardSearchInput')

        doManualInput(searchText, searchBox)

        expect(searchBox.value).toBe(searchText)
    })

    it('filters list correctly', () => {
        let searchText = last(testOptions).text
        let { getByTestId } = render(
            <SectionedMultiSelect
                hasAllOption={false}
                hasKeyboardSearch
                hasKeyboardSearchText
            />
        )

        let container = getByTestId('sectioned_multi-select')
        let searchBox = container.querySelector('.MultiSelect-KeyboardSearchInput')

        doManualInput(searchText, searchBox)

        expect(container.querySelectorAll('.MultiSelect-Option').length).toBe(1)
    })
})