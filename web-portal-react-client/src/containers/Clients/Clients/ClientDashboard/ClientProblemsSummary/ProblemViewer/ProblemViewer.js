import React, { memo, useEffect } from 'react'

import { connect } from 'react-redux'
import { compose, bindActionCreators } from 'redux'

import { Button } from 'reactstrap'

import Modal from 'components/Modal/Modal'
import Loader from 'components/Loader/Loader'
import Detail from 'components/business/common/Detail/Detail'

import actions from 'redux/client/problem/details/clientProblemDetailsActions'

import { isEmpty, isNotEmpty, DateUtils as DU } from 'lib/utils/Utils'

import './ProblemViewer.scss'

const { format, formats } = DU

const DATE_FORMAT = formats.longDateMediumTime12

const formatDate = date => format(date, DATE_FORMAT)

function renderTextProps(...props) {
    return props
        .filter(isNotEmpty)
        .reduce((result, value, index) => {
            if (index !== 0) {
                value = ' ' + value
            }

            return result + (!index ? '' : ' ') + value
        }, '')
}

const getCodeSet = ({ code, codeSet }) => {
    return renderTextProps(code, codeSet)
}

const getAgeObservation = ({ ageObservationValue, ageObservationUnit }) => {
    return renderTextProps(ageObservationValue, ageObservationUnit)
}

function SubDetail({ title, children }) {
    return isNotEmpty(children) && (
        <div className="ProblemSubDetail">
            <span className="ProblemSubDetail-Title">{title}</span>
            <span className="ProblemSubDetail-Value">{children}</span>
        </div>
    )
}

const mapStateToProps = state => ({
    state: state.client.problem.details,
})

const mapDispatchToProps = dispatch => ({
    actions: bindActionCreators(actions, dispatch)
})

function ProblemViewer({
    state,
    actions,

    isOpen,
    onClose,

    clientId,
    problemId,
}) {
    const { isFetching, data } = state

    let content = isFetching ? (
        <Loader />
    ) : (
            <div>No data</div>
        )

    if (!isEmpty(data)) {
        content = (
            <div className="ProblemViewer">
                <div className="ProblemViewer-Section">
                    <Detail title="PROBLEM NAME">
                        {data.name}
                    </Detail>
                    <Detail title="CODE, CODE SET">
                        {getCodeSet(data)}
                    </Detail>
                    <Detail title="PRIMARY">
                        {data.primary ? 'Yes' : (
                            data.primary === false ? 'No' : null
                        )}
                    </Detail>
                    <Detail title="TYPE">
                        {data.type}
                    </Detail>
                    <Detail title="STATUS">
                        {data.status}
                    </Detail>
                    <Detail title="IDENTIFIED DATE">
                        {formatDate(data.identifiedDate)}
                    </Detail>
                    <Detail title="RESOLVED DATE">
                        {formatDate(data.resolvedDate)}
                    </Detail>
                    <Detail title="ONSET DATE">
                        {formatDate(data.onsetDate)}
                    </Detail>
                    <Detail title="AGE OBSERVATION">
                        {getAgeObservation(data)}
                    </Detail>
                    <Detail title="DATE RECORDED">
                        {formatDate(data.recordedDate)}
                    </Detail>
                    <Detail title="RECORDED BY">
                        {data.recordedBy}
                    </Detail>
                    <Detail title="COMMENT">
                        {data.comments}
                    </Detail>
                    <Detail
                        title="Data Source"
                        valueClassName="d-flex flex-column"
                    >
                        <SubDetail title="Organization name">
                            {data.organizationName}
                        </SubDetail>
                        <SubDetail title="Community name">
                            {data.communityName}
                        </SubDetail>
                    </Detail>
                </div>
            </div>
        )
    }

    useEffect(() => {
        actions.load({
            clientId,
            problemId,
        })

        return actions.clear
    }, [actions, clientId, problemId])

    return (
        <Modal
            isOpen={isOpen}
            onClose={onClose}
            hasCloseBtn={false}
            title="View problem details"
            className="ProblemViewer"
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
    memo,
    connect(mapStateToProps, mapDispatchToProps)
)(ProblemViewer)
