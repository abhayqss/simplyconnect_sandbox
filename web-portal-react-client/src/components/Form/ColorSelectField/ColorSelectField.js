import React, {
    useRef,
    useMemo,
    Fragment,
    useEffect,
} from 'react'

import cn from 'classnames'

import { UncontrolledTooltip as Tooltip } from 'reactstrap'

import SelectField from '../SelectField/SelectField'

import './ColorSelectField.scss'

function ColorSelectField({
    name,
    value,
    label,
    options,
    className,
    onChange,
    ...restProps
}) {
    const refs = useRef([])

    const sections = useMemo(() => options.map(option => (
        { options: [option] })
    ), [options])

    function onClickOption(onSelectOption) {
        return event => {
            const { id } = event.target

            onSelectOption(id === value, id)
        }
    }

    useEffect(() => {
        refs.current = refs.current.slice(0, options.length);
    }, [options])

    return (
        <SelectField
            {...restProps}
            name={name}
            value={value}
            label={label}
            sections={sections}
            isSectioned
            onChange={onChange}
            className={cn('ColorSelectField', className)}
            renderSelectedText={text => (
                <div className="ColorSelectField-SelectedText">
                    <div
                        className="ColorSelectField-ColorMark"
                        style={{ backgroundColor: value }}
                    />
                    <span>{text}</span>
                </div>
            )}
            renderSection={({ options, onSelectOption, index }) => {
                return options.map((option) => (
                    <Fragment key={option.value}>
                        <div
                            id={option.value}
                            ref={el => refs.current[index] = el}
                            onClick={onClickOption(onSelectOption)}
                            className="ColorSelectField-Option"
                            style={{ backgroundColor: option.value }}
                        />

                        {refs.current?.[index] && (
                            <Tooltip
                                target={refs.current[index]}
                                flip={false}
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
                            >
                                {option.text}
                            </Tooltip>
                        )}
                    </Fragment>
                ))
            }}
        />
    )
}

export default ColorSelectField
