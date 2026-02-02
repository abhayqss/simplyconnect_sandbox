import React, {
    memo
} from 'react'

import cn from 'classnames'
import { filesize } from 'filesize'
import { map } from 'underscore'

import {
    CollapsibleText
} from 'components'

import {
    Detail
} from 'components/business/common'

import {
    isNotEmpty,
    DateUtils as DU
} from 'lib/utils/Utils'

import { ReactComponent as Indicatior } from 'images/dot.svg'

import './DocumentDetails.scss'

const { format, formats } = DU

const DATE_FORMAT = formats.americanMediumDate

function DocumentDetails(
    {
        data = {},
        className,
        onViewSignature
    }
) {
    return isNotEmpty(data) && (
        <>
            <div className={cn('DocumentDetails', className)}>
                <Detail
                    layout="v"
                    title="File name"
                >
                    {data.title}
                </Detail>
                <Detail
                    layout="v"
                    title="Description"
                >
                    {data.description ? (
                        <CollapsibleText lines={4}>
                            {data.description}
                        </CollapsibleText>
                    ) : ''}
                </Detail>
                {isNotEmpty(data.categories) && (
                    <Detail
                        layout="v"
                        title="Categories"
                        valueClassName="d-flex flex-row flex-wrap padding-top-5"
                    >
                        {map(data.categories, o => (
                            <div
                                className="DocumentCategory DocumentManager-DocumentCategory"
                                style={{ borderColor: o.color || '#000000' }}
                            >
                                <Indicatior
                                    style={{ fill: o.color || '#000000' }}
                                    className="DocumentCategory-Indicator DocumentManager-DocumentCategoryIndicator"
                                />
                                <div className="DocumentCategory-Name DocumentManager-DocumentCategoryName">
                                    {o.name}
                                </div>
                            </div>
                        ))}
                    </Detail>
                )}
                <Detail
                    layout="v"
                    title="Client name"
                >
                    {data.clientName}
                </Detail>
                <Detail
                    layout="v"
                    title="Community name"
                >
                    {data.communityTitle}
                </Detail>
                <div className="d-flex flex-row">
                    <Detail
                        layout="v"
                        title="Created"
                        className="margin-right-15"
                    >
                        {format(data.createdDate, DATE_FORMAT)}
                    </Detail>
                    <Detail
                        layout="v"
                        title="Size"
                    >
                        {filesize(data.size, { standard: 'jedec' })}
                    </Detail>
                </div>
                <div className="d-flex flex-row">
                    <Detail
                        layout="v"
                        title="Assigned date"
                        className="margin-right-15"
                    >
                        {format(data.assignedDate, DATE_FORMAT)}
                    </Detail>
                    <Detail
                        layout="v"
                        title="Assigned by"
                    >
                        {data.assignedBy}
                    </Detail>
                </div>
                <div className="d-flex flex-row">
                    <Detail
                        layout="v"
                        title="Deleted date"
                        className="margin-right-15"
                    >
                        {format(data.temporarilyDeletedDate, DATE_FORMAT)}
                    </Detail>
                    <Detail
                        layout="v"
                        title="Deleted by"
                    >
                        {data.temporarilyDeletedBy}
                    </Detail>
                </div>
                <Detail
                    layout="v"
                    title="Status"
                >
                    {data.signature?.statusTitle}
                </Detail>
                {data.signature && (
                    <div
                        className="link font-size-15 margin-bottom-25"
                        onClick={onViewSignature}
                    >
                        View Signature Information
                    </div>
                )}
            </div>
        </>
    )
}

export default memo(DocumentDetails)