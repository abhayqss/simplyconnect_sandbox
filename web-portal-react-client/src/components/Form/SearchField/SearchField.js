import React, { memo } from 'react'

import cn from 'classnames'

import {
    Label,
    FormGroup,
    UncontrolledTooltip as Tooltip,
} from 'reactstrap'

import { SearchBar } from 'components'

import './SearchField.scss'

function SearchField({
    name,
    value,
    label,
    tooltip,
    onChange,
    className,
    errorText,
    isDisabled = false,
    ...params
}) {
    return (
        <FormGroup className={cn('SearchField', className, { 'SearchField-Disabled': isDisabled })}>
            {label ? (
                <>
                    <Label className="SearchField-Label">
                        {label}
                    </Label>

                    {tooltip && (
                        <Tooltip
                            modifiers={[
                                {
                                    name: 'offset',
                                    options: { offset: [0, 6] }
                                },
                                {
                                    name: 'preventOverflow',
                                    options: { boundary: document.body }
                                }
                            ]}
                            {...tooltip}
                        >
                            {tooltip.text || tooltip.render()}
                        </Tooltip>
                    )}
                </>
            ) : null}

            <SearchBar
                name={name}
                value={value}
                onChange={onChange}
                hasError={!!errorText}
                isDisabled={isDisabled}
                {...params}
            />

            {errorText && (
                <div className="SearchField-Error">
                    {errorText}
                </div>
            )}
        </FormGroup>
    )
}

export default memo(SearchField)
