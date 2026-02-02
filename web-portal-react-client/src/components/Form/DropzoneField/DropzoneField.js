import React, {
    useMemo,
    useCallback
} from 'react'

import cn from 'classnames'

import PTypes from 'prop-types'

import {
    first,
    values,
    isArray,
    isString,
    isEqual
} from 'underscore'

import {
    Label,
    FormGroup,
    UncontrolledTooltip as Tooltip
} from 'reactstrap'

import { useDropzone } from 'react-dropzone'

import { FileFormatIcon } from 'components'

import { ReactComponent as CrossIcon } from 'images/cross.svg'

import {
    ALLOWED_FILE_FORMATS,
    ALLOWED_FILE_FORMAT_MIME_TYPES,
} from 'lib/Constants'

import {
    eqIgnoreCase
} from 'lib/utils/StringUtils'

import {
    map,
    isEmpty,
    isUnary
} from 'lib/utils/ArrayUtils'

import './DropzoneField.scss'

const {
    PDF,
    PNG,
    JPG,
    JPEG,
    GIF,
    TIFF
} = ALLOWED_FILE_FORMATS

export const BROWSER_TYPES = {
    default: 'default',
    custom: 'custom'
}

const ALLOWED_MIME_TYPES = [PDF, PNG, JPG, JPEG, GIF, TIFF].map(
    type => ALLOWED_FILE_FORMAT_MIME_TYPES[type]
)

const IMAGE_MIME_TYPES = [PNG, JPG, JPEG, GIF].map(
    type => ALLOWED_FILE_FORMAT_MIME_TYPES[type]
)

function isDefaultBrowser(browser) {
    return eqIgnoreCase(browser.type, BROWSER_TYPES.default)
}

function DropzoneField(
    {
        name,
        label,
        value,
        errors,
        browsers,
        onChange,
        hintText,
        className,
        maxCount,
        allowedTypes,
        ...props
    }
) {
    let size = value.length || value.size
    let isDisabled = props.isDisabled || size >= maxCount

    let error = isString(errors) ? errors : null

    const onDrop = useCallback(acceptedFiles => {
        let files = isArray(value) ? [...value, ...acceptedFiles] : value.push(...acceptedFiles)
        let size = files.length || files.size

        if (size > maxCount) {
            files = files.slice(0, maxCount)
        }

        onChange(name, files)
    }, [value, name, maxCount, onChange])

    const onRemoveFile = file => {
        onChange(name, value.filter(o => o !== file))
    }

    const hasOnlyDefaultBrowser = (
        isEmpty(browsers)
        || (isUnary(browsers) && isDefaultBrowser(browsers[0]))
    )

    const { open, getRootProps, getInputProps } = useDropzone({
        onDrop,
        disabled: isDisabled,
        ...!hasOnlyDefaultBrowser && {
            noClick: true,
            noKeyboard: true
        }
    })

    const files = useMemo(() => (
        [...value].sort((prev, next) => prev.name.localeCompare(next.name))
    ), [value])

    const sortedErrors = useMemo(() => {
        return Object.keys(errors).reduce((accum, key) => {
            let index = files.findIndex(o => isEqual(o, Array.from(value)[key]))

            accum[index] = errors[key]

            return accum
        }, {})
    }, [errors, files, value])

    return (
        <FormGroup className={cn('DropzoneField', className, { isDisabled })}>
            {label && (
                <Label className="DropzoneField-Label">
                    {label}
                </Label>
            )}

            <div className="DropzoneField-DropArea" {...getRootProps()}>
                <input {...getInputProps()} accept={allowedTypes.toString()} />

                <div className="DropzoneField-DropArea-HintText">
                    Drop files to attach, or {(
                    <span
                        id={`${name}-browse-btn`}
                        className="DropzoneField-Link color_primary">browse</span>
                    )}
                    {!(isDisabled || hasOnlyDefaultBrowser) && (
                        <Tooltip
                            trigger="legacy"
                            target={`${name}-browse-btn`}
                            className="DropzoneField-BrowserPopup"
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
                            <ul className="DropzoneField-BrowserList">
                                {map(browsers, o => (
                                    <li
                                        key={o.name ?? o.title}
                                        className="DropzoneField-BrowserListItem"
                                    >
                                        <a
                                            href="#"
                                            className={cn(
                                                'btn btn-link',
                                                'DropzoneField-BrowserTitle',
                                                { disabled: o.isDisabled }
                                            )}
                                            onClick={isDefaultBrowser(o) ? open : o.onSelect}
                                        >
                                            {o.title}
                                        </a>
                                    </li>
                                ))}
                            </ul>
                        </Tooltip>
                    )}
                    {size >= maxCount && (
                        <Tooltip
                            trigger="hover"
                            target={`${name}-browse-btn`}
                            className="DropzoneField-BrowserPopup"
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
                            <div className="h-padding-10">
                                You cannot select more than {maxCount} files
                            </div>
                        </Tooltip>
                    )}
                </div>
            </div>

            <div className="DropzoneField-HintText">
                {hintText}
            </div>

            {!!size && (
                <div className="DropzoneField-Files margin-top-16">
                    {files.map((file, index) => {
                        let errorMessage = first(values(sortedErrors[index]))

                        return (
                            <React.Fragment key={index}>
                                <div className={cn('DropzoneField-File form-control', { 'is-invalid': errorMessage })}>
                                    {
                                        (file instanceof File && IMAGE_MIME_TYPES.includes(file.type)) ? (
                                            <img
                                                alt={file.name}
                                                src={URL.createObjectURL(file)}
                                                className="DropzoneField-Image margin-right-10"
                                            />
                                        ) : (
                                            <FileFormatIcon
                                                mimeType={file.type}
                                                className="DropzoneField-Image margin-right-10"
                                            />
                                        )
                                    }


                                    <div className="DropzoneField-MessageBox margin-right-10">
                                        <div className="DropzoneField-FileName">
                                            {file.name}
                                        </div>

                                        {errorMessage && (
                                            <div className="DropzoneField-ErrorHint">
                                                {errorMessage}
                                            </div>
                                        )}
                                    </div>

                                    <CrossIcon
                                        onClick={() => onRemoveFile(file)}
                                        className="DropzoneField-CrossIcon"
                                    />
                                </div>

                            </React.Fragment>
                        )
                    })}
                </div>
            )}

            {error && (
                <div className="DropzoneField-ErrorHint DropzoneField-SingleError form-control is-invalid">
                    {error}
                </div>
            )}
        </FormGroup>
    )
}

DropzoneField.propTypes = {
    name: PTypes.string,
    label: PTypes.string,
    value: PTypes.array,
    browsers: PTypes.array,
    errors: PTypes.object,
    onChange: PTypes.func,
    hintText: PTypes.string,
    isDisabled: PTypes.bool,
    className: PTypes.string,
    maxCount: PTypes.number,
    allowedTypes: PTypes.array,
}

DropzoneField.defaultProps = {
    value: [],
    errors: {},
    maxCount: 20,
    allowedTypes: ALLOWED_MIME_TYPES
}

export default DropzoneField
