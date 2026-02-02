import React, { memo, useMemo, useState, useEffect, useContext, useCallback } from 'react'

import { connect } from 'react-redux'
import { compose, bindActionCreators } from 'redux'

import { Button } from 'reactstrap'

import Modal from 'components/Modal/Modal'
import Loader from 'components/Loader/Loader'
import Detail from 'components/business/common/Detail/Detail'

import { DetailsContext } from '../ReferralDetails/ReferralDetails'

import RequestInfoEditor from '../RequestInfoEditor/RequestInfoEditor';

import actions from 'redux/referral/info/request/details/referralInfoRequestDetailsActions'

import { isEmpty, DateUtils as DU } from 'lib/utils/Utils'

import './ReferralInfoRequestViewer.scss'

const { format, formats } = DU

const DATE_FORMAT = formats.longDateMediumTime12

const STATUS_COLORS = {
    PENDING: '#e0e0e0',
    REPLIED: '#d5f3b8'
}

function mapStateToProps(state) {
    return {
        state: state.referral.info.request.details
    }
}

function mapDispatchToProps(dispatch) {
    return {
        actions: bindActionCreators(actions, dispatch)
    }
}

function ReferralInfoRequestViewer({
    state,
    actions,

    isOpen,
    onClose,

    canReply,
    requestId,
    referralId,
    infoRequestId,
    requestAvailable,
}) {
    const { isFetching, data } = state
    const { refresh: refreshReferralDetails } = useContext(DetailsContext)
    const canFetch = (
        Number.isInteger(requestId) || Number.isInteger(referralId)
    ) && Number.isInteger(infoRequestId)

    const [isRequestInfoEditorOpen, setIsRequestInfoEditorOpen] = useState()

    const params = useMemo(() => ({
        infoRequestId,
        ...Number.isInteger(requestId)
            ? { requestId }
            : { referralId }
    }), [
        requestId,
        referralId,
        infoRequestId
    ])

    function fetch() {
        actions.load(params)
    }

    function fetchIfCan() {
        if (canFetch) {
            onFetch()
        }
    }

    const onFetch = useCallback(fetch, [params])
    const onCloseRequestInfoEditor = useCallback(() => setIsRequestInfoEditorOpen(false), [])

    useEffect(fetchIfCan, [canFetch, onFetch])

    useEffect(() => actions.clear, [actions])

    let content = isFetching ? (
        <Loader />
    ) : (
            <div>No data</div>
        )

    if (!isEmpty(data)) {
        const hasResponse = data.response?.date !== null

        content = (
            <>
                <div className="ReferralInfoRequestViewer-Section">
                    <div className="ReferralInfoRequestViewer-SectionTitle">
                        Request
                    </div>

                    <Detail title="SUBJECT">
                        {data.subject}
                    </Detail>
                    <Detail title="REQUEST DATE">
                        {format(data.request.date, DATE_FORMAT)}
                    </Detail>
                    <Detail title="STATUS">
                        <div
                            className="ReferralInfoReferralRequest-Status"
                            style={{ backgroundColor: STATUS_COLORS[data.statusName] }}
                        >
                            {data.statusTitle}
                        </div>
                    </Detail>
                    <Detail title="AUTHOR">
                        {data.request.authorFullName}
                    </Detail>
                    <Detail title="PHONE #">
                        {data.request.authorPhone}
                    </Detail>
                    <Detail title="MESSAGE">
                        {data.request.text}
                    </Detail>
                </div>

                {hasResponse && (
                    <div className="ReferralInfoRequestViewer-Section">
                        <div className="ReferralInfoRequestViewer-SectionTitle">
                            Reply
                        </div>

                        <Detail title="RESPONSE DATE">
                            {format(data.response.date, DATE_FORMAT)}
                        </Detail>
                        <Detail title="AUTHOR">
                            {data.response.authorFullName}
                        </Detail>
                        <Detail title="PHONE #">
                            {data.response.authorPhone}
                        </Detail>
                        <Detail title="MESSAGE">
                            {data.response.text}
                        </Detail>
                    </div>
                )}
            </>
        )
    }

    return (
        <>
            <Modal
                isOpen={isOpen}
                onClose={onClose}
                hasCloseBtn={false}
                title="View Request for Information"
                className="ReferralInfoRequestViewer"
                renderFooter={() => (
                    <>
                        <Button
                            outline
                            color="success"
                            onClick={onClose}
                        >
                            Close
                    </Button>
                        {canReply && (
                            <Button
                                color="success"
                                onClick={() => setIsRequestInfoEditorOpen(true)}
                                disabled={!requestAvailable}
                            >
                                Reply
                            </Button>
                        )}
                    </>
                )}>
                {content}
            </Modal>

            <RequestInfoEditor
                referralId={referralId}
                infoRequestId={infoRequestId}
                isOpen={isRequestInfoEditorOpen}
                onClose={onCloseRequestInfoEditor}
                onSubmit={refreshReferralDetails}
            />
        </>
    )
}

export default compose(
    memo, connect(mapStateToProps, mapDispatchToProps)
)(ReferralInfoRequestViewer)
