import React, {
    memo,
    useState,
    useEffect,
    useCallback,
} from 'react'

import { compose, bindActionCreators } from 'redux'
import { connect } from 'react-redux'

import { Link } from 'react-router-dom'

import { noop } from 'underscore'

import { Form, Badge, Button } from 'reactstrap'

import { Table } from 'components'
import { WarningDialog } from 'components/dialogs'

import Actions from 'components/Table/Actions/Actions'
import CheckboxField from 'components/Form/CheckboxField/CheckboxField'

import LabResearchOrderViewer from '../LabResearchOrderViewer/LabResearchOrderViewer'

import * as errorActions from 'redux/error/errorActions'
import documentActions from 'redux/client/document/details/clientDocumentDetailsActions'
import reviewOrderActions from 'redux/lab/research/order/review/labResearchOrderReviewActions'

import {
    useResponse,
    useScrollable,
} from 'hooks/common'

import { usePendingReviewOrderList } from 'hooks/business/labs'

import { path } from 'lib/utils/ContextUtils'
import { getIconComponent } from 'lib/utils/FileUtils'
import { interpolate, DateUtils as DU } from 'lib/utils/Utils'

import './LabResultsReviewForm.scss'

const { format, formats } = DU

const DATE_FORMAT = formats.americanMediumDate

const formatDate = value => value ? format(value, DATE_FORMAT) : null

const scrollableStyles = { flex: 1 }

const getData = results => (
    results.filter(o => o.selected).map(o => o.id)
)

const MESSAGES = {
    CONFIRM_REVIEW: `By clicking on the "Mark as Reviewed" button, you confirm that $0 result(s) have been reviewed`,
}

const ICON_SIZE = 36

const mapState = state => ({
    review: state.lab.research.order.review
})

const mapDispatch = dispatch => ({
    actions: {
        ...bindActionCreators(reviewOrderActions, dispatch),
        error: bindActionCreators(errorActions, dispatch),
        document: bindActionCreators(documentActions, dispatch)
    }
})

function LabResultsReviewForm(
    {
        onClose,
        actions,
        review,
        onSubmitSuccess,
        communityIds = [],
        organizationId,
    }
) {
    let [isFetching, setIsFetching] = useState(false)
    let [isConfirmReviewDialogOpen, setIsConfirmReviewDialogOpen] = useState(false)
    let [selected, setSelected] = useState(false)

    let {
        state,
        fetch, 
        toggleSelected: onToggleSelected,
        toggleAllSelected: onToggleAllSelected,
    } = usePendingReviewOrderList({ communityIds, organizationId })

    let isChanged = state.isChanged()
    let selectedCount = state.getSelectedCount()

    const { data } = state.dataSource
    const { Scrollable } = useScrollable()

    function cancel() {
        onClose(isChanged)
    }

    let onResponse = useResponse({
        onFailure: actions.error.change,
        onSuccess: useCallback(({ data }) => onSubmitSuccess(data), [onSubmitSuccess]),
        onUnknown: actions.error.change
    })

    function submit() {
        async function sendRequest() {
            setIsFetching(true)

            try {
                onResponse(
                    await actions.load(getData(data))
                )
            } catch (error) {
                actions.error.change(error)
            } finally {
                setIsFetching(false)
            }
        }

        sendRequest()
    }

    function fetchIfNeed() {
        if (review.fetchCount === 1) {
            fetch()
        }
    }

    let onDownloadDocument = documentData => {
        actions.document.download({
            ...documentData,
            documentId: documentData.id,
        })
    }

    let onSubmit = useCallback(submit, [data, actions.load, onResponse])
    let onCancel = useCallback(cancel, [onClose, isChanged])

    useEffect(() => { fetch() }, [fetch])

    useEffect(fetchIfNeed, [fetch, review.fetchCount])

    return (
        <>
            <Form className="LabResultsReviewForm">
                <Scrollable style={scrollableStyles}>
                    <div className="LabResultsReviewForm-Section">
                        <Table
                            hasHover
                            hasOptions
                            keyField="id"
                            title="Pending Lab Results"
                            isLoading={state.isFetching}
                            className="LabResultsList"
                            containerClass="LabResultsContainer"
                            data={data}
                            columns={[
                                {
                                    dataField: 'selected',
                                    text: '',
                                    headerStyle: {
                                        width: '65px',
                                    },
                                    formatter: (v, row) => (
                                        <CheckboxField
                                            name={row.id}
                                            value={v}
                                            className='LabResultsReviewForm-CheckboxField'
                                            onChange={onToggleSelected}
                                        />
                                    ),
                                    headerClasses: 'LabResultsReviewForm-SelectColHeader',
                                    headerFormatter: () => (
                                        <CheckboxField
                                            name="selectAll"
                                            value={!state.isFetching && state.isAllSelected()}
                                            className='LabResultsReviewForm-CheckboxField'
                                            onChange={onToggleAllSelected}
                                        />
                                    )
                                },
                                {
                                    dataField: 'clientName',
                                    text: 'Client Name',
                                },
                                {
                                    dataField: 'orderDate',
                                    text: 'Order Date',
                                    headerAlign: 'right',
                                    align: 'right',
                                    formatter: formatDate,
                                },
                                {
                                    dataField: 'documents',
                                    text: 'Results',
                                    formatter: (v, row) => (
                                        v && v.map(document => {
                                            const Icon = getIconComponent({ mimeType: document.mimeType })

                                            return (
                                                <div className="LabResultsReviewForm-Documents" key={document.id}>
                                                    <Icon className="LabResultsReviewForm-DocumentIcon" />
                                                    <Actions
                                                        data={document}
                                                        hasDownloadAction
                                                        iconSize={ICON_SIZE}
                                                        onDownload={onDownloadDocument}
                                                        downloadHintMessage="Download document"
                                                    />
                                                </div>
                                            )
                                        })
                                    )
                                },
                                {
                                    dataField: '@actions',
                                    text: 'Actions',
                                    formatter: (v, row) => (
                                        <span
                                            className="LabResultsReviewForm-ViewDetailsBtn"
                                            onClick={() => setSelected(row)}
                                        >
                                            View Order
                                        </span>
                                    )
                                },
                            ]}
                            columnsMobile={['selected', 'clientName']}
                            renderCaption={title => (
                                <div className="LabResultsReviewForm-Caption">
                                    {title}

                                    <Badge
                                        color='info'
                                        className="LabResultsReviewForm-Count"
                                    >
                                        {data.length}
                                    </Badge>
                                </div>
                            )}
                        />
                    </div>
                </Scrollable>

                <div className="LabResultsReviewForm-Buttons">
                    <Button
                        outline
                        color="success"
                        onClick={onCancel}
                    >
                        Cancel
                    </Button>

                    <Button
                        color="success"
                        onClick={() => setIsConfirmReviewDialogOpen(true)}
                        disabled={state.isFetching || isFetching || !isChanged}
                    >
                        Mark as Reviewed {selectedCount > 0 ? `(${selectedCount})` : ''}
                    </Button>
                </div>
            </Form>

            {isConfirmReviewDialogOpen && (
                <WarningDialog
                    isOpen
                    title={interpolate(MESSAGES.CONFIRM_REVIEW, selectedCount)}
                    buttons={[
                        {
                            color: 'success',
                            outline: true,
                            text: 'Cancel',
                            onClick: () => setIsConfirmReviewDialogOpen(false)
                        },
                        {
                            color: 'success',
                            disabled: isFetching,
                            text: 'Mark as Reviewed',
                            onClick: onSubmit
                        },
                    ]}
                />
            )}

            {!!selected && (
                <LabResearchOrderViewer
                    orderId={selected.id}
                    isOpen={true}
                    onClose={() => setSelected(null)}
                />
            )}
        </>
    )
}

export default compose(
    memo,
    connect(mapState, mapDispatch)
)(LabResultsReviewForm)
