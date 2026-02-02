import React, {
    memo,
    useMemo,
    useState,
    useEffect,
    useCallback
} from 'react'

import {
    Link,
    useParams
} from 'react-router-dom'

import { Col, Row } from 'reactstrap'

import {
    map,
    size,
    filter
} from 'underscore'

import {
    useToggle
} from 'hooks/common'

import {
    useESignHistoryQuery,
    useESignBulkRequestsQuery,
    useESignRequestPinResending
} from 'hooks/business/documents/e-sign'

import {
    Modal,
    Table,
    ErrorViewer
} from 'components'

import {
    Button
} from 'components/buttons'

import {
    Detail
} from 'components/business/common'

import {
    ConfirmDialog,
    SuccessDialog
} from 'components/dialogs'

import {
    E_SIGN_ACTIONS,
    E_SIGN_STATUSES,
    E_SIGN_BULK_STATUSES
} from 'lib/Constants'

import {
    DateUtils as DU
} from 'lib/utils/Utils'

import {
    path
} from 'lib/utils/ContextUtils'

import { DocumentSignatureStatusIcon as StatusIcon } from '../'

import { ReactComponent as Warning } from 'images/alert-yellow.svg'
import { ReactComponent as Close } from 'images/close.svg'

import './DocumentSignatureManager.scss'

const { format, formats } = DU

const DATE_TIME_FORMAT = formats.longDateMediumTime12

const {
    SENT,
    SIGNED,
    FAILED,
    RECEIVED,
    REQUESTED,
    REQUEST_EXPIRED,
    REQUEST_CANCELED
} = E_SIGN_STATUSES

const {
    DOCUMENT_SENT,
    DOCUMENT_SIGNED,
    DOCUMENT_RECEIVED,
    DOCUMENT_SIGNATURE_FAILED,
    DOCUMENT_SIGNATURE_REQUESTED,
    DOCUMENT_SIGNATURE_REQUEST_EXPIRED,
    DOCUMENT_SIGNATURE_REQUEST_CANCELED
} = E_SIGN_ACTIONS

const SIGNATURE_ACTIONS_STATUSES = {
    [DOCUMENT_SENT]: SENT,
    [DOCUMENT_SIGNED]: SIGNED,
    [DOCUMENT_RECEIVED]: RECEIVED,
    [DOCUMENT_SIGNATURE_FAILED]: FAILED,
    [DOCUMENT_SIGNATURE_REQUESTED]: REQUESTED,
    [DOCUMENT_SIGNATURE_REQUEST_EXPIRED]: REQUEST_EXPIRED,
    [DOCUMENT_SIGNATURE_REQUEST_CANCELED]: REQUEST_CANCELED
}

let isPinResendingDisabledByDefault = false

function getStatusByAction(actionName) {
    return SIGNATURE_ACTIONS_STATUSES[actionName]
}

function DocumentSignatureManager(
    {
        isOpen,
        document,
        onClose,
        onSignDocument,
        onRequestSignature,
        onRenewBulkRequest,
        onCancelBulkRequest,
        onRenewSignatureRequest,
        onCancelSignatureRequest
    }
) {
    const params = useParams()

    const [error, setError] = useState(null)

    const [isPinResendingDisabled, togglePinResendingDisabling] = useToggle(isPinResendingDisabledByDefault)

    const [isCancelConfirmDialogOpen, toggleCancelConfirmDialog] = useToggle()
    const [isRequestPinSendSuccessDialog, toggleRequestPinSendSuccessDialog] = useToggle()
    const [isCancelBulkRequestConfirmDialogOpen, toggleCancelBulkRequestConfirmDialog] = useToggle()
    const [isRenewBulkRequestConfirmDialogOpen, toggleRenewBulkRequestConfirmDialog] = useToggle()

    const canCancelRequest = useMemo(() => (
            !document?.isTemporarilyDeleted
            && document?.signature?.canCancelRequest
            && [REQUESTED, SENT].includes(document?.signature?.statusName)
        ), [document]
    )

    const canRequestSignature = useMemo(
        () => document?.signature?.canRequest
            && !document?.isTemporarilyDeleted
            && document?.signature?.statusName === REQUEST_EXPIRED,
        [document]
    )

    const {
        data,
        sort,
        fetch,
        isFetching,
        pagination
    } = useESignHistoryQuery(
        { documentId: document?.id },
        { onError: setError }
    )

    const {
        data: bulkRequests,
        isFetching: isFetchingBulkRequests
    } = useESignBulkRequestsQuery({
            status: canCancelRequest ? [
                E_SIGN_BULK_STATUSES.SIGNATURE_REQUESTED, E_SIGN_BULK_STATUSES.REVIEW_REQUESTED
            ] : [E_SIGN_BULK_STATUSES.EXPIRED],
            bulkRequestId: document?.bulkRequestId
        }, {
            enabled: (canCancelRequest || canRequestSignature) && !!document?.bulkRequestId,
            cacheTime: 0
        }
    )

    const accessibleBulkRequests = useMemo(
        () => filter(bulkRequests, ({ clientId, templateId }) => templateId === document?.templateId && clientId !== parseInt(params.clientId)),
        [bulkRequests, document, params.clientId]
    )

    const shouldShowConfirmBulkRequestDialog = useMemo(() => size(accessibleBulkRequests) > 0, [accessibleBulkRequests])

    const {
        mutateAsync: resendPin,
        data: { receiverPhone: requestReceiverPhone } = {}
    } = useESignRequestPinResending({
        onError: setError,
        onSuccess: () => toggleRequestPinSendSuccessDialog()
    })

    const onResendPin = useCallback(() => {
        togglePinResendingDisabling()
        isPinResendingDisabledByDefault = true

        resendPin({ requestId: document?.signature?.requestId })

        setTimeout(() => {
            togglePinResendingDisabling()
            isPinResendingDisabledByDefault = false
        }, 5 * 60 * 1000)
    }, [document, resendPin, togglePinResendingDisabling])

    useEffect(() => {
        if (isOpen && document) fetch()
    }, [isOpen, fetch, document])

    return isOpen && (
        <>
            <Modal
                isOpen
                title="Signature Information"
                className="DocumentSignatureManager"
                bodyClassName="DocumentSignatureManager-Body"
                footerClassName="DocumentSignatureManager-Footer"
                onClose={onClose}
                renderFooter={() => (
                    <>
                        <Button
                            outline
                            color="success"
                            onClick={onClose}
                            className="DocumentSignatureManager-Action"
                        >
                            Back to Details
                        </Button>
                        {canCancelRequest && (
                                <Button
                                    color="success"
                                    id={`doc-${document?.id}_cancel-sign-req-btn`}
                                    onClick={() => !shouldShowConfirmBulkRequestDialog ?
                                        toggleCancelConfirmDialog() :
                                        toggleCancelBulkRequestConfirmDialog()
                                    }
                                    className="DocumentSignatureManager-Action"
                                >
                                    Cancel Request
                                </Button>
                            )
                        }
                        {document?.signature?.canRequest && (
                            !document?.signature?.statusName
                            || document?.signature?.statusName === SIGNED
                        ) && document?.signature?.hasAvailableAreas && (
                                <Button
                                    color="success"
                                    id={`doc-${document?.id}_req-sign-btn`}
                                    onClick={onRequestSignature}
                                    className="DocumentSignatureManager-Action"
                                >
                                    Request Signature
                                </Button>
                            )}
                        {canRequestSignature && (
                                <Button
                                    color="success"
                                    id={`doc-${document?.id}_req-sign-btn`}
                                    onClick={() => !shouldShowConfirmBulkRequestDialog ?
                                        onRenewSignatureRequest() :
                                        toggleRenewBulkRequestConfirmDialog()
                                    }
                                    className="DocumentSignatureManager-Action"
                                >
                                    {document?.signature?.hasAreas ? 'Request Signature' : 'Send Document'}
                                </Button>
                            )}
                        {(
                            !document?.isTemporarilyDeleted
                            && document?.signature?.canSign
                            && [SENT, REQUESTED].includes(document?.signature?.statusName)
                        ) && (
                                <Button
                                    color="success"
                                    id={`doc-${document?.id}_req-sign-btn`}
                                    onClick={onSignDocument}
                                    className="DocumentSignatureManager-Action"
                                >
                                    {document.signature.statusName === REQUESTED ? 'Sign Document' : 'View Document'}
                                </Button>
                            )}
                    </>
                )}
            >
                <div className="DocumentSignatureManager-Info padding-top-20">
                    <Row className="margin-bottom-20">
                        <Col md={3}>
                            <Detail
                                layout="v"
                                title="Status"
                            >
                                <span className="margin-right-8">
                                    {document?.signature?.statusTitle}
                                </span>
                                <StatusIcon
                                    hasTip={false}
                                    statusName={document?.signature?.statusName}
                                    statusTitle={document?.signature?.statusTitle}
                                />
                            </Detail>
                        </Col>
                        {document?.signature?.pdcFlowPinCode && (
                            <Col md={3}>
                                <Detail
                                    layout="v"
                                    title="PIN"
                                >
                                    {document?.signature?.pdcFlowPinCode}
                                </Detail>
                            </Col>
                        )}
                        {(
                            !document?.isTemporarilyDeleted
                            && document?.signature?.canResendPdcFlowPinCode
                        ) && (
                                <Col md={3} className="h-flexbox align-items-center">
                                    <Button
                                        color="success"
                                        onClick={onResendPin}
                                        id="resend-request-pin-btn"
                                        disabled={isPinResendingDisabled}
                                        hasTip={isPinResendingDisabled}
                                        tipText="Please allow 5 minutes for this code to arrive. Then you can request another code."
                                    >
                                        Resend PIN
                                    </Button>
                                </Col>
                            )}
                    </Row>
                </div>
                <Table
                    hasHover
                    keyField="id"
                    title="History"
                    noDataText="No data"
                    data={data}
                    isLoading={isFetching || isFetchingBulkRequests}
                    pagination={pagination}
                    columns={[
                        {
                            dataField: 'actionTitle',
                            text: 'Action',
                            sort: true,
                            onSort: sort,
                            formatter: (v, row) => {
                                return (
                                    <>
                                        <StatusIcon
                                            hasTip={false}
                                            statusTitle={v}
                                            statusName={getStatusByAction(row.actionName)}
                                            className="margin-right-8"
                                        />
                                        <span>{v}</span>
                                    </>
                                )
                            },
                            headerClasses: 'DocumentSignatureHistory-ActionCol'
                        },
                        {
                            dataField: 'source',
                            text: 'By',
                            sort: true,
                            onSort: sort
                        },
                        {
                            dataField: 'roleTitle',
                            text: 'Role',
                            sort: true,
                            onSort: sort,
                        },
                        {
                            dataField: 'date',
                            text: 'Date',
                            sort: true,
                            onSort: sort,
                            formatter: v => format(v, DATE_TIME_FORMAT)
                        },
                        {
                            dataField: 'comments',
                            text: 'Comments',
                            headerClasses: 'DocumentSignatureHistory-CommentsCol',
                            formatter: v => (
                                v ? v.split('\n').map(o => (
                                    <div className="DocumentSignatureHistory-Comment">{o}</div>
                                )) : ''
                            )
                        }
                    ]}
                    className="DocumentSignatureHistory"
                    containerClass="DocumentSignatureHistoryContainer"
                />
            </Modal>
            {isCancelConfirmDialogOpen && (
                <ConfirmDialog
                    isOpen
                    icon={Warning}
                    cancelBtnText="Close"
                    confirmBtnText="Cancel Signature Request"
                    title="The signature request will be canceled. The document will be archived."
                    onConfirm={onCancelSignatureRequest}
                    onCancel={() => toggleCancelConfirmDialog()}
                />
            )}
            {isRequestPinSendSuccessDialog && (
                <SuccessDialog
                    isOpen
                    title={`The PIN code sent to ${requestReceiverPhone}. Please allow several minutes for this code to arrive.`}
                    buttons={[
                        { text: 'Ok', onClick: () => toggleRequestPinSendSuccessDialog() }
                    ]}
                />
            )}
            {isCancelBulkRequestConfirmDialogOpen && (
                <ConfirmDialog
                    isOpen
                    icon={null}
                    title={(
                        <div className="d-flex flex-column align-items-start text-left font-size-18 font-weight-normal">
                            <p>This request was submitted in bulk.</p>
                            <p>There are clients who haven't signed/reviewed document(s):</p>
                            <p className="d-flex flex-column">{map(accessibleBulkRequests, ({ clientId, clientFullName }) => (
                                <Link
                                    key={`link-${clientId}`}
                                    to={path(`/clients/${clientId}/documents`)}
                                    target="_blank"
                                    className="DocumentSignatureHistory-ClientLink"
                                >
                                    {clientFullName}
                                </Link>
                            ))}</p>
                            <p>Please select an option.</p>
                            <Close
                                className="DocumentSignatureHistory-DialogCloseIcon"

                                onClick={() => toggleCancelBulkRequestConfirmDialog()}
                            />
                        </div>
                    )}
                    className="CancelRequestDialog"
                    cancelBtnText="Cancel For All Clients"
                    confirmBtnText="Cancel For Selected Client"
                    onConfirm={onCancelSignatureRequest}
                    onCancel={() => onCancelBulkRequest()}
                />
            )}
            {isRenewBulkRequestConfirmDialogOpen && (
                <ConfirmDialog
                    isOpen
                    icon={null}
                    title={(
                        <div className="d-flex flex-column align-items-start text-left font-size-18 font-weight-normal">
                            <p>This request was submitted in bulk.</p>
                            <p>There are clients with expired signature requests:</p>
                            <p className="d-flex flex-column">{map(accessibleBulkRequests, ({ clientId, clientFullName }) => (
                                <Link
                                    key={`link-${clientId}`}
                                    to={path(`/clients/${clientId}/documents`)}
                                    target="_blank"
                                    className="DocumentSignatureHistory-ClientLink"
                                >
                                    {clientFullName}
                                </Link>
                            ))}</p>
                            <p>Please select an option.</p>
                            <Close
                                className="DocumentSignatureHistory-DialogCloseIcon"

                                onClick={() => toggleRenewBulkRequestConfirmDialog()}
                            />
                        </div>
                    )}
                    className="ResubmitConfirmDialog"
                    cancelBtnText="Resubmit For All Clients"
                    confirmBtnText="Resubmit For Selected Client"
                    onConfirm={onRenewSignatureRequest}
                    onCancel={() => onRenewBulkRequest()}
                />
            )}
            {error && (
                <ErrorViewer
                    isOpen
                    error={error}
                    onClose={() => setError(null)}
                />
            )}
        </>
    )
}

export default memo(DocumentSignatureManager)