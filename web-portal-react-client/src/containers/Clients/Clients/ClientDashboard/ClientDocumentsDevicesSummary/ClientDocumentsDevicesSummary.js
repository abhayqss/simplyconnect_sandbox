import React, {
    memo,
    useMemo,
    useEffect
} from 'react'

import cn from 'classnames'
import PTypes from 'prop-types'

import {
    Button
} from 'components/buttons'

import {
    useCanDownloadCCDQuery,
    useClientDocumentsQuery,
    useCanViewDocumentsQuery,
    useCanDownloadFacesheetQuery
} from 'hooks/business/client/documents'

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
    map,
    part,
    isNotEmpty
} from 'lib/utils/ArrayUtils'

import DocumentDetail from '../../Documents/DocumentDetail/DocumentDetail'

import './ClientDocumentsDevicesSummary.scss'

const { MAX_SIZE } = PAGINATION

const { PDF, XML } = ALLOWED_FILE_FORMATS

const {
    SIGNED, REQUESTED
} = E_SIGN_STATUSES

const DEVICE_TYPE_ICONS = {
    'SMART_WATCH': SmartWatchIcon,
    'SMART_PILLBOX': SmartPillboxIcon
}

function ClientDevice({ type, title, summary, className }) {
    const TypeIcon = DEVICE_TYPE_ICONS[type] || PdfTypeIcon

    return (
        <div className={cn('ClientDevice', className)}>
            <div className='flex-1 d-flex'>
                <TypeIcon className='ClientDevice-TypeIcon'/>
                <div className='d-flex flex-column justify-content-center'>
                    <div className='ClientDevice-Title'>
                        {title}
                    </div>
                    <div className='ClientDevice-Summary'>
                        {summary}
                    </div>
                </div>
            </div>
            <div className='d-flex align-items-center'>
                <Edit
                    className='ClientDevice-EditBtn'
                    onClick={() => {alert('Coming Soon')}}
                />
                <Delete
                    className='ClientDevice-DeleteBtn'
                    onClick={() => {alert('Coming Soon')}}
                />
            </div>
        </div>
    )
}

const SignedDocumentList = memo(
    function SignedDocumentList({ clientId, data }) {
        return (
            <div className='ClientDocumentSummaryList'>
                <div className='ClientDocumentSummaryList-Title'>
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
                        clientId={clientId}
                        layout="stretch"
                        downloadHint="Download signed document"
                        className='ClientDocumentSummaryList-Item'
                    />
                ))}
            </div>
        )
    }
)

const SignatureRequestedDocumentList = memo(
    function SignatureRequestedDocumentList({ clientId, data }) {
        return (
            <div className='ClientDocumentSummaryList'>
                <div className='ClientDocumentSummaryList-Title'>
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
                        clientId={clientId}
                        layout="stretch"
                        downloadHint="Download document"
                        className='ClientDocumentSummaryList-Item'
                    />
                ))}
            </div>
        )
    }
)

function ClientDocumentsDevicesSummary(
    {
        clientId,
        className,
        clientName,

        onViewAllDocuments
    }
) {
    const {
        fetch,
        data: { data: documents = [] } = {}
    } = useClientDocumentsQuery({
        clientId,
        size: MAX_SIZE,
        signatureStatusNames: [SIGNED, REQUESTED]
    })

    const {
        data: canViewDocuments
    } = useCanViewDocumentsQuery({ clientId })

    const {
        data: canDownloadCCD
    } = useCanDownloadCCDQuery({ clientId })

    const {
        data: canDownloadFacesheet
    } = useCanDownloadFacesheetQuery({ clientId })

    const signedDocuments = useMemo(() => (
        part(documents?.filter(o => o.signature.statusName === SIGNED), 0, 2)
    ), [documents])

    const signatureRequestedDocuments = useMemo(() => (
        part(documents?.filter(o => o.signature.statusName === REQUESTED), 0, 2)
    ), [documents])

    useEffect(() => { fetch() }, [fetch])

    return (
        <div className={cn('ClientDocumentsDevicesSummary', className)}>
            <div className='ClientDocumentSummaryList'>
                <div className='ClientDocumentSummaryList-Title'>Documents</div>
                <DocumentDetail
                    id="facesheet"
                    title='Facesheet'
                    format={PDF}
                    date={Date.now()}
                    canView={false}
                    canDownload={canDownloadFacesheet}
                    clientId={clientId}
                    layout="stretch"
                    className='ClientDocumentSummaryList-Item'
                />
                <DocumentDetail
                    id="ccd"
                    title='CCD'
                    format={XML}
                    date={Date.now()}
                    clientId={clientId}
                    clientName={clientName}
                    canView={canViewDocuments}
                    viewHint={!canViewDocuments ? "You don't have permissions to see Client's CCD" : ''}
                    canDownload={canDownloadCCD}
                    layout="stretch"
                    className='ClientDocumentSummaryList-Item'
                />
            </div>
            {isNotEmpty(signedDocuments) && (
                <SignedDocumentList
                    clientId={clientId}
                    data={signedDocuments}
                />
            )}
            {isNotEmpty(signatureRequestedDocuments) && (
                <SignatureRequestedDocumentList
                    clientId={clientId}
                    data={signatureRequestedDocuments}
                />
            )}
            <div className="text-right margin-top-5">
                <Button
                    color="success"
                    disabled={!canViewDocuments}
                    className="ClientDocumentSummaryList-ViewAllBtn"
                    tooltip={canViewDocuments ? '' : "You don't have permissions to see Client's Documents"}
                    onClick={() => { onViewAllDocuments() }}>
                    View All Documents
                </Button>
            </div>
        </div>
    )
}

export default memo(ClientDocumentsDevicesSummary)