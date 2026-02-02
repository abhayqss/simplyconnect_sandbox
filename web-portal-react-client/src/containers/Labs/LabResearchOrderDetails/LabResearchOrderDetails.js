import React, {
    useMemo,
    useState,
    useEffect,
    useCallback
} from 'react'

import { get } from 'lodash'
import { map, isArray } from 'underscore'

import { connect } from 'react-redux'
import { bindActionCreators } from 'redux'

import { Link, useParams, useHistory } from 'react-router-dom'

import { Row, Col, Button } from 'reactstrap'

import DocumentTitle from 'react-document-title'

import {
    Loader,
    Footer,
    ScrollTop,
    Breadcrumbs,
    ErrorViewer
} from 'components'

import { ConfirmDialog } from 'components/dialogs'

import {
    Detail
} from 'components/business/common'

import { useQueryParams } from 'hooks/common'

import DocumentDetail from 'containers/Clients/Clients/Documents/DocumentDetail/DocumentDetail'

import actions from 'redux/lab/research/order/details/labOrderDetailsActions'
import reviewOrderActions from 'redux/lab/research/order/review/labResearchOrderReviewActions'

import {
    SERVER_ERROR_CODES,
    ALLOWED_FILE_FORMATS,
    LAB_RESEARCH_ORDER_STATUSES,
    LAB_RESEARCH_ORDER_STATUS_COLORS
} from 'lib/Constants'

import {
    isEmpty,
    isNotEmpty,
    getAddress,
    DateUtils as DU,
    getFileFormatByMimeType
} from 'lib/utils/Utils'

import { path } from 'lib/utils/ContextUtils'
import { isNotEmptyOrBlank } from 'lib/utils/ObjectUtils'

import { ReactComponent as Warning } from 'images/alert-yellow.svg'

import LabResearchTestResults from '../LabResearchTestResults/LabResearchTestResults'

import './LabResearchOrderDetails.scss'

const { REVIEWED } = LAB_RESEARCH_ORDER_STATUSES

const { format, formats } = DU

const { PDF } = ALLOWED_FILE_FORMATS

const SUBSECTIONS = {
    OBSERVATION: ['source', 'dates', 'performerName', 'performerAddress', 'medicalDirector'],
    NOTES_AND_COMMENTS: ['commentSource', 'comments'],
    SPECIMEN: ['specimenType.text', 'specimenDate', 'specimenReceivedDate']
}

const DATE_FORMAT = formats.americanMediumDate
const DATE_TIME_FORMAT = formats.longDateMediumTime12

function isSectionShown(section, paths = []) {
    return paths.some(path => {
        let details = get(section, path)

        return isArray(details) ? details.length > 0 : !!details
    })
}

function isIgnoredError(e = {}) {
    return e.code === SERVER_ERROR_CODES.ACCOUNT_INACTIVE
}

function mapStateToProps(state) {
    return {
        state: state.lab.research.order.details
    }
}

function mapDispatchToProps(dispatch) {
    return {
        actions: {
            ...bindActionCreators(actions, dispatch),
            review: bindActionCreators(reviewOrderActions, dispatch)
        }
    }
}

function LabResearchOrderDetails({ id, state, actions }) {
    const { data, error } = state

    const { clear, load } = actions

    const params = useParams()
    const history = useHistory()
    const queryParams = useQueryParams()

    const orderId = id || parseInt(
        params.orderId
    ) || undefined

    const [isFetching, setIsFetching] = useState(true)
    const [isReviewed, setIsReviewed] = useState(false)
    const [isReviewConfirmOpen, setIsReviewConfirmOpen] = useState(false)

    function fetch() {
        setIsFetching(true)
        load(orderId).finally(() => setIsFetching(false))
    }

    function setIsReviewedIfNeed() {
        if (queryParams?.isReviewed) {
            history.replace()
            setIsReviewed(true)
        }
    }

    const review = useCallback(() => {
        actions.review.load([ orderId ])
    }, [ orderId, actions ])

    const onFetch = useCallback(fetch, [load])

    const onCancelReview = useCallback(() => {
        setIsReviewConfirmOpen(false)
    }, [])

    const onConfirmReview = useCallback(() => {
        review()
        setIsReviewed(true)
        setIsReviewConfirmOpen(false)
    }, [ review ])

    let content

    if (isFetching) {
        content = <Loader isCentered/>
    }

    else if (isEmpty(data)) {
        content = <h4>No Data</h4>
    }

    else {
        content = (
            <>
                <div className="LabResearchOrderDetails-Header">
                    <div className="LabResearchOrderDetails-Title">
                        Order
                    </div>
                </div>
                <div className="LabResearchOrderDetails-Body">
                    <div className="LabResearchOrderDetails-Section">
                        <div className="LabResearchOrderDetails-SectionTitle">
                            Order Information
                        </div>
                        <>
                            <Detail
                                title="Requisition or accession number"
                                titleClassName="LabResearchOrderDetail-Title padding-right-15"
                                valueClassName="LabResearchOrderDetail-Value"
                                className="LabResearchOrderDetail"
                            >
                                {data.requisitionNumber}
                            </Detail>
                            {!data.result && (
                                <Detail
                                    title="Status"
                                    titleClassName="LabResearchOrderDetail-Title"
                                    valueClassName="LabResearchOrderDetail-Value"
                                    className="LabResearchOrderDetail"
                                >
                                    <div
                                        className="LabResearchOrderDetail-Status"
                                        style={{ backgroundColor: LAB_RESEARCH_ORDER_STATUS_COLORS[data.statusName] }}
                                    >
                                        {data.statusTitle}
                                    </div>
                                </Detail>
                            )}
                            <Detail
                                title="Reason For Testing"
                                titleClassName="LabResearchOrderDetail-Title"
                                valueClassName="LabResearchOrderDetail-Value"
                                className="LabResearchOrderDetail"
                            >
                                {data.reason}
                            </Detail>
                            <Detail
                                title="Clinic"
                                titleClassName="LabResearchOrderDetail-Title"
                                valueClassName="LabResearchOrderDetail-Value"
                                className="LabResearchOrderDetail"
                            >
                                {data.clinic}
                            </Detail>
                            <Detail
                                title="Clinic Address"
                                titleClassName="LabResearchOrderDetail-Title"
                                valueClassName="LabResearchOrderDetail-Value"
                                className="LabResearchOrderDetail"
                            >
                                {data.clinicAddress}
                            </Detail>
                            <Detail
                                title="Order Date"
                                titleClassName="LabResearchOrderDetail-Title"
                                valueClassName="LabResearchOrderDetail-Value"
                                className="LabResearchOrderDetail"
                            >
                                {format(data.createdDate, DATE_TIME_FORMAT)}
                            </Detail>
                            <Detail
                                title="Created By"
                                titleClassName="LabResearchOrderDetail-Title"
                                valueClassName="LabResearchOrderDetail-Value"
                                className="LabResearchOrderDetail"
                            >
                                {data.createdByName}
                            </Detail>
                        </>
                    </div>
                    {isNotEmptyOrBlank(data.client) && (
                        <div className="LabResearchOrderDetails-Section">
                            <div className="LabResearchOrderDetails-SectionTitle">
                                Client information
                            </div>
                            <>
                                <Detail
                                    title="Name"
                                    titleClassName="LabResearchOrderDetail-Title"
                                    valueClassName="LabResearchOrderDetail-Value"
                                    className="LabResearchOrderDetail"
                                >
                                    {data.client.canView ? (
                                        <Link
                                            className='LabResearchOrderDetails-Client'
                                            to={path(`/clients/${data.client.id}`)}
                                        >
                                            {data.client.fullName}
                                        </Link>
                                    ) : data.client.fullName}
                                </Detail>
                                <Detail
                                    title="Sex"
                                    titleClassName="LabResearchOrderDetail-Title"
                                    valueClassName="LabResearchOrderDetail-Value"
                                    className="LabResearchOrderDetail"
                                >
                                    {data.client.genderTitle}
                                </Detail>
                                <Detail
                                    title="Race"
                                    titleClassName="ReferralDetail-Title"
                                    valueClassName="ReferralDetail-Value"
                                    className="ReferralDetail"
                                >
                                    {data.client.raceTitle}
                                </Detail>
                                <Detail
                                    title="Date Of Birth"
                                    titleClassName="LabResearchOrderDetail-Title"
                                    valueClassName="LabResearchOrderDetail-Value"
                                    className="LabResearchOrderDetail"
                                >
                                    {data.client.birthDate}
                                </Detail>
                                <Detail
                                    title="SSN"
                                    titleClassName="LabResearchOrderDetail-Title"
                                    valueClassName="LabResearchOrderDetail-Value"
                                    className="LabResearchOrderDetail"
                                >
                                    {data.client.ssn && ('###-##-' + data.client.ssn.substr(-4, 4))}
                                </Detail>
                                <Detail
                                    title="Phone #"
                                    titleClassName="LabResearchOrderDetail-Title"
                                    valueClassName="LabResearchOrderDetail-Value"
                                    className="LabResearchOrderDetail"
                                >
                                    {data.client.phone}
                                </Detail>
                                <Detail
                                    title="Address"
                                    titleClassName="LabResearchOrderDetail-Title"
                                    valueClassName="LabResearchOrderDetail-Value"
                                    className="LabResearchOrderDetail"
                                >
                                    {getAddress(data.client.address, ',')}
                                </Detail>
                                <Detail
                                    title="Primary Insurance"
                                    titleClassName="LabResearchOrderDetail-Title"
                                    valueClassName="LabResearchOrderDetail-Value"
                                    className="LabResearchOrderDetail"
                                >
                                    {data.client.insuranceNetwork}
                                </Detail>
                                <Detail
                                    title="Policy #"
                                    titleClassName="LabResearchOrderDetail-Title"
                                    valueClassName="LabResearchOrderDetail-Value"
                                    className="LabResearchOrderDetail"
                                >
                                    {data.client.policyNumber}
                                </Detail>
                                <Detail
                                    title="Policy Holder"
                                    titleClassName="LabResearchOrderDetail-Title"
                                    valueClassName="LabResearchOrderDetail-Value"
                                    className="LabResearchOrderDetail"
                                >
                                    {data.client.policyHolderRelationTitle}
                                </Detail>
                                <Detail
                                    title="Policy Holder Name (if spouse or parent)"
                                    titleClassName="LabResearchOrderDetail-Title"
                                    valueClassName="LabResearchOrderDetail-Value"
                                    className="LabResearchOrderDetail"
                                >
                                    {data.client.policyHolderName}
                                </Detail>
                                <Detail
                                    title="Policy Holder DOB"
                                    titleClassName="LabResearchOrderDetail-Title"
                                    valueClassName="LabResearchOrderDetail-Value"
                                    className="LabResearchOrderDetail"
                                >
                                    {data.client.policyHolderDOB}
                                </Detail>
                            </>
                        </div>
                    )}
                    {isNotEmptyOrBlank(data.specimen) && (
                        <div className="LabResearchOrderDetails-Section">
                            <div className="LabResearchOrderDetails-SectionTitle">
                                Specimen information
                            </div>
                            <>
                                <Detail
                                    title="Specimen"
                                    titleClassName="LabResearchOrderDetail-Title"
                                    valueClassName="LabResearchOrderDetail-Value d-flex flex-column"
                                    className="LabResearchOrderDetail"
                                >
                                    {map(data.specimen.types, o => (
                                        <div key={o.id} className="line-height-2">
                                            {o.title}
                                        </div>
                                    ))}
                                </Detail>
                                <Detail
                                    title="Collector's Name"
                                    titleClassName="LabResearchOrderDetail-Title"
                                    valueClassName="LabResearchOrderDetail-Value"
                                    className="LabResearchOrderDetail"
                                >
                                    {data.specimen.collectorName}
                                </Detail>
                                <Detail
                                    title="Site"
                                    titleClassName="LabResearchOrderDetail-Title"
                                    valueClassName="LabResearchOrderDetail-Value"
                                    className="LabResearchOrderDetail"
                                >
                                    {data.specimen.site}
                                </Detail>
                                <Detail
                                    title="Date & Time"
                                    titleClassName="LabResearchOrderDetail-Title"
                                    valueClassName="LabResearchOrderDetail-Value"
                                    className="LabResearchOrderDetail"
                                >
                                    {format(data.specimen.date, DATE_TIME_FORMAT)}
                                </Detail>
                            </>
                        </div>
                    )}
                    <div className="LabResearchOrderDetails-Section">
                        <div className="LabResearchOrderDetails-SectionTitle">
                            Ordering Provider
                        </div>
                        <>
                            <Detail
                                title="Full Name"
                                titleClassName="LabResearchOrderDetail-Title"
                                valueClassName="LabResearchOrderDetail-Value"
                                className="LabResearchOrderDetail"
                            >
                                {data.providerFullName}
                            </Detail>
                            <Detail
                                title="Date"
                                titleClassName="LabResearchOrderDetail-Title"
                                valueClassName="LabResearchOrderDetail-Value"
                                className="LabResearchOrderDetail"
                            >
                                {format(data.orderDate, DATE_FORMAT)}
                            </Detail>
                        </>
                    </div>
                    <div className="LabResearchOrderDetails-Section">
                        <div className="LabResearchOrderDetails-SectionTitle">
                            Panels
                        </div>
                        <Detail
                            title="Urgent COVID-19"
                            titleClassName="LabResearchOrderDetail-Title"
                            valueClassName="LabResearchOrderDetail-Value"
                            className="LabResearchOrderDetail"
                        >
                            <div className="max-width-600">
                                URGENT COVID-19 (SARS-CoV-2, Coronavirus)
                                Negative results do not preclude SARS‐CoV‐2 infection and should not be used as the sole
                                basis for
                                patient management decisions. Negative results must be combined with clinical
                                observations, patient history,
                                and epidemiological information. The assay is intended for use under the Food and Drug
                                Administration’s
                                Emergency Use Authorization.
                            </div>
                        </Detail>
                    </div>
                    <div className="LabResearchOrderDetails-Section">
                        <div className="LabResearchOrderDetails-SectionTitle">
                            ICD Codes
                        </div>
                        <Detail
                            title="ICD 10 Code"
                            titleClassName="LabResearchOrderDetail-Title"
                            valueClassName="LabResearchOrderDetail-Value d-flex flex-column"
                            className="LabResearchOrderDetail"
                        >
                            {map(data.icd10Codes, o => (
                                <div key={o} className="line-height-2">{o}</div>
                            ))}
                        </Detail>
                        <Detail
                            title="COVID-19 (SARS-COV-2)"
                            titleClassName="LabResearchOrderDetail-Title"
                            valueClassName="LabResearchOrderDetail-Value d-flex flex-column"
                            className="LabResearchOrderDetail"
                        >
                            {map(data.covid19Codes, o => (
                                <div key={o} className="line-height-2">{o}</div>
                            ))}
                        </Detail>
                    </div>
                    {isNotEmpty(data.notes) && (
                        <div className="LabResearchOrderDetails-Section">
                            <div className="LabResearchOrderDetails-SectionTitle">
                                Additional notes
                            </div>
                            <>
                                <Detail
                                    title="Notes"
                                    titleClassName="LabResearchOrderDetail-Title"
                                    valueClassName="LabResearchOrderDetail-Value"
                                    className="LabResearchOrderDetail"
                                >
                                    {data.notes}
                                </Detail>
                            </>
                        </div>
                    )}
                    {isNotEmpty(data.result) && (
                        <>
                            <div className="LabResearchOrderDetails-Header d-flex flex-row">
                                <div className="LabResearchOrderDetails-Title flex-1">
                                    Results
                                </div>
                                {!(isReviewed || data.result.statusName === REVIEWED) && (
                                    <div className="flex-1 text-right">
                                        <Button
                                            color="success"
                                            onClick={() => setIsReviewConfirmOpen(true)}
                                        >
                                            Mark as Reviewed
                                        </Button>
                                    </div>
                                )}
                            </div>
                            <div className="LabResearchOrderDetails-Section">
                                <>
                                    <Detail
                                        title="Status"
                                        titleClassName="LabResearchOrderDetail-Title"
                                        valueClassName="LabResearchOrderDetail-Value"
                                        className="LabResearchOrderDetail"
                                    >
                                        <div
                                            className="LabResearchOrderDetail-Status"
                                            style={{ backgroundColor: LAB_RESEARCH_ORDER_STATUS_COLORS[isReviewed ? REVIEWED : data.result.statusName] }}
                                        >
                                            {isReviewed ? 'Reviewed' : data.result.statusTitle}
                                        </div>
                                    </Detail>
                                    {map(data.result.documents, ({ id, title, mimeType }) => (
                                        <DocumentDetail
                                            key={id}
                                            id={id}
                                            title={title}
                                            canView={false}
                                            clientId={data.client.id}
                                            downloadHint="Download document"
                                            format={getFileFormatByMimeType(mimeType) || PDF}
                                        />
                                    ))}
                                </>
                            </div>
                            {isSectionShown(data.result, SUBSECTIONS.OBSERVATION) && (
                                <div className="LabResearchResultDetails-Section">
                                    <div className="LabResearchResultDetails-SectionTitle">
                                        Observation/Result
                                    </div>
                                    <Detail
                                        title="Source"
                                        titleClassName="LabResearchResultDetail-Title"
                                        valueClassName="LabResearchResultDetail-Value"
                                        className="LabResearchResultDetail"
                                    >
                                        {data.result.source}
                                    </Detail>
                                    <Detail
                                        title="Date/Time"
                                        titleClassName="LabResearchResultDetail-Title"
                                        valueClassName="LabResearchResultDetail-Value d-flex flex-column"
                                        className="LabResearchResultDetail"
                                    >
                                        {map(data.result.dates, o => (
                                            <div key={o} className="line-height-2">
                                                {format(o, DATE_TIME_FORMAT)}
                                            </div>
                                        ))}
                                    </Detail>
                                    <Detail
                                        title="Performing Organization"
                                        titleClassName="LabResearchResultDetail-Title"
                                        valueClassName="LabResearchResultDetail-Value"
                                        className="LabResearchResultDetail"
                                    >
                                        {data.result.performerName}
                                    </Detail>
                                    <Detail
                                        title="Organization Address"
                                        titleClassName="LabResearchResultDetail-Title"
                                        valueClassName="LabResearchResultDetail-Value"
                                        className="LabResearchResultDetail"
                                    >
                                        {data.result.performerAddress}
                                    </Detail>
                                    <Detail
                                        title="Medical Director"
                                        titleClassName="LabResearchResultDetail-Title"
                                        valueClassName="LabResearchResultDetail-Value"
                                        className="LabResearchResultDetail"
                                    >
                                        {data.result.medicalDirector}
                                    </Detail>

                                    <LabResearchTestResults orderId={orderId}/>
                                </div>
                            )}
                            {isSectionShown(data.result, SUBSECTIONS.NOTES_AND_COMMENTS) && (
                                <div className="LabResearchResultDetails-Section margin-top-5">
                                    <div className="LabResearchResultDetails-SectionTitle">
                                        Notes and Comments
                                    </div>
                                    <Detail
                                        title="Source of comment"
                                        titleClassName="LabResearchResultDetail-Title"
                                        valueClassName="LabResearchResultDetail-Value"
                                        className="LabResearchResultDetail"
                                    >
                                        {data.result.commentSource}
                                    </Detail>
                                    <Detail
                                        title="Comment"
                                        titleClassName="LabResearchResultDetail-Title"
                                        valueClassName="LabResearchResultDetail-Value d-flex flex-column"
                                        className="LabResearchResultDetail-Comment"
                                    >
                                        {map(data.result.comments, o => (
                                            <div key={o} className="line-height-2">{o}</div>
                                        ))}
                                    </Detail>
                                </div>
                            )}
                            {isSectionShown(data.result, SUBSECTIONS.SPECIMEN) && (
                                <div className="LabResearchResultDetails-Section">
                                    <div className="LabResearchResultDetails-SectionTitle">
                                        Specimen
                                    </div>
                                    <Detail
                                        title="Specimen Type"
                                        titleClassName="LabResearchResultDetail-Title"
                                        valueClassName="LabResearchResultDetail-Value"
                                        className="LabResearchResultDetail"
                                    >
                                        {data.result?.specimenType?.text}
                                    </Detail>
                                    <Detail
                                        title="Date / Time"
                                        titleClassName="LabResearchResultDetail-Title"
                                        valueClassName="LabResearchResultDetail-Value"
                                        className="LabResearchResultDetail"
                                    >
                                        {format(data.result.specimenDate, DATE_TIME_FORMAT)}
                                    </Detail>
                                    <Detail
                                        title="Specimen Received Date / Time"
                                        titleClassName="LabResearchResultDetail-Title"
                                        valueClassName="LabResearchResultDetail-Value"
                                        className="LabResearchOrderDetail"
                                    >
                                        {format(data.result.specimenReceivedDate, DATE_TIME_FORMAT)}
                                    </Detail>
                                </div>
                            )}
                        </>
                    )}
                </div>
            </>
        )
    }

    useEffect(() => { onFetch() }, [onFetch])

    useEffect(setIsReviewedIfNeed, [history, queryParams])

    useEffect(() => clear, [clear])
    useEffect(() => actions.review.clear, [actions.review.clear])

    return (
        <DocumentTitle title="Simply Connect | Admin | Order">
            <>
                <div className="LabResearchOrderDetails">
                    <Breadcrumbs
                        items={[
                            { title: 'Labs', href: '/labs', isEnabled: true },
                            { title: 'Order Details', isActive: true },
                        ]}
                        className="margin-bottom-40"
                    />
                    {content}
                    {isReviewConfirmOpen && (
                        <ConfirmDialog
                            isOpen
                            icon={Warning}
                            confirmBtnText="OK"
                            title={'By clicking on the "Mark as Reviewed" button, you confirm that the results have been reviewed. The results will be visible to other users'}
                            onConfirm={onConfirmReview}
                            onCancel={onCancelReview}
                        />
                    )}
                    {error && !isIgnoredError(error) && (
                        <ErrorViewer
                            isOpen
                            error={error}
                            onClose={actions.clearError}
                        />
                    )}
                    <ScrollTop
                        scrollable=".App"
                        scrollTopBtnClass="LabResearchOrderDetails-ScrollTopBtn"
                    />
                </div>
                <Footer theme='gray'/>
            </>
        </DocumentTitle>
    )
}

export default connect(mapStateToProps, mapDispatchToProps)(LabResearchOrderDetails)