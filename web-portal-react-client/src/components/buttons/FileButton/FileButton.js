import React, {
    useRef,
    useState,
    useEffect
} from 'react'

import cn from 'classnames'
import PTypes from 'prop-types'

import { first } from 'underscore'

import {
    Button,
    UncontrolledTooltip
} from 'reactstrap'

import {
    ALLOWED_FILE_FORMATS,
    ALLOWED_FILE_FORMAT_MIME_TYPES
} from 'lib/Constants'

import './FileButton.scss'

const {
    DOC,
    DOCX,
    PDF,
    JPG,
    JPEG,
    PJPG,
    PNG,
    MP3,
    MP4
} = ALLOWED_FILE_FORMATS

const ALLOWED_MIME_TYPES = [
    ALLOWED_FILE_FORMAT_MIME_TYPES[DOC],
    ALLOWED_FILE_FORMAT_MIME_TYPES[DOCX],
    ALLOWED_FILE_FORMAT_MIME_TYPES[PDF],
    ALLOWED_FILE_FORMAT_MIME_TYPES[JPG],
    ALLOWED_FILE_FORMAT_MIME_TYPES[JPEG],
    ALLOWED_FILE_FORMAT_MIME_TYPES[PJPG],
    ALLOWED_FILE_FORMAT_MIME_TYPES[PNG],
    ALLOWED_FILE_FORMAT_MIME_TYPES[MP3],
    ALLOWED_FILE_FORMAT_MIME_TYPES[MP4]
]

function getRandom() {
    return Math.random() * 1000000000000
}

function Tooltip(props) {
    return (
        <UncontrolledTooltip
            trigger="click hover"
            placement="left-start"
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
            {...props}
        />
    )
}

export default function FileButton(
    {
        text,
        Icon,
        value,
        isDisabled,
        allowedTypes,
        onChangeFiles,
        isMultiple,
        tooltip,
        id = getRandom(),
        className
    }
) {
    const ref = useRef()
    const inputRef = useRef()

    const [, setTooltipReady] = useState(false)

    function changeInput(event) {
        let { files } = event.target

        onChangeFiles(isMultiple ? files : first(files))
    }

    useEffect(() => setTooltipReady(true), [])

    useEffect(function clearInput() {
        if (!value) {
            inputRef.current.value = ''
        }
    }, [value])

    return (
        <>
            <input
                id={`input-${id}`}
                type='file'
                ref={inputRef}
                accept={allowedTypes.toString()}
                className="FileBtn-Input"
                onChange={changeInput}
                multiple={isMultiple}
            />

            <label
                id={id}
                ref={ref}
                htmlFor={`input-${id}`}
                className={cn('FileBtn', className)}
            >
                {Icon && (
                    <Icon/>
                )}

                {!Icon && (
                    <Button
                        disabled={isDisabled}
                        color={isDisabled ? 'secondary' : 'success'}
                    >
                        {text || 'Choose File'}
                    </Button>
                )}
            </label>

            {tooltip && ref.current && typeof tooltip === 'string' && (
                <Tooltip target={ref.current}>
                    {tooltip}
                </Tooltip>
            )}

            {tooltip && ref.current && typeof tooltip === 'object' && (
                <Tooltip target={ref.current} {...tooltip}>
                    {tooltip.text ?? tooltip.render()}
                </Tooltip>
            )}
        </>
    )
}

FileButton.propTypes = {
    text: PTypes.string,
    isDisabled: PTypes.bool,
    onChangeFile: PTypes.func,
    className: PTypes.string,
    tooltip: PTypes.oneOfType([PTypes.string, PTypes.object])
}

FileButton.defaultProps = {
    allowedTypes: ALLOWED_MIME_TYPES
}