import React from 'react'

import { Loader } from 'components'

import PicturePreview from '../PicturePreview/PicturePreview'

import cn from 'classnames'

import {
    ALLOWED_FILE_FORMATS,
    ALLOWED_FILE_FORMAT_MIME_TYPES,
} from 'lib/Constants'

import { ReactComponent as File } from 'images/unknown-file.svg'

import './FilePreview.scss'

const { PNG, JPG, JPEG, GIF, TIFF } = ALLOWED_FILE_FORMATS

const IMAGE_MIME_TYPES = [PNG, JPG, JPEG, GIF, TIFF].map(
    type => ALLOWED_FILE_FORMAT_MIME_TYPES[type]
)

const isPicture = mimeType => IMAGE_MIME_TYPES.includes(mimeType)

export default function FilePreview({
    url,
    data,
    onClick,
    isLoading,
    className,
    renderIcon
}) {
    if (isPicture(data.type)) {
        return (
            <PicturePreview
                url={url}
                data={data}
                isLoading={isLoading}
                className={className}
                renderIcon={renderIcon}
            />
        )
    }

    return (
        <div className={cn('FilePreview', className)} onClick={onClick}>
            <div className="FilePreview-Body">
                <File className="FilePreview-Icon margin-right-9" />

                <span className="FilePreview-Name">{data.name}</span>

                {renderIcon && !isLoading && (
                    <div className="FilePreview-Slot margin-left-9">
                        {renderIcon()}
                    </div>
                )}
            </div>

            {isLoading && (
                <Loader hasBackdrop className="FilePreview-Loader" />
            )}
        </div>
    )
}