import React, {
    memo,
    useState,
    useEffect,
    useCallback
} from 'react'

import cn from 'classnames'

import { connect } from 'react-redux'
import { compose, bindActionCreators } from 'redux'

import {
    Badge,
    Collapse
} from 'reactstrap'

import DocumentTitle from 'react-document-title'

import {
    useListState
} from 'hooks/common'

import {
    useAuthUser
} from 'hooks/common/redux'

import {
    useClientRecordsQuery,
    useClientAccessRequestSubmit
} from 'hooks/business/client/records'

import {
    Footer,
    ErrorViewer
} from 'components'

import {
    LoadingDialog,
    ConfirmDialog,
    SuccessDialog
} from 'components/dialogs'

import listActions from 'redux/client/record/list/clientRecordListActions'

import { ReactComponent as Filter } from 'images/filters.svg'

import FilterEntity from 'entities/ClientRecordFilter'

import ClientRecordList from './ClientRecordList/ClientRecordList'
import ClientRecordFilter from './ClientRecordFilter/ClientRecordFilter'

import { PROFESSIONAL_SYSTEM_ROLES } from './Constants'

import './ClientRecords.scss'

function mapStateToProps(state) {
    return {
        isFilterOpen: state.client.record.list.isFilterOpen
    }
}

function mapDispatchToProps(dispatch) {
    return {
        actions: bindActionCreators(listActions, dispatch)
    }
}

function ClientRecords({ isFilterOpen, actions }) {
    const {
        state,
        setError,
        clearError,
        changeFilter
    } = useListState({ filterEntity: FilterEntity })

    const [selected, setSelected] = useState(null)
    const [isNoData, setIsNoData] = useState(false)
    const [shouldFetch, setShouldFetch] = useState(false)
    const [accessRequestError, setAccessRequestError] = useState(null)

    const [isConfirmAccessRequestPopupOpen, toggleConfirmAccessRequestPopup] = useState(false)
    const [isAccessRequestSuccessPopupOpen, toggleAccessRequestSuccessPopup] = useState(false)

    const user = useAuthUser()

    const isProfessionalRole = (
        PROFESSIONAL_SYSTEM_ROLES.includes(user?.roleName)
    )

    const {
        sort,
        refresh,
        isFetching,
        pagination,
        data: { data } = {}
    } = useClientRecordsQuery(state.filter.toJS(), {
        retry: 1,
        onError: setError,
        enabled: shouldFetch
    })

    const {
        mutateAsync: submitAccessRequest,
        isLoading: isSubmittingAccessRequest
    } = useClientAccessRequestSubmit(
        { clientId: selected?.id },
        { throwOnError: true }
    )

    const onToggleFilter = useCallback(() => {
        actions.toggleFilter(!isFilterOpen)
    }, [actions, isFilterOpen])

    const clear = useCallback(() => {
        setIsNoData(true)
    }, [])
    
    const onSort = useCallback((field, order) => {
        sort(field, order)
        setShouldFetch(true)
    }, [sort])

    const onRefresh = useCallback(page => {
        refresh(page)
        setIsNoData(false)
        setShouldFetch(true)
    }, [refresh])

    const onRequestAccess = useCallback(o => {
        setSelected(o)
        toggleConfirmAccessRequestPopup(true)
    }, [])

    const onConfirmAccessRequest = useCallback(async () => {
        toggleConfirmAccessRequestPopup(false)

        try {
            await submitAccessRequest()

            refresh()
            setShouldFetch(true)
            toggleAccessRequestSuccessPopup(true)
        } catch (e) {
            setAccessRequestError(e)
            toggleConfirmAccessRequestPopup(false)
        }

    }, [refresh, submitAccessRequest])

    const onCancelAccessRequest = useCallback(() => {
        setSelected(null)
        toggleConfirmAccessRequestPopup(false)
    }, [])

    useEffect(() => {
        if (shouldFetch) {
            setShouldFetch(false)
        }
    }, [shouldFetch])

    return (
        <DocumentTitle title="Simply Connect | Record Search">
            <>
                <div className="ClientRecords">
                    <div className="ClientRecords-Header">
                        <div className="ClientRecords-HeaderItem">
                            <div className="ClientRecords-Title">
                                <div className="ClientRecords-TitleText">
                                    Record Search
                                </div>
                                {!isNoData && data?.length > 0 ? (
                                    <Badge color='info' className="Badge Badge_place_top-right">
                                        {data?.length}
                                    </Badge>
                                ) : null}
                            </div>
                        </div>
                        <div className="ClientRecords-HeaderItem">
                            <div className="ClientRecords-Actions">
                                <Filter
                                    className={cn(
                                        'ClientRecordFilter-Icon',
                                        isFilterOpen
                                            ? 'ClientRecordFilter-Icon_rotated_90'
                                            : 'ClientRecordFilter-Icon_rotated_0'
                                    )}
                                    onClick={onToggleFilter}
                                />
                            </div>
                        </div>
                    </div>
                    <Collapse isOpen={isFilterOpen}>
                        <ClientRecordFilter
                            onReset={clear}
                            onApply={onRefresh}
                            onRestore={onRefresh}
                            onChange={changeFilter}
                            className="margin-bottom-50"
                            canRequestAccess={isProfessionalRole}
                        />
                    </Collapse>
                    <ClientRecordList
                        data={isNoData ? [] : data}
                        isFetching={isFetching}
                        pagination={pagination}
                        canRequestAccess={isProfessionalRole}
                        onRefresh={onRefresh}
                        onSort={onSort}
                        onRequestAccess={onRequestAccess}
                    />

                    {isSubmittingAccessRequest && (
                        <LoadingDialog isOpen/>
                    )}

                    {isConfirmAccessRequestPopupOpen && (
                        <ConfirmDialog
                            isOpen
                            title="Are you sure you want to save user to your Clients?"
                            confirmBtnText="Save"
                            onConfirm={onConfirmAccessRequest}
                            onCancel={onCancelAccessRequest}
                        />
                    )}

                    {isAccessRequestSuccessPopupOpen && (
                        <SuccessDialog
                            isOpen
                            title="The Client has been saved successfully."
                            buttons={[{
                                text: 'Close',
                                onClick: () => {
                                    toggleAccessRequestSuccessPopup(false)
                                }
                            }]}
                        />
                    )}

                    {state.error && (
                        <ErrorViewer
                            isOpen
                            error={state.error}
                            onClose={clearError}
                        />
                    )}

                    {accessRequestError && (
                        <ErrorViewer
                            isOpen
                            error={accessRequestError}
                            onClose={() => setAccessRequestError(null)}
                        />
                    )}
                </div>
                <Footer theme='gray'/>
            </>
        </DocumentTitle>
    )
}

export default compose(
    memo,
    connect(mapStateToProps, mapDispatchToProps)
)(ClientRecords)