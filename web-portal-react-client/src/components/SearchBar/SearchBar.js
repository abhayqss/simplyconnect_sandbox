import React, { useMemo, useState, useEffect, useCallback } from 'react'

import cn from 'classnames'

import {
    Input,
    Dropdown,
    InputGroup,
    DropdownMenu,
    DropdownItem,
    DropdownToggle,
} from 'reactstrap'

import { useDebounce } from 'use-debounce'

import { useList } from 'hooks/common'

import { ReactComponent as Cross } from 'images/cross.svg'

import './SearchBar.scss'

const defaultOptions = {
    isMinimal: true,
    doLoad: Promise.resolve,
    textProp: 'title',
    valueProp: 'id',
}

const renderMenuBody = ({ dataSource, value, onPickItem, getText, getValue }) => {
    return (
        dataSource.data.map(item => {
            let [itemText, itemValue] = [getText(item), getValue(item)]
            let selected = getValue(value)

            return (
                <DropdownItem className="SearchBar-DropdownOption" onClick={() => onPickItem(item)}>
                    <div className="SearchBar-Checkbox">
                        {selected === itemValue ? (
                            <span className="SearchBar-CheckMark"></span>
                        ) : null}
                    </div>
                    <div className="SearchBar-DropdownOption-Text">{itemText}</div>
                </DropdownItem>
            )
        })
    )
}

function SearchBar({
    value,
    params,
    options,
    onChange,
    hasError,
    className,
    isDisabled,
    name = 'search',
    defaultValue = null,
    children = renderMenuBody,
}) {
    const [searchText, setSearchText] = useState('')
    const [isOpenDropdown, setIsOpenDropdown] = useState(false)

    const [search] = useDebounce(searchText, 300)

    const parameters = useMemo(() => ({
        ...params,
        search,
    }), [params, search])

    const opts = useMemo(() => ({
        ...defaultOptions,
        ...options,
    }), [options])

    const {
        state: {
            dataSource,
        },
        fetch,
        fetchIf,
        clear: clearData,
    } = useList('LIVE_SEARCH', parameters, opts)

    function setSearchTextIf() {
        if (value) {
            setSearchText(value[opts.textProp])
        }
    }

    const toggleDropdown = () => setIsOpenDropdown(prevState => !prevState)

    const selectItem = item => {
        onChange(name, item)
    }

    const clearField = useCallback(() => {
        clearData()
        setSearchText('')
        onChange(name, defaultValue)
    }, [clearData, onChange, name, defaultValue])

    useEffect(() => {
        if (!search?.trim()) {
            clearField()
        }
    }, [search, clearField])

    useEffect(() => { fetchIf(!!search?.trim()) }, [fetchIf, search])

    useEffect(setSearchTextIf, [value, opts.textProp])

    return (
        <InputGroup classNames={cn('SearchBar', 'SearchBar-Container', className)}>
            <Dropdown isOpen={isOpenDropdown} toggle={toggleDropdown} className="SearchBar-Dropdown">
                <DropdownToggle tag="div" className="SearchBar-DropdownToggle">
                    <Input
                        className="SearchBar-Input"
                        type="text"
                        name={name}
                        invalid={hasError}
                        value={searchText}
                        disabled={isDisabled}
                        autoComplete="off"
                        onChange={({ target }) => setSearchText(target.value)}
                    />
                </DropdownToggle>

                {searchText ? (
                    <Cross
                        className="SearchBar-CloseIcon"
                        onClick={clearField}
                    />
                ) : null}

                <DropdownMenu className="SearchBar-DropdownMenu">
                    {children({
                        value,
                        dataSource,
                        refresh: fetch,
                        toggle: toggleDropdown,
                        onPickItem: selectItem,
                        getValue: item => item && item[opts.valueProp],
                        getText: item => item && item[opts.textProp],
                    })}
                </DropdownMenu>
            </Dropdown>
        </InputGroup>
    )
}

export default SearchBar