import React from 'react'

import { map } from 'underscore'

import { Loader } from 'components'
import { IconButton } from 'components/buttons'

import { DocumentDetail } from 'components/business/Documents'

import {
    isEmpty,
    DateUtils as DU,
    getFileFormatByMimeType
} from 'lib/utils/Utils'

import { ReactComponent as Cross } from 'images/close.svg'
import { ReactComponent as Pencil } from 'images/pencil.svg'

import './DocumentList.scss'

const { format, formats } = DU

const DATE_FORMAT = formats.americanMediumDate

export default function DocumentList(
    {
        data = [],
        isLoading,
        noDataText = 'No data',
        className,
        getItemOptions
    }
) {
    return (
        <div className={className}>
            {isLoading ? (
                <Loader/>
            ) : (
                isEmpty(data) ? noDataText : map(data, o => {
                    const options = getItemOptions(o) ?? {}

                    return (
                        <DocumentDetail
                            key={o.id}
                            layout="stretch"
                            id={o.id}
                            name={o.name || o.title || o.id}
                            {...{...o, ...options}}
                            date={o.createdDate}
                            format={getFileFormatByMimeType(o.mimeType)}
                            onDownload={() => options.onDownload(o)}
                            renderInfo={({ name, title, date }) => (
                                <>
                                    {date && (
                                        <div className='DocumentDetail-Date'>
                                            {format(date, DATE_FORMAT)}
                                        </div>
                                    )}
                                    <div className='DocumentDetail-Title'>
                                        {name || title}
                                    </div>
                                </>
                            )}
                            renderActions={items => (
                                <>
                                    {options.canEdit && (
                                        <IconButton
                                            name={`doc-${o.id}_edit`}
                                            Icon={Pencil}
                                            onClick={() => options.onEdit(o)}
                                            className="DocumentDetail-Action"
                                            tipText={options.editHint}
                                        />
                                    )}
                                    {options.canDelete && (
                                        <IconButton
                                            name={`doc-${o.id}_delete`}
                                            Icon={Cross}
                                            onClick={() => options.onDelete(o)}
                                            className="DocumentDetail-Action DocumentDetail-DeleteBtn"
                                            tipText={options.deleteHint}
                                        />
                                    )}
                                    {items}
                                </>
                            )}
                        />
                    )
                })
            )}
        </div>
    )
}