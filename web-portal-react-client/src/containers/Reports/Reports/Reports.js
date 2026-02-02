import React, {
    useState,
    useEffect,
    useCallback
} from 'react'

import cn from 'classnames'

import { connect } from 'react-redux'
import { bindActionCreators } from 'redux'

import { Collapse } from 'reactstrap'

import DocumentTitle from 'react-document-title'

import SideBar from 'containers/SideBar/SideBar'

import {
    Loader,
    ErrorViewer
} from 'components'

import {
    useSideBarUpdate
} from 'hooks/common/redux'

import {
    useCanViewSDoHReportsQuery
} from 'hooks/business/reports'

import * as reportDocumentActions from 'redux/report/document/reportDocumentActions'

import { SERVER_ERROR_CODES } from 'lib/Constants'

import { ReactComponent as Filter } from 'images/filters.svg'

import { getSideBarItems } from '../SideBarItems'
import ReportFilter from './ReportFilter/ReportFilter'

import './Reports.scss'

function isIgnoredError(e = {}) {
    return e.code === SERVER_ERROR_CODES.ACCOUNT_INACTIVE
}

function mapStateToProps(state) {
    return {
        document: state.report.document,
        canViewSDoHReports: state.report.sdoh.can.view.value
    }
}

function mapDispatchToProps(dispatch) {
    return {
        actions: {
            document: bindActionCreators(reportDocumentActions, dispatch)
        }
    }
}

function Reports({ document, actions, canViewSDoHReports }) {
    const {
        error,
        isFetching
    } = document

    const [isFilterOpen, setIsFilterOpen] = useState(true)

    const onToggleFilter = useCallback(() => {
        setIsFilterOpen(!isFilterOpen)
    }, [isFilterOpen])

    const onResetError = useCallback(() => {
        actions.document.clearError()
    }, [actions])

    useCanViewSDoHReportsQuery()

    const update = useSideBarUpdate()

    useEffect(() => {
        update({
            isHidden: !canViewSDoHReports,
            items: getSideBarItems()
        })
    }, [update, canViewSDoHReports])

    return (
        <DocumentTitle title="Simply Connect | Reports">
            <div className="Reports">
                <div className="d-flex flex-row justify-content-between margin-top-30 margin-bottom-15">
                    <div className="Reports-Title">
                        Reports
                    </div>
                    <Filter
                        className={cn(
                            'ReportFilter-Icon',
                            isFilterOpen
                                ? 'ReportFilter-Icon_rotated_90'
                                : 'ReportFilter-Icon_rotated_0',
                        )}
                        onClick={onToggleFilter}
                    />
                </div>
                <Collapse isOpen={isFilterOpen}>
                    <ReportFilter />
                </Collapse>
                {isFetching && <Loader />}
                {error && !isIgnoredError(error) && (
                    <ErrorViewer
                        isOpen
                        error={error}
                        onClose={onResetError}
                    />
                )}
            </div>
        </DocumentTitle>
    )
}

export default connect(mapStateToProps, mapDispatchToProps)(Reports)
