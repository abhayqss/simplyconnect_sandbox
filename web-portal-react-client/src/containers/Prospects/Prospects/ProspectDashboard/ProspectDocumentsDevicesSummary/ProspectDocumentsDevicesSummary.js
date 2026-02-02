import React, {
    memo,
    useMemo,
    useEffect
} from 'react'

import cn from 'classnames'

import { Button } from 'reactstrap'

import {
    useProspectQuery
} from 'hooks/business/Prospects'

import {
    useProspectDocumentsQuery
} from 'hooks/business/Prospects/Documents'

import { DocumentDetail } from 'containers/Prospects/Prospects/Documents'

import { ReactComponent as PdfTypeIcon } from 'images/pdf.svg'
import { ReactComponent as XmlTypeIcon } from 'images/xml.svg'
import { ReactComponent as DocTypeIcon } from 'images/doc.svg'

import { ReactComponent as Edit } from 'images/pencil.svg'
import { ReactComponent as Delete } from 'images/delete.svg'
import { ReactComponent as SmartWatchIcon } from 'images/smart-watch.svg'
import { ReactComponent as SmartPillboxIcon } from 'images/smart-pillbox.svg'

import {
    PAGINATION,
    E_SIGN_STATUSES,
    ALLOWED_FILE_FORMATS
} from 'lib/Constants'

import {
    isInteger
} from 'lib/utils/Utils'

import {
    map,
    part,
    isNotEmpty
} from 'lib/utils/ArrayUtils'

import './ProspectDocumentsDevicesSummary.scss'

const { MAX_SIZE } = PAGINATION

const { PDF, XML } = ALLOWED_FILE_FORMATS

const {
    SIGNED, REQUESTED
} = E_SIGN_STATUSES

const DEVICE_TYPE_ICONS = {
    'SMART_WATCH': SmartWatchIcon,
    'SMART_PILLBOX': SmartPillboxIcon
}

function ProspectDevice({ type, title, summary, className }) {
    const TypeIcon = DEVICE_TYPE_ICONS[type] || PdfTypeIcon

    return (
        <div className={cn('ProspectDevice', className)}>
            <div className='flex-1 d-flex'>
                <TypeIcon className='ProspectDevice-TypeIcon'/>
                <div className='d-flex flex-column justify-content-center'>
                    <div className='ProspectDevice-Title'>
                        {title}
                    </div>
                    <div className='ProspectDevice-Summary'>
                        {summary}
                    </div>
                </div>
            </div>
            <div className='d-flex align-items-center'>
                <Edit
                    className='ProspectDevice-EditBtn'
                    onClick={() => {alert('Coming Soon')}}
                />
                <Delete
                    className='ProspectDevice-DeleteBtn'
                    onClick={() => {alert('Coming Soon')}}
                />
            </div>
        </div>
    )
}

const SignedDocumentList = memo(
    function SignedDocumentList({ prospectId, data }) {
        return (
            <div className='ProspectDocumentSummaryList'>
                <div className='ProspectDocumentSummaryList-Title'>
                    Signed
                </div>
                {map(data, o => (
                    <DocumentDetail
                        key={o.id}
                        id={o.id}
                        canView={false}
                        title={o.title}
                        mimeType={o.mimeType}
                        date={o.signature.signedDate}
                        prospectId={prospectId}
                        layout="stretch"
                        downloadHint="Download signed document"
                        className='ProspectDocumentSummaryList-Item'
                    />
                ))}
            </div>
        )
    }
)

const SignatureRequestedDocumentList = memo(
    function SignatureRequestedDocumentList({ prospectId, data }) {
        return (
            <div className='ProspectDocumentSummaryList'>
                <div className='ProspectDocumentSummaryList-Title'>
                    Signature Requested
                </div>
                {map(data, o => (
                    <DocumentDetail
                        key={o.id}
                        id={o.id}
                        canView={false}
                        title={o.title}
                        mimeType={o.mimeType}
                        date={o.signature.requestedDate}
                        prospectId={prospectId}
                        layout="stretch"
                        downloadHint="Download document"
                        className='ProspectDocumentSummaryList-Item'
                    />
                ))}
            </div>
        )
    }
)

function ProspectDocumentsDevicesSummary(
    {
        prospectId,
        className,
        onViewAllDocuments
    }
) {
    const {
        data: prospect
    } = useProspectQuery({ prospectId }, {
        enabled: isInteger(prospectId)
    })

    const {
        fetch,
        data: documents = []
    } = useProspectDocumentsQuery({
        prospectId,
        size: MAX_SIZE,
        signatureStatusNames: [SIGNED, REQUESTED]
    })

    const signedDocuments = useMemo(() => (
        part(documents?.filter(o => o.signature.statusName === SIGNED), 0, 2)
    ), [documents])

    const signatureRequestedDocuments = useMemo(() => (
        part(documents?.filter(o => o.signature.statusName === REQUESTED), 0, 2)
    ), [documents])

    useEffect(() => {
        if (isInteger(prospectId)) fetch()
    }, [fetch, prospectId])

    return (
        <div className={cn('ProspectDocumentsDevicesSummary', className)}>
            <div className='ProspectDocumentSummaryList'>
                <div className='ProspectDocumentSummaryList-Title'>Documents</div>
                <DocumentDetail
                    id="facesheet"
                    title='Facesheet'
                    format={PDF}
                    date={Date.now()}
                    canView={false}
                    prospectId={prospectId}
                    prospectName={prospect?.fullName}
                    layout="stretch"
                    className='ProspectDocumentSummaryList-Item'
                />
                <DocumentDetail
                    id="ccd"
                    title='CCD'
                    format={XML}
                    date={Date.now()}
                    prospectId={prospectId}
                    prospectName={prospect?.fullName}
                    layout="stretch"
                    className='ProspectDocumentSummaryList-Item'
                />
            </div>
            {isNotEmpty(signedDocuments) && (
                <SignedDocumentList
                    prospectId={prospectId}
                    data={signedDocuments}
                />
            )}
            {isNotEmpty(signatureRequestedDocuments) && (
                <SignatureRequestedDocumentList
                    prospectId={prospectId}
                    data={signatureRequestedDocuments}
                />
            )}
            <div className="text-right margin-top-5">
                <Button
                    color="success"
                    className="ProspectDocumentSummaryList-ViewAllBtn"
                    onClick={() => { onViewAllDocuments() }}>
                    View All Documents
                </Button>
            </div>
        </div>
    )
}

export default memo(ProspectDocumentsDevicesSummary)