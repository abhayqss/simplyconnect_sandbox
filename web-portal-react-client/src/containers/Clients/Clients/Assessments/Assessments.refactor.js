import React, {
    Component,
    useMemo,
    useState,
    useEffect,
    useCallback
} from 'react'

import {
    compact,
} from 'underscore'

import { connect } from 'react-redux'
import { bindActionCreators, compose } from 'redux'

import {
    withRouter,
    useParams
} from 'react-router-dom'

import { withQueryCache } from 'hocs'
import {withAssessmentUtils} from 'hocs/clients'
import {
    useToggle
} from 'hooks/common'

import {
    useAuthUser
} from 'hooks/common/redux'

import {
    useAssessmentTypesQuery
} from 'hooks/business/directory/query'

import {
    useClientQuery
} from 'hooks/business/client/queries'

import {
    useCanDownloadInTuneReportQuery,
    useCanGenerateInTuneReportQuery
} from 'hooks/business/client/assessments'

import DocumentTitle from 'react-document-title'

import {
    Row,
    Col,
    Badge,
    UncontrolledTooltip as Tooltip
} from 'reactstrap'

import {
    Table,
    SearchField,
    Breadcrumbs,
    ErrorViewer
} from 'components'

import {
    Button,
    AddButton,
    EditButton,
    HideButton,
    RefreshButton,
    DownloadButton
} from 'components/buttons'

import {
    SuccessDialog,
    ConfirmDialog
} from 'components/dialogs'

import {
    LoadAssessmentTypesAction
} from 'actions/directory'

import {
    UpdateSideBarAction,
    LoadClientDetailsAction,
    LoadCanGenerateInTuneReportAction,
    LoadCanDownloadInTuneReportAction
} from 'actions/clients'

import * as assessmentListActions from 'redux/client/assessment/list/assessmentListActions'
import inTuneReportDetailsActions from 'redux/client/assessment/report/in-tune/details/inTuneReportDetailsActions'

import {
    PAGINATION,
    SYSTEM_ROLES,
    ASSESSMENT_TYPES,
    ASSESSMENT_STATUSES,
    SERVER_ERROR_CODES
} from 'lib/Constants'

import { isEmpty, DateUtils, toNumberExcept } from 'lib/utils/Utils'

import { ReactComponent as Warning } from 'images/alert-yellow.svg'

import AssessmentViewer from './AssessmentViewer/AssessmentViewer'
import AssessmentEditor from './AssessmentEditor/AssessmentEditor'
import AssessmentVisibilityEditor from './AssessmentVisibilityEditor/AssessmentVisibilityEditor'

import './Assessments.scss'

const { FIRST_PAGE } = PAGINATION

const { format, formats } = DateUtils

const DATE_FORMAT = formats.americanMediumDate

const {
    HOME_CARE_ASSISTANT
} = SYSTEM_ROLES

const SYS_ROLES_WITH_NOT_VIEWABLE_CLIENT = [
    HOME_CARE_ASSISTANT
]

const {
    GAD7,
    PHQ9,
    IN_HOME,
    CARE_MGMT,
    ARIZONA_SSM,
    IN_HOME_CARE,
    COMPREHENSIVE,
    NOR_CAL_COMPREHENSIVE,
    BENEFICIARY_SCREENING_DIABETIC_FOOT_AND_EYE_EXAM,
    BENEFICIARY_COLORECTAL_CANCER_SCREENING,
    BENEFICIARY_MAMMOGRAM_SCREENING,
    BENEFICIARY_SCREENING_FOR_TYPE_II_DIABETES
} = ASSESSMENT_TYPES

const { HIDDEN } = ASSESSMENT_STATUSES

const STATUS_COLORS = {
    HIDDEN: '#e0e0e0',
    INACTIVE: '#e0e0e0',
    COMPLETED: '#d1ebfe',
    IN_PROCESS: '#d5f3b8'
}

function isIgnoredError (e = {}) {
    return e.code === SERVER_ERROR_CODES.ACCOUNT_INACTIVE
}

function mapStateToProps (state) {
    const {
        document,
        assessment,
        servicePlan,
    } = state.client

    return {
        error: assessment.list.error,
        isFetching: assessment.list.isFetching,
        fetchCount: assessment.list.fetchCount,
        dataSource: assessment.list.dataSource,
        shouldReload: assessment.list.shouldReload,

        auth: state.auth,
        client: state.client,
        directory: state.directory,

        count: assessment.count.value,
        canAdd: assessment.can.add.value,
        canDownloadInTuneReport: assessment.report.inTune.can.download,
        canGenerateInTuneReport: assessment.report.inTune.can.generate.value,

        documentCount: document.count.value,
        servicePlanCount: servicePlan.count.value,
        eventCount: state.event.note.composed.count.value,
    }
}

function mapDispatchToProps (dispatch) {
    return {
        actions: {
            ...bindActionCreators(assessmentListActions, dispatch),
            report: {
                inTune: {
                    details: bindActionCreators(inTuneReportDetailsActions, dispatch)
                }
            }
        },
    }
}

function AssessmentsX() {
    const [selected, setSelected] = useState(null)
    const [selectedArchived, setSelectedArchived] = useState(null)

    const [isCopying, toggleCopying] = useToggle()
    const [isEditorOpen, toggleEditor] = useToggle()
    const [isViewerOpen, toggleViewer] = useToggle()
    const [shouldOpenViewer, setShouldOpenViewer] = useState()
    const [isArchiveViewerOpen, toggleArchiveViewer] = useToggle()
    const [isVisibilityEditorOpen, toggleVisibilityEditor] = useToggle()
    const [isSaveSuccessDialogOpen, toggleSaveSuccessDialog] = useToggle()
    const [isActivityChangeSuccessDialogOpen, toggleActivityChangeSuccessDialog] = useToggle()
    const [isVisibilityChangeSuccessDialogOpen, toggleVisibilityChangeSuccessDialog] = useToggle()
    const [isEditCancelConfirmDialogOpen, toggleEditCancelConfirmDialog] = useToggle()
    const [isCompleteSuccessDialogOpen, toggleCompleteSuccessDialog] = useToggle()

    const user = useAuthUser()

    const params = useParams()

    const clientId = toNumberExcept(
        params.clientId, [null, undefined]
    )

    const {
        data: client = {}
    } = useClientQuery(
        { clientId },
        { enabled: Boolean(clientId) }
    )

    const {
        data: types = {}
    } = useAssessmentTypesQuery(
        {
            clientId,
            types: [
                GAD7,
                PHQ9,
                IN_HOME,
                CARE_MGMT,
                ARIZONA_SSM,
                IN_HOME_CARE,
                COMPREHENSIVE,
                NOR_CAL_COMPREHENSIVE
            ]
        },
        { enabled: Boolean(clientId) }
    )

    const preselectedType = useMemo(() => (
        user?.roleName === HOME_CARE_ASSISTANT && (
            types.map(o => o.types).flat()
                .find(type => type.name === CARE_MGMT)
        )
    ), [user, types])

    const canAddComprehensive = useMemo(() => (
        types.map(o => o.types).flat()
            .find(type => type.name.includes(COMPREHENSIVE))?.canAdd
    ), [types])


    const {
        data: canDownloadInTuneReport
    } = useCanDownloadInTuneReportQuery(
        { clientId }, { enabled: Boolean(clientId) }
    )

    const {
        canAdd,
        isFetching,
        fetchCount,
        dataSource: ds,
        canGenerateInTuneReport,
    } = this.props

    return (
        <DocumentTitle
            title={`Simply Connect | Clients | ${client?.fullName} | Assessments`}>
            <div className="Assessments">
                <LoadCanGenerateInTuneReportAction
                    isMultiple
                    params={{
                        clientId,
                        assessmentFetchCount: fetchCount
                    }}
                    shouldPerform={prevParams => (
                        fetchCount !== prevParams.assessmentFetchCount
                    )}
                />
                <LoadCanDownloadInTuneReportAction
                    isMultiple
                    params={{
                        clientId,
                        assessmentFetchCount: fetchCount
                    }}
                    shouldPerform={prevParams => (
                        fetchCount !== prevParams.assessmentFetchCount
                    )}
                />
                <UpdateSideBarAction
                    params={{ clientId, shouldRefresh: isFetching && fetchCount > 1 }}
                />
                <Breadcrumbs items={compact([
                    { title: 'Clients', href: '/clients', isEnabled: true },
                    client.details.data && {
                        title: `${client?.fullName}`,
                        href: `/clients/${clientId || 1}`,
                        isActive: !this.canViewClient()
                    },
                    client.details.data && {
                        title: 'Assessments',
                        href: `/clients/${clientId || 1}/assessments`,
                        isActive: true
                    }
                ])}/>
                <Table
                    hasHover
                    hasOptions
                    hasPagination
                    keyField='id'
                    title='Assessments'
                    isLoading={isFetching}
                    className='AssessmentList'
                    containerClass='AssessmentListContainer'
                    data={ds.data}
                    pagination={ds.pagination}
                    columns={[
                        {
                            dataField: 'typeTitle',
                            text: 'Assessment',
                            sort: true,
                            headerClasses: 'ClientList-Header-AssessmentName',
                            onSort: this.onSort,
                            formatter: (v, row, index, formatExtraData, isMobile) => {
                                return (
                                    <>
                                            <span
                                                id={`${isMobile ? 'm-' : ''}assessment-${row.id}`}
                                                className='AssessmentList-AssessmentName'
                                                onClick={() => this.onView(row)}>
                                                {v}
                                            </span>
                                        <Tooltip
                                            className='AssessmentList-Tooltip'
                                            placement="top"
                                            target={`${isMobile ? 'm-' : ''}assessment-${row.id}`}
                                            modifiers={[
                                                {
                                                    name: 'offset',
                                                    options: { offset: [0, 6] }
                                                },
                                                {
                                                    name: 'preventOverflow',
                                                    options: { boundary: document.body }
                                                }
                                            ]}
                                        >
                                            View Assessment
                                        </Tooltip>
                                    </>
                                )
                            }
                        },
                        {
                            dataField: 'status',
                            text: 'Status',
                            sort: true,
                            align: 'left',
                            headerAlign: 'left',
                            onSort: this.onSort,
                            formatter: (v, row) => {
                                return (
                                    <span
                                        style={{ backgroundColor: STATUS_COLORS[row.status.name] }}
                                        className='AssessmentList-AssessmentStatus'>
                                       {row.status.title}
                                    </span>
                                )
                            }
                        },
                        {
                            dataField: 'dateStarted',
                            text: 'Date Started',
                            sort: true,
                            align: 'right',
                            headerAlign: 'right',
                            headerClasses: 'AssessmentList-DateStartedCol',
                            onSort: this.onSort,
                            formatter: v => v && format(v, DATE_FORMAT)
                        },
                        {
                            dataField: 'dateCompleted',
                            text: 'Date Completed',
                            sort: true,
                            align: 'right',
                            headerAlign: 'right',
                            headerClasses: 'AssessmentList-DateCompletedCol',
                            onSort: this.onSort,
                            formatter: v => v && format(v, DATE_FORMAT)
                        },
                        {
                            dataField: 'author',
                            text: 'Author',
                            sort: true,
                            align: 'left',
                            onSort: this.onSort,
                        },
                        {
                            dataField: 'score',
                            text: 'Score',
                            align: 'left',
                        },
                        {
                            dataField: '@actions',
                            text: '',
                            align: 'right',
                            headerClasses: 'AssessmentList-ActionCol',
                            formatter: (v, row) => {
                                return (
                                    <div className="position-relative AssessmentList-Actions">
                                        {row.typeName?.includes(COMPREHENSIVE) && row.status.name !== HIDDEN && row.canHide && (
                                            <HideButton
                                                name={`assessment-${row.id}_hide-btn`}
                                                tipText="Hide the assessment"
                                                onClick={() => this.onOpenVisibilityEditor(row)}
                                                className="AssessmentList-ActionBtn"
                                            />
                                        )}
                                        {row.typeName?.includes(COMPREHENSIVE) && row.status.name === HIDDEN && row.canRestore && (
                                            <RefreshButton
                                                name={`assessment-${row.id}_restore-btn`}
                                                tipText="Restore the assessment"
                                                onClick={() => this.onOpenVisibilityEditor(row)}
                                                className="AssessmentList-ActionBtn"
                                            />
                                        )}
                                        {canAdd && canAddComprehensive && row.typeName?.includes(COMPREHENSIVE) && (
                                            <AddButton
                                                name={`add-copy-${row.id}`}
                                                tipText="Create a copy of assessment"
                                                onClick={() => this.onCopy(row)}
                                                className="AssessmentList-ActionBtn"
                                            />
                                        )}
                                        {[GAD7, PHQ9, IN_HOME, CARE_MGMT, ARIZONA_SSM, IN_HOME_CARE, COMPREHENSIVE, NOR_CAL_COMPREHENSIVE].includes(row.typeName) && (
                                            <>
                                                <DownloadButton
                                                    name={`download-${row.id}`}
                                                    {...!row.typeName?.includes(COMPREHENSIVE) && {
                                                        tipText: 'Download Pdf File'
                                                    }}
                                                    onClick={() => this.onDownload(row)}
                                                    className="AssessmentList-ActionBtn"
                                                />
                                                {row.typeName?.includes(COMPREHENSIVE) && (
                                                    <Tooltip
                                                        trigger="focus"
                                                        placement="bottom"
                                                        target={`download-${row.id}`}
                                                        innerClassName="DownloadOptionPicker"
                                                        modifiers={[
                                                            {
                                                                name: 'offset',
                                                                options: { offset: [0, 6] }
                                                            },
                                                            {
                                                                name: 'preventOverflow',
                                                                options: { boundary: document.body }
                                                            }
                                                        ]}
                                                    >
                                                        <div
                                                            onClick={() => {
                                                                this.downloadPdf(row.id, row.typeName)
                                                            }}
                                                            className="DownloadOptionPicker-Item"
                                                        >
                                                            Download Pdf File
                                                        </div>
                                                        <div
                                                            onClick={() => {
                                                                this.downloadJson(row.id)
                                                            }}
                                                            className="DownloadOptionPicker-Item"
                                                        >
                                                            Download JSON file
                                                        </div>
                                                    </Tooltip>
                                                )}
                                            </>
                                        )}
                                        {row.canEdit && (
                                            <EditButton
                                                name={`edit-${row.id}`}
                                                tipText="Edit assessment"
                                                onClick={() => this.onEdit(row)}
                                                className="AssessmentList-ActionBtn"
                                            />
                                        )}
                                    </div>
                                )
                            }
                        }
                    ]}
                    columnsMobile={['typeTitle', 'author']}
                    noDataText="No assessments"
                    renderCaption={(title, isMobile) => {
                        return (
                            <div className='AssessmentList-Caption'>
                                <div className="Assessments-Header">
                                    <div className="Assessments-Title">
                                                <span className='Assessments-TitleText'>
                                                    {title}
                                                </span>
                                        <span className="text-nowrap line-height-2">
                                                    <span className="Assessments-ClientName">
                                                        {client.details.data && (' / ' + client.details.data.fullName)}
                                                    </span>
                                            {ds.pagination.totalCount ? (
                                                <Badge color='info' className='Badge Badge_place_top-right'>
                                                    {ds.pagination.totalCount}
                                                </Badge>
                                            ) : null}
                                                </span>
                                    </div>
                                    <div className="Assessments-Actions">
                                        {(canDownloadInTuneReport.value || !!canDownloadInTuneReport.error) && (
                                            <Button
                                                color='success'
                                                id={`run-in-tune-report${isMobile ? '-mobile' : ''}`}
                                                hasTip={!canGenerateInTuneReport?.value}
                                                disabled={!canGenerateInTuneReport?.value}
                                                tipText={canGenerateInTuneReport?.reasonText}
                                                className='Assessments-Action InTuneReportBtn'
                                                title="Run InTune Report"
                                                onClick={this.onDownloadInTuneReport}
                                            >
                                                <span className="AddAssessmentBtn-OptText">Run InTune&nbsp;</span>Report
                                            </Button>
                                        )}
                                        {canAdd && (
                                            <Button
                                                color='success'
                                                className='Assessments-Action AddAssessmentBtn'
                                                title="Add New Assessment"
                                                onClick={this.onAdd}
                                            >
                                                Add New<span className="AddAssessmentBtn-OptText">&nbsp;Assessment</span>
                                            </Button>
                                        )}
                                    </div>
                                </div>
                                <div className='Assessments-Filter'>
                                    <Row>
                                        <Col md={6} lg={4}>
                                            <SearchField
                                                name='name'
                                                value={ds.filter.name}
                                                placeholder='Search'
                                                onChange={this.onChangeFilterField}
                                                onClear={this.onChangeFilterField}
                                            />
                                        </Col>
                                    </Row>
                                </div>
                            </div>
                        )
                    }}
                    onRefresh={this.onRefresh}
                />
                {isViewerOpen && (
                    <AssessmentViewer
                        isOpen
                        assessmentId={selected?.id}
                        assessmentTypeId={selected?.typeId}
                        onView={this.onViewArchived}
                        onClose={this.onCloseViewer}
                    />
                )}
                {isArchiveViewerOpen && (
                    <AssessmentViewer
                        isOpen
                        isAssessmentArchived
                        assessmentId={selectedArchived?.id}
                        assessmentTypeId={selectedArchived?.typeId}
                        onClose={this.onCloseArchiveViewer}
                    />
                )}
                {isEditorOpen && (
                    <AssessmentEditor
                        isOpen

                        clientId={clientId}
                        isCopying={isCopying}
                        assessmentId={selected?.id}
                        assessmentTypeId={preselectedType?.id}
                        shouldAddNeedsToServicePlan={selected?.shouldAddNeedsToServicePlan}

                        onClose={this.onCloseEditor}
                        onSaveSuccess={this.onSaveSuccess}
                        onCompleteSuccess={this.onCompleteSuccess}
                        onChangeActivitySuccess={this.onChangeActivitySuccess}
                    />
                )}
                <AssessmentVisibilityEditor
                    isOpen={isVisibilityEditorOpen}
                    clientId={clientId}
                    assessmentId={selected?.id}
                    assessmentStatus={selected?.status?.name}
                    onClose={this.onCloseVisibilityEditor}
                    onSaveSuccess={this.onChangeVisibilitySuccess}
                />
                {isEditCancelConfirmDialogOpen && (
                    <ConfirmDialog
                        isOpen
                        icon={Warning}
                        confirmBtnText='OK'
                        title='The updates will not be saved'
                        onConfirm={this.onCloseEditor}
                        onCancel={this.onCloseEditCancelConfirmDialog}
                    />
                )}
                {isCompleteSuccessDialogOpen && (
                    <SuccessDialog
                        isOpen
                        title={'The assessment has been completed.' + (
                            [ARIZONA_SSM].includes(selected?.typeName) ? ` Score is ${selected?.score}` : ''
                        )}
                        buttons={[
                            {
                                outline: true,
                                text: 'Close',
                                className: 'min-width-170',
                                onClick: this.onCompleteSave
                            },
                            {
                                text: 'Back to assessment',
                                className: 'min-width-170',
                                onClick: this.onBackToEditor
                            }
                        ]}
                    />
                )}
                {isSaveSuccessDialogOpen && selected?.typeName === COMPREHENSIVE && (
                    <SuccessDialog
                        isOpen
                        title="The updates have been saved"
                        buttons={[
                            {
                                outline: true,
                                text: 'Close',
                                className: 'min-width-170',
                                onClick: this.onCompleteSave
                            },
                            {
                                text: 'Back to assessment',
                                className: 'min-width-170',
                                onClick: this.onBackToEditor
                            }
                        ]}
                    />
                )}
                {isSaveSuccessDialogOpen && [IN_HOME, IN_HOME_CARE].includes(selected?.typeName) && (
                    <SuccessDialog
                        isOpen
                        title={
                            `The assessment has been completed. ${
                                selected.notAddedToServicePlanNeedCount ? (
                                    'Do you want to create/update a service plan?'
                                ) : ''}`
                        }
                        buttons={selected.notAddedToServicePlanNeedCount ? (
                            [
                                {
                                    outline: true,
                                    text: 'No',
                                    className: 'min-width-170',
                                    onClick: this.onCompleteSave
                                },
                                {
                                    text: 'Yes',
                                    className: 'min-width-170',
                                    onClick: () => {
                                        this.onBackToEditor({ shouldAddNeedsToServicePlan: true })
                                    }
                                }
                            ]
                        ) : ([
                            {
                                text: 'OK',
                                onClick: this.onCompleteSave
                            }
                        ])}
                    />
                )}
                {isSaveSuccessDialogOpen && ![IN_HOME, IN_HOME_CARE, ARIZONA_SSM, COMPREHENSIVE].includes(selected?.typeName) && (
                    <SuccessDialog
                        isOpen
                        title="The assessment has been completed."
                        buttons={[
                            {
                                text: 'OK',
                                onClick: this.onCompleteSave
                            }
                        ]}
                    />
                )}
                {isSaveSuccessDialogOpen && [ARIZONA_SSM].includes(selected?.typeName) && (
                    <SuccessDialog
                        isOpen
                        title="The assessment has been saved."
                        buttons={[
                            {
                                outline: true,
                                text: 'Close',
                                className: 'min-width-170',
                                onClick: this.onCompleteSave
                            },
                            {
                                text: 'Back to assessment',
                                className: 'min-width-170',
                                onClick: this.onBackToEditor
                            }
                        ]}
                    />
                )}
                {isChangeActivitySuccessDialogOpen && selected && (
                    <SuccessDialog
                        isOpen
                        title={selected.isInactive ?
                            'The assessment has been marked as inactive'
                            : 'The assessment is in process'}
                        buttons={[
                            {
                                text: 'Ok',
                                onClick: this.onCompleteSave
                            }
                        ]}
                    />
                )}
                {isChangeVisibilitySuccessDialogOpen && (
                    <SuccessDialog
                        isOpen
                        title={`The assessment has been ${selected?.status?.name === HIDDEN ? 'restored' : 'hidden'}.`}
                        buttons={[
                            {
                                text: 'Close',
                                onClick: () => this.setState({
                                    selected: null,
                                    isChangeVisibilitySuccessDialogOpen: false
                                })
                            }
                        ]}
                    />
                )}
                {this.error && !isIgnoredError(this.error) && (
                    <ErrorViewer
                        isOpen
                        error={this.error}
                        onClose={this.onResetError}
                    />
                )}
            </div>
        </DocumentTitle>
    )
}

class Assessments extends Component {

    state = {
        selected: null,
        selectedArchived: null,

        isCopying: false,
        isEditorOpen: false,
        isViewerOpen: false,
        shouldOpenViewer: false,
        isArchiveViewerOpen: false,
        isVisibilityEditorOpen: false,

        isSaveSuccessDialogOpen: false,
        isChangeActivitySuccessDialogOpen: false,
        isChangeVisibilitySuccessDialogOpen: false,

        isEditCancelConfirmDialogOpen: false,
        isCompleteSuccessDialogOpen: false
    }

    componentDidMount () {
        this.refresh()

        const {
            state
        } = this.props.location

        if (state) {
            const {
                assessmentId,
                assessmentTypeName
            } = state

            if (assessmentId) {
                this.setState({
                    selected: {
                        id: assessmentId,
                        typeName: assessmentTypeName
                    },
                    shouldOpenViewer: true
                })
            } else this.setState({
                isEditorOpen: state.shouldCreate
            })

            this.props
                .history
                .replace('assessments', {})
        }
    }

    componentDidUpdate () {
        const {
            shouldReload
        } = this.props

        const {
            selected,
            shouldOpenViewer
        } = this.state

        if (shouldReload) this.refresh()

        if (shouldOpenViewer) {
            const { typeName } = selected
            const type = this.getTypeByName(typeName)

            if (type) {
                this.setState({ shouldOpenViewer: false })
                this.onView({ ...selected, typeId: type.id })
            }
        }
    }

    componentWillUnmount() {
        this.actions.clear()
    }

    onResetError = () => {
        this.actions.clearError()
    }

    onRefresh = (page) => {
        this.refresh(page)
    }

    onSort = (field, order) => {
        this.sort(field, order)
    }

    onChangeFilterField = (name, value) => {
        this.changeFilter({ [name]: value })
    }

    onAdd = () => {
        this.setState({ isEditorOpen: true })
    }

    onDownloadInTuneReport = () => {
        this.downloadInTuneReport()
    }

    onCopy = (assessment) => {
        this.setState({
            isCopying: true,
            isEditorOpen: true,
            selected: assessment
        })
    }

    onEdit = (assessment) => {
        this.setState({
            isEditorOpen: true,
            selected: assessment
        })
    }

    onView = (assessment) => {
        this.setState({
            selected: assessment,
            isViewerOpen: true,
        })
    }

    onDownload = ({ id, typeName }) => {
        if (![COMPREHENSIVE, NOR_CAL_COMPREHENSIVE].includes(typeName)) {
            this.downloadPdf(id, typeName)
        }
    }

    onViewArchived = (assessment) => {
        this.setState({
            selectedArchived: assessment,
            isArchiveViewerOpen: true,
        })
    }

    onCloseEditor = (shouldConfirm = false) => {
        this.setState({
            isEditorOpen: shouldConfirm,
            isCompleteSuccessDialogOpen: false,
            isEditCancelConfirmDialogOpen: shouldConfirm
        })

        if (!shouldConfirm) {
            this.setState({
                selected: null,
                isCopying: false
            })
        }
    }

    onCloseEditCancelConfirmDialog = () => {
        this.setState({
            isEditCancelConfirmDialogOpen: false
        })
    }

    onCloseViewer = () => {
        this.setState({
            selected: null,
            isViewerOpen: false
        })
    }

    onCloseArchiveViewer = () => {
        this.setState({
            selectedArchived: null,
            isArchiveViewerOpen: false
        })
    }

    onSaveSuccess = (o, shouldClose) => {
        this.refresh()

        if (shouldClose) {
            this.setState({
                selected: o,
                isCopying: false,
                isEditorOpen: false,
                isSaveSuccessDialogOpen: true
            })

        } else this.setState({ selected: o })

        this.props.cache.invalidateQueries('Assessment', {
            clientId: this.clientId,
            size: 10
        })
    }

    onCompleteSuccess = o => {
        this.refresh()

        this.setState({
            selected: o,
            isCopying: false,
            isEditorOpen: false,
            isCompleteSuccessDialogOpen: true
        })
    }

    onChangeActivitySuccess = (data, isInactive) => {
        this.refresh().then(() => {
            this.setState({
                selected: { ...data, isInactive },
                isChangeActivitySuccessDialogOpen: true
            })
        })

        this.setState({
            isCopying: false,
            isEditorOpen: false
        })
    }

    onOpenVisibilityEditor = o => {
        this.setState({
            selected: o,
            isVisibilityEditorOpen: true
        })
    }

    onChangeVisibilitySuccess = () => {
        this.refresh()
        this.onCloseVisibilityEditor()
        this.setState({ isChangeVisibilitySuccessDialogOpen: true })
    }

    onCloseVisibilityEditor = isCancel => {
        this.setState({
            isVisibilityEditorOpen: false,
            ...isCancel && { selected: null }
        })
    }

    onCompleteSave = () => {
        this.setState({
            selected: null,
            isCopying: false,
            isSaveSuccessDialogOpen: false,
            isCompleteSuccessDialogOpen: false,
            isChangeActivitySuccessDialogOpen: false
        })
    }

    onBackToEditor = ({ shouldAddNeedsToServicePlan = false } = {}) => {
        this.setState(s => ({
            isEditorOpen: true,
            isSaveSuccessDialogOpen: false,
            isCompleteSuccessDialogOpen: false,
            selected: { ...s.selected, shouldAddNeedsToServicePlan }
        }))
    }

    get actions () {
        return this.props.actions
    }

    get authUser() {
        return this.props.auth.login.user.data
    }

    get clientId () {
        return +this.props.match.params.clientId
    }

    get error () {
        return this.props.error
    }

    update (isReload, page) {
        const {
            isFetching,
            shouldReload,
            dataSource: ds
        } = this.props

        if (isReload
            || shouldReload
            || (!isFetching && isEmpty(ds.data))) {
            const { field, order } = ds.sorting
            const { page: p, size } = ds.pagination

            return this.actions.load({
                size,
                page: page || p,
                ...ds.filter.toJS(),
                clientId: this.clientId,
                ...field && {sort: `${field},${order}`}
            })
        }
    }

    sort (field, order) {
        this.actions.sort(field, order)
    }

    refresh (page) {
        return this.update(true, page || FIRST_PAGE)
    }

    getTypeByName(name) {
        return this.props.assessmentUtils.getTypeByName(name)
    }

    downloadPdf(id, typeName) {
        return this.props.assessmentUtils.downloadPdf(id, typeName)
    }

    downloadJson(id) {
        return this.props.assessmentUtils.downloadJson(id)
    }

    downloadInTuneReport() {
        return this.actions.report.inTune.details.download(
            { clientId: this.clientId }
        )
    }

    clear () {
        this.actions.clear()
    }

    changeFilter (changes, shouldReload) {
        this.actions.changeFilter(
            changes, shouldReload
        )
    }

    canViewClient() {
        return Boolean(this.authUser && !(
            SYS_ROLES_WITH_NOT_VIEWABLE_CLIENT.includes(
                this.authUser.roleName
            )
        ))
    }

    render () {
        const {
            client,
            canAdd,
            isFetching,
            fetchCount,
            dataSource: ds,
            canGenerateInTuneReport,
            canDownloadInTuneReport
        } = this.props

        const {
            selected,
            selectedArchived,

            isCopying,
            isEditorOpen,
            isViewerOpen,
            isArchiveViewerOpen,
            isVisibilityEditorOpen,

            isSaveSuccessDialogOpen,
            isChangeActivitySuccessDialogOpen,
            isChangeVisibilitySuccessDialogOpen,

            isEditCancelConfirmDialogOpen,
            isCompleteSuccessDialogOpen
        } = this.state;

        const {
            fullName: clientFullName
        } = client.details.data || {}

        const clientId = this.clientId

        const preselectedType = (
            this.authUser?.roleName === HOME_CARE_ASSISTANT
            && this.getTypeByName(CARE_MGMT)
        )

        const canAddComprehensive = this.getTypeByName(COMPREHENSIVE)?.canAdd

        return (
            <DocumentTitle
                title={`Simply Connect | Clients | ${clientFullName} | Assessments`}>
                <div className="Assessments">
                    <LoadClientDetailsAction params={{ clientId }}/>
                    <LoadAssessmentTypesAction
                        params={{
                            clientId,
                            types: [
                                GAD7,
                                PHQ9,
                                IN_HOME,
                                CARE_MGMT,
                                ARIZONA_SSM,
                                IN_HOME_CARE,
                                COMPREHENSIVE,
                                NOR_CAL_COMPREHENSIVE,
                                BENEFICIARY_SCREENING_DIABETIC_FOOT_AND_EYE_EXAM,
                                BENEFICIARY_COLORECTAL_CANCER_SCREENING,
                                BENEFICIARY_MAMMOGRAM_SCREENING,
                                BENEFICIARY_SCREENING_FOR_TYPE_II_DIABETES
                            ]
                        }}
                    />
                    <LoadCanGenerateInTuneReportAction
                        isMultiple
                        params={{
                            clientId,
                            assessmentFetchCount: fetchCount
                        }}
                        shouldPerform={prevParams => (
                            fetchCount !== prevParams.assessmentFetchCount
                        )}
                    />
                    <LoadCanDownloadInTuneReportAction
                        isMultiple
                        params={{
                            clientId,
                            assessmentFetchCount: fetchCount
                        }}
                        shouldPerform={prevParams => (
                            fetchCount !== prevParams.assessmentFetchCount
                        )}
                    />
                    <UpdateSideBarAction
                        params={{ clientId, shouldRefresh: isFetching && fetchCount > 1 }}
                    />
                    <Breadcrumbs items={compact([
                        { title: 'Clients', href: '/clients', isEnabled: true },
                        client.details.data && {
                            title: `${clientFullName}`,
                            href: `/clients/${clientId || 1}`,
                            isActive: !this.canViewClient()
                        },
                        client.details.data && {
                            title: 'Assessments',
                            href: `/clients/${clientId || 1}/assessments`,
                            isActive: true
                        }
                    ])}/>
                    <Table
                        hasHover
                        hasOptions
                        hasPagination
                        keyField='id'
                        title='Assessments'
                        isLoading={isFetching}
                        className='AssessmentList'
                        containerClass='AssessmentListContainer'
                        data={ds.data}
                        pagination={ds.pagination}
                        columns={[
                            {
                                dataField: 'typeTitle',
                                text: 'Assessment',
                                sort: true,
                                headerClasses: 'ClientList-Header-AssessmentName',
                                onSort: this.onSort,
                                formatter: (v, row, index, formatExtraData, isMobile) => {
                                    return (
                                        <>
                                            <span
                                                id={`${isMobile ? 'm-' : ''}assessment-${row.id}`}
                                                className='AssessmentList-AssessmentName'
                                                onClick={() => this.onView(row)}>
                                                {v}
                                            </span>
                                            <Tooltip
                                                className='AssessmentList-Tooltip'
                                                placement="top"
                                                target={`${isMobile ? 'm-' : ''}assessment-${row.id}`}
                                                modifiers={[
                                                    {
                                                        name: 'offset',
                                                        options: { offset: [0, 6] }
                                                    },
                                                    {
                                                        name: 'preventOverflow',
                                                        options: { boundary: document.body }
                                                    }
                                                ]}
                                            >
                                                View Assessment
                                            </Tooltip>
                                        </>
                                    )
                                }
                            },
                            {
                                dataField: 'status',
                                text: 'Status',
                                sort: true,
                                align: 'left',
                                headerAlign: 'left',
                                onSort: this.onSort,
                                formatter: (v, row) => {
                                    return (
                                        <span
                                            style={{ backgroundColor: STATUS_COLORS[row.status.name] }}
                                            className='AssessmentList-AssessmentStatus'>
                                       {row.status.title}
                                    </span>
                                    )
                                }
                            },
                            {
                                dataField: 'dateStarted',
                                text: 'Date Started',
                                sort: true,
                                align: 'right',
                                headerAlign: 'right',
                                headerClasses: 'AssessmentList-DateStartedCol',
                                onSort: this.onSort,
                                formatter: v => v && format(v, DATE_FORMAT)
                            },
                            {
                                dataField: 'dateCompleted',
                                text: 'Date Completed',
                                sort: true,
                                align: 'right',
                                headerAlign: 'right',
                                headerClasses: 'AssessmentList-DateCompletedCol',
                                onSort: this.onSort,
                                formatter: v => v && format(v, DATE_FORMAT)
                            },
                            {
                                dataField: 'author',
                                text: 'Author',
                                sort: true,
                                align: 'left',
                                onSort: this.onSort,
                            },
                            {
                                dataField: 'score',
                                text: 'Score',
                                align: 'left',
                            },
                            {
                                dataField: '@actions',
                                text: '',
                                align: 'right',
                                headerClasses: 'AssessmentList-ActionCol',
                                formatter: (v, row) => {
                                    return (
                                        <div className="position-relative AssessmentList-Actions">
                                            {row.typeName?.includes(COMPREHENSIVE) && row.status.name !== HIDDEN && row.canHide && (
                                                <HideButton
                                                    name={`assessment-${row.id}_hide-btn`}
                                                    tipText="Hide the assessment"
                                                    onClick={() => this.onOpenVisibilityEditor(row)}
                                                    className="AssessmentList-ActionBtn"
                                                />
                                            )}
                                            {row.typeName?.includes(COMPREHENSIVE) && row.status.name === HIDDEN && row.canRestore && (
                                                <RefreshButton
                                                    name={`assessment-${row.id}_restore-btn`}
                                                    tipText="Restore the assessment"
                                                    onClick={() => this.onOpenVisibilityEditor(row)}
                                                    className="AssessmentList-ActionBtn"
                                                />
                                            )}
                                            {canAdd && canAddComprehensive && row.typeName?.includes(COMPREHENSIVE) && (
                                                <AddButton
                                                    name={`add-copy-${row.id}`}
                                                    tipText="Create a copy of assessment"
                                                    onClick={() => this.onCopy(row)}
                                                    className="AssessmentList-ActionBtn"
                                                />
                                            )}
                                            {[GAD7, PHQ9, IN_HOME, CARE_MGMT, ARIZONA_SSM, IN_HOME_CARE, COMPREHENSIVE, NOR_CAL_COMPREHENSIVE].includes(row.typeName) && (
                                                <>
                                                    <DownloadButton
                                                        name={`download-${row.id}`}
                                                        {...!row.typeName?.includes(COMPREHENSIVE) && {
                                                            tipText: 'Download Pdf File'
                                                        }}
                                                        onClick={() => this.onDownload(row)}
                                                        className="AssessmentList-ActionBtn"
                                                    />
                                                    {row.typeName?.includes(COMPREHENSIVE) && (
                                                        <Tooltip
                                                            trigger="focus"
                                                            placement="bottom"
                                                            target={`download-${row.id}`}
                                                            innerClassName="DownloadOptionPicker"
                                                            modifiers={[
                                                                {
                                                                    name: 'offset',
                                                                    options: { offset: [0, 6] }
                                                                },
                                                                {
                                                                    name: 'preventOverflow',
                                                                    options: { boundary: document.body }
                                                                }
                                                            ]}
                                                        >
                                                            <div
                                                                onClick={() => {
                                                                    this.downloadPdf(row.id, row.typeName)
                                                                }}
                                                                className="DownloadOptionPicker-Item"
                                                            >
                                                                Download Pdf File
                                                            </div>
                                                            <div
                                                                onClick={() => {
                                                                    this.downloadJson(row.id)
                                                                }}
                                                                className="DownloadOptionPicker-Item"
                                                            >
                                                                Download JSON file
                                                            </div>
                                                        </Tooltip>
                                                    )}
                                                </>
                                            )}
                                            {row.canEdit && (
                                                <EditButton
                                                    name={`edit-${row.id}`}
                                                    tipText="Edit assessment"
                                                    onClick={() => this.onEdit(row)}
                                                    className="AssessmentList-ActionBtn"
                                                />
                                            )}
                                        </div>
                                    )
                                }
                            }
                        ]}
                        columnsMobile={['typeTitle', 'author']}
                        noDataText="No assessments"
                        renderCaption={(title, isMobile) => {
                            return (
                                <div className='AssessmentList-Caption'>
                                    <div className="Assessments-Header">
                                        <div className="Assessments-Title">
                                                <span className='Assessments-TitleText'>
                                                    {title}
                                                </span>
                                                <span className="text-nowrap line-height-2">
                                                    <span className="Assessments-ClientName">
                                                        {client.details.data && (' / ' + client.details.data.fullName)}
                                                    </span>
                                                    {ds.pagination.totalCount ? (
                                                        <Badge color='info' className='Badge Badge_place_top-right'>
                                                            {ds.pagination.totalCount}
                                                        </Badge>
                                                    ) : null}
                                                </span>
                                            </div>
                                        <div className="Assessments-Actions">
                                            {(canDownloadInTuneReport.value || !!canDownloadInTuneReport.error) && (
                                                <Button
                                                    color='success'
                                                    id={`run-in-tune-report${isMobile ? '-mobile' : ''}`}
                                                    hasTip={!canGenerateInTuneReport?.value}
                                                    disabled={!canGenerateInTuneReport?.value}
                                                    tipText={canGenerateInTuneReport?.reasonText}
                                                    className='Assessments-Action InTuneReportBtn'
                                                    title="Run InTune Report"
                                                    onClick={this.onDownloadInTuneReport}
                                                >
                                                    <span className="AddAssessmentBtn-OptText">Run InTune&nbsp;</span>Report
                                                </Button>
                                            )}
                                            {canAdd && (
                                                <Button
                                                    color='success'
                                                    className='Assessments-Action AddAssessmentBtn'
                                                    title="Add New Assessment"
                                                    onClick={this.onAdd}
                                                >
                                                    Add New<span className="AddAssessmentBtn-OptText">&nbsp;Assessment</span>
                                                </Button>
                                            )}
                                        </div>
                                    </div>
                                    <div className='Assessments-Filter'>
                                        <Row>
                                            <Col md={6} lg={4}>
                                                <SearchField
                                                    name='name'
                                                    value={ds.filter.name}
                                                    placeholder='Search'
                                                    onChange={this.onChangeFilterField}
                                                    onClear={this.onChangeFilterField}
                                                />
                                            </Col>
                                        </Row>
                                    </div>
                                </div>
                            )
                        }}
                        onRefresh={this.onRefresh}
                    />
                    {isViewerOpen && (
                        <AssessmentViewer
                            isOpen
                            assessmentId={selected?.id}
                            assessmentTypeId={selected?.typeId}
                            onView={this.onViewArchived}
                            onClose={this.onCloseViewer}
                        />
                    )}
                    {isArchiveViewerOpen && (
                        <AssessmentViewer
                            isOpen
                            isAssessmentArchived
                            assessmentId={selectedArchived?.id}
                            assessmentTypeId={selectedArchived?.typeId}
                            onClose={this.onCloseArchiveViewer}
                        />
                    )}
                    {isEditorOpen && (
                        <AssessmentEditor
                            isOpen

                            clientId={clientId}
                            isCopying={isCopying}
                            assessmentId={selected?.id}
                            assessmentTypeId={preselectedType?.id}
                            shouldAddNeedsToServicePlan={selected?.shouldAddNeedsToServicePlan}

                            onClose={this.onCloseEditor}
                            onSaveSuccess={this.onSaveSuccess}
                            onCompleteSuccess={this.onCompleteSuccess}
                            onChangeActivitySuccess={this.onChangeActivitySuccess}
                        />
                    )}
                    <AssessmentVisibilityEditor
                        isOpen={isVisibilityEditorOpen}
                        clientId={clientId}
                        assessmentId={selected?.id}
                        assessmentStatus={selected?.status?.name}
                        onClose={this.onCloseVisibilityEditor}
                        onSaveSuccess={this.onChangeVisibilitySuccess}
                    />
                    {isEditCancelConfirmDialogOpen && (
                        <ConfirmDialog
                            isOpen
                            icon={Warning}
                            confirmBtnText='OK'
                            title='The updates will not be saved'
                            onConfirm={this.onCloseEditor}
                            onCancel={this.onCloseEditCancelConfirmDialog}
                        />
                    )}
                    {isCompleteSuccessDialogOpen && (
                        <SuccessDialog
                            isOpen
                            title={'The assessment has been completed.' + (
                                [ARIZONA_SSM].includes(selected?.typeName) ? ` Score is ${selected?.score}` : ''
                            )}
                            buttons={[
                                {
                                    outline: true,
                                    text: 'Close',
                                    className: 'min-width-170',
                                    onClick: this.onCompleteSave
                                },
                                {
                                    text: 'Back to assessment',
                                    className: 'min-width-170',
                                    onClick: this.onBackToEditor
                                }
                            ]}
                        />
                    )}
                    {isSaveSuccessDialogOpen && selected?.typeName === COMPREHENSIVE && (
                        <SuccessDialog
                            isOpen
                            title="The updates have been saved"
                            buttons={[
                                {
                                    outline: true,
                                    text: 'Close',
                                    className: 'min-width-170',
                                    onClick: this.onCompleteSave
                                },
                                {
                                    text: 'Back to assessment',
                                    className: 'min-width-170',
                                    onClick: this.onBackToEditor
                                }
                            ]}
                        />
                    )}
                    {isSaveSuccessDialogOpen && [IN_HOME, IN_HOME_CARE].includes(selected?.typeName) && (
                        <SuccessDialog
                            isOpen
                            title={
                                `The assessment has been completed. ${
                                    selected.notAddedToServicePlanNeedCount ? (
                                        'Do you want to create/update a service plan?'
                                    ) : ''}`
                            }
                            buttons={selected.notAddedToServicePlanNeedCount ? (
                                [
                                    {
                                        outline: true,
                                        text: 'No',
                                        className: 'min-width-170',
                                        onClick: this.onCompleteSave
                                    },
                                    {
                                        text: 'Yes',
                                        className: 'min-width-170',
                                        onClick: () => {
                                            this.onBackToEditor({ shouldAddNeedsToServicePlan: true })
                                        }
                                    }
                                ]
                            ) : ([
                                {
                                    text: 'OK',
                                    onClick: this.onCompleteSave
                                }
                            ])}
                        />
                    )}
                    {isSaveSuccessDialogOpen && ![IN_HOME, IN_HOME_CARE, ARIZONA_SSM, COMPREHENSIVE].includes(selected?.typeName) && (
                        <SuccessDialog
                            isOpen
                            title="The assessment has been completed."
                            buttons={[
                                {
                                    text: 'OK',
                                    onClick: this.onCompleteSave
                                }
                            ]}
                        />
                    )}
                    {isSaveSuccessDialogOpen && [ARIZONA_SSM].includes(selected?.typeName) && (
                        <SuccessDialog
                            isOpen
                            title="The assessment has been saved."
                            buttons={[
                                {
                                    outline: true,
                                    text: 'Close',
                                    className: 'min-width-170',
                                    onClick: this.onCompleteSave
                                },
                                {
                                    text: 'Back to assessment',
                                    className: 'min-width-170',
                                    onClick: this.onBackToEditor
                                }
                            ]}
                        />
                    )}
                    {isChangeActivitySuccessDialogOpen && selected && (
                        <SuccessDialog
                            isOpen
                            title={selected.isInactive ?
                                'The assessment has been marked as inactive'
                                : 'The assessment is in process'}
                            buttons={[
                                {
                                    text: 'Ok',
                                    onClick: this.onCompleteSave
                                }
                            ]}
                        />
                    )}
                    {isChangeVisibilitySuccessDialogOpen && (
                        <SuccessDialog
                            isOpen
                            title={`The assessment has been ${selected?.status?.name === HIDDEN ? 'restored' : 'hidden'}.`}
                            buttons={[
                                {
                                    text: 'Close',
                                    onClick: () => this.setState({
                                        selected: null,
                                        isChangeVisibilitySuccessDialogOpen: false
                                    })
                                }
                            ]}
                        />
                    )}
                    {this.error && !isIgnoredError(this.error) && (
                        <ErrorViewer
                            isOpen
                            error={this.error}
                            onClose={this.onResetError}
                        />
                    )}
                </div>
            </DocumentTitle>
        )
    }
}

export default compose(
    withRouter,
    withAssessmentUtils,
    connect(mapStateToProps, mapDispatchToProps),
    withQueryCache,
)(Assessments)
