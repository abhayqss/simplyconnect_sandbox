import React, { memo, useEffect, useCallback } from 'react'

import { connect } from 'react-redux'
import { compose, bindActionCreators } from 'redux'

import { Button } from 'reactstrap'

import Modal from 'components/Modal/Modal'
import Loader from 'components/Loader/Loader'
import Detail from 'components/business/common/Detail/Detail'

import actions from 'redux/referral/request/details/referralRequestDetailsActions'

import { isEmpty, DateUtils as DU } from 'lib/utils/Utils'

import './RequestViewer.scss'

const { format, formats } = DU

const DATE_FORMAT = formats.longDateMediumTime12

const STATUS_COLORS = {
    PENDING: '#e0e0e0',
    ACCEPTED: '#d5f3b8',
    PRE_ADMIT: '#ffedc2',
    DECLINED: '#fde1d5',
    CANCELLED: '#fcccb8'
}

function mapStateToProps(state) {
    return {
        state: state.referral.request.details
    }
}

function mapDispatchToProps(dispatch) {
    return {
        actions: bindActionCreators(actions, dispatch)
    }
}

function RequestViewer({
    state,
    actions,

    isOpen,
    onClose,

    clientId,
    requestId,
    referralId,
    organizationId,
}) {
    const { isFetching, data } = state

    let content = isFetching ? (
        <Loader/>
    ) : (
        <div>No data</div>
    )

    if (!isEmpty(data)) {
        content = (
            <div className="RequestViewer">
                <div className="RequestViewer-Section">
                    <Detail title="Organization">
                        {data.organization}
                    </Detail>
                    <Detail title="Service Provider">
                        {data.community}
                    </Detail>
                    <Detail title="Status">
                        <div
                            className="ReferralRequest-Status"
                            style={{ backgroundColor: STATUS_COLORS[data.statusName] }}
                        >
                            {data.statusTitle}
                        </div>
                    </Detail>
                    <Detail title="Response Date">
                        {format(data.date, DATE_FORMAT)}
                    </Detail>
                    <Detail title="Pre-Admit Date">
                        {format(data.preAdmitDate, DATE_FORMAT)}
                    </Detail>
                    <Detail title="Decline Reason">
                        {data.declineReason}
                    </Detail>
                    <Detail title="Comment">
                        {data.comment}
                    </Detail>
                </div>
            </div>
        )
    }

    useEffect(() => {
        actions.load({
            clientId,
            requestId,
            referralId,
            organizationId
        })
    }, [
        actions,
        clientId,
        requestId,
        referralId,
        organizationId
    ])

    useEffect(() => actions.clear, [actions])

    return (
        <Modal
            isOpen={isOpen}
            onClose={onClose}
            hasCloseBtn={false}
            title="View Response to Referral Request"
            className="RequestViewer"
            renderFooter={() => (
                <Button color='success' onClick={onClose}>
                    Close
                </Button>
            )}>
            {content}
        </Modal>
    )
}

export default compose(
    memo, connect(mapStateToProps, mapDispatchToProps)
)(RequestViewer)
