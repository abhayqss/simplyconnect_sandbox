import React, {
    memo,
    useMemo,
    useState,
    useEffect,
    useCallback,
} from 'react'

import {
    omit,
    chain,
    isNumber
} from 'underscore'

import cn from 'classnames'

import moment from 'moment'

import { connect } from 'react-redux'
import { compose, bindActionCreators } from 'redux'

import {
    Col,
    Row,
    Form,
    Button,
    UncontrolledTooltip as Tooltip
} from 'reactstrap'

import {
    TextField,
    DateField,
    PhoneField,
    SelectField,
    CheckboxField,
    RadioGroupField,
} from 'components/Form'

import LabResearchOrderReviewDetails from '../LabResearchOrderReviewDetails/LabResearchOrderReviewDetails'

import * as errorActions from 'redux/error/errorActions'
import defaultActions from 'redux/lab/research/order/default/labResearchOrderDefaultActions'
import labResearchOrderFormActions from 'redux/lab/research/order/form/labResearchOrderFormActions'

import {
    useForm,
    useResponse,
    useScrollable,
    useDirectoryData,
    useSelectOptions,
    useScrollToFormError
} from 'hooks/common'

import {
    useRacesQuery,
    useStatesQuery,
    useClientsQuery,
    useGendersQuery,
    useCommunityNameList,
    useLabResearchReasonQuery,
    usePolicyHolderRelationsQuery,
} from 'hooks/business/directory'

import {
    useIcdCodesQuery,
    useSpecimenTypesQuery,
    useCollectorSitesQuery,
} from 'hooks/business/labs'

import Client from 'entities/Client'
import LabResearchOrder from 'entities/LabResearchOrder'
import LabResearchOrderFormValidator from 'validators/LabResearchOrderFormValidator'

import { DateUtils as DU } from 'lib/utils/Utils'

import { LAB_RESEARCH_ORDER_STEPS as STEPS } from '../Constants'

import './LabOrderForm.scss'


const { format, formats } = DU

const DATE_FORMAT = formats.americanMediumDate

const formatSSN = value => value ? `###-##-${value}` : ''
const formatDate = value => value ? format(value, DATE_FORMAT) : null
const formatStringDate = value => value ? moment(value, 'MM/DD/YYYY').toDate().getTime() : null

const scrollableStyles = { flex: 1 }

const getData = fields => {
    let data = fields.toJS()

    let icd10Codes = Array.from(
        new Set([
            ...data.icd10Codes,
            ...Object.values(
                omit(data.customIcdCodes, 'secondIsDisabled', 'thirdIsDisabled') // - CCN-4398. Remove Set invocation when it's needed
            )
                .filter(s => !!s.trim())
                .map(s => s.trim().toUpperCase())
        ]) // - CCN-4398. Remove Set invocation when it's needed
    )

    return {
        ...data,
        specimen: {
            ...data.specimen,
            types: data.specimen.types.map(id => ({ id }))
        },
        icd10Codes
    }
}

const urgentMessage = (
    <>
        <div>URGENT COVID-19 (SARS-CoV-2, Coronavirus)</div>
        <div>
            Negative results do not preclude SARS‐CoV‐2 infection and should not be used as the sole basis for patient management decisions.
            Negative results must be combined with clinical observations, patient history, and epidemiological information. The assay is intended
            for use under the Food and Drug Administration’s Emergency Use Authorization.
        </div>
    </>
)

const SELF = 'SELF'
const ESWAB = 'ESWAB'
const COVID19 = 'COVID19'
const OTHER_SITE = 'OTHER'
const DefaultIcdCodes = ['Z03.818']

const groupCodes = codes => {
    let row = -1

    return chain(codes)
        .groupBy((_, index) => index % 3 === 0 ? ++row : row)
        .values()
        .value()
}

function mapStateToProps(state) {
    return {
        user: state.auth.login.user.data,
        defaultData: state.lab.research.order.default.data
    }
}

function mapDispatchToProps(dispatch) {
    return {
        actions: {
            ...bindActionCreators(labResearchOrderFormActions, dispatch),
            default: bindActionCreators(defaultActions, dispatch),
            error: bindActionCreators(errorActions, dispatch),
        }
    }
}

function LabOrderForm(
    {
        onClose,
        actions,
        clientId,
        communityId,
        defaultData,
        onChangedStep,
        organizationId,
        onSubmitSuccess,
    }
) {
    const isClient = Number.isInteger(clientId)

    const {
        races,
        states,
        genders,
        clients,
        reasons,
        icdCodes,
        specimenTypes,
        policyHolderRelations,
    } = useDirectoryData({
        races: ['race'],
        states: ['state'],
        genders: ['gender'],
        clients: ['client'],
        reasons: ['lab', 'research', 'reason'],
        icdCodes: ['lab', 'research', 'icdCode'],
        specimenTypes: ['lab', 'research', 'order', 'specimenType'],
        policyHolderRelations: ['lab', 'research', 'policyHolderRelation']
    })

    const {
        fields,
        errors,
        validate,
        isChanged,
        changeField: onChangeField,
        changeFields: onChangeFields,
        changeDateField: onChangeDateField
    } = useForm('LabOrder', LabResearchOrder, LabResearchOrderFormValidator)

    const {
        data: collectorSites = [],
        isFetching: isFetchingCollectorSites
    } = useCollectorSitesQuery(
        { communityId: communityId },
        { enabled: isNumber(communityId) }
    )

    const [needValidation, setNeedValidation] = useState(false)
    const [isFetching, setIsFetching] = useState(false)
    const [site, setSite] = useState(null)
    const [step, setStep] = useState(STEPS.FORM)

    const policyHolderRelationName = fields.client?.policyHolderRelationName

    const isSelfHolder = policyHolderRelationName === SELF

    const decorateWithAsterix = title => `${title}${isSelfHolder || !policyHolderRelationName ? '' : '*'}`

    const icd10Codes = useMemo(() => fields.icd10Codes.toArray(), [fields.icd10Codes])
    const specimenTypeList = useMemo(() => fields.specimen.types.toArray(), [fields.specimen.types])

    const covid19Codes = useMemo(() => {
        return icdCodes
            .find(o => o.name === COVID19)
            ?.codes.map(o => o.name) || []
    }, [icdCodes])

    const gendersOptions = useMemo(() => genders.map(o => ({
        value: o.id,
        label: o.label,
    })), [genders])

    const racesOptions = useSelectOptions(races)
    const statesOptions = useSelectOptions(states, { textProp: 'label' })
    const clientsOptions = useSelectOptions(clients, { textProp: 'fullName' })
    const reasonsOptions = useSelectOptions(reasons, { valueProp: 'name' })
    const communitiesOptions = useSelectOptions(collectorSites)
    const policyHolderRelationsOptions = useSelectOptions(policyHolderRelations, { valueProp: 'name' })

    const siteOptions = useMemo(() => [
        ...communitiesOptions,
        { text: 'Other', value: OTHER_SITE }
    ], [communitiesOptions])

    const validationOptions = useMemo(() => ({
        included: {
            excludedRelations: [SELF],
            selected: {
                icd10Codes: icd10Codes,
            }
        }
    }), [icd10Codes])

    const { Scrollable, scroll } = useScrollable()

    function cancel() {
        onClose(isChanged)
    }

    const onResponse = useResponse({
        onFailure: actions.error.change,
        onSuccess: useCallback(({ data }) => onSubmitSuccess(data), [onSubmitSuccess]),
        onUnknown: actions.error.change
    })

    const clearClientFields = useCallback(() => {
        onChangeField('client', Client())
    }, [onChangeField])

    function setDefaultClientData() {
        if (defaultData != null) {
            clearClientFields()
            onChangeFields(defaultData, isClient)
        }
    }

    function setDefaultFormData() {
        if (specimenTypes.length > 0) {
            onChangeFields({
                specimen: {
                    types: [
                        specimenTypes.find(type => type.name === ESWAB).id
                    ]
                }
            }, true)
        }
    }

    const loadDefaultData = useCallback(clientId => {
        Number.isInteger(clientId) && actions.default.load({ clientId })
    }, [actions.default])

    const onChangeClientField = useCallback((field, value) => {
        loadDefaultData(value)
        onChangeField(field, value)
    }, [loadDefaultData, onChangeField])

    const onChangeCustomIcdCode = useCallback((value, shouldRemove = false) => {
        let customFieldNames = ['second', 'third']
        let byEmptyString = name => fields.customIcdCodes.get(name) === ''
        let byValue = name => fields.customIcdCodes.get(name) === value

        let targetName = customFieldNames.find(shouldRemove ? byValue : byEmptyString)

        if (targetName) {
            onChangeFields({
                customIcdCodes: {
                    [targetName]: shouldRemove ? '' : value,
                    [`${targetName}IsDisabled`]: !shouldRemove,
                }
            })
        }
    }, [onChangeFields, fields.customIcdCodes])

    const onChangeCovidIcdCheckbox = useCallback(icdCode => {
        let icd10Codes = fields.icd10Codes
        let value = icd10Codes.includes(icdCode)
            ? icd10Codes.remove(icdCode)
            : icd10Codes.add(icdCode)

        if (icd10Codes.includes(icdCode)) {
            value = icd10Codes.remove(icdCode)
            onChangeCustomIcdCode(icdCode, true) // - CCN-4398. Remove it when it's needed
        } else {
            value = icd10Codes.add(icdCode)
            onChangeCustomIcdCode(icdCode, false) // - CCN-4398. Remove it when it's needed
        }

        onChangeField('icd10Codes', value)
    }, [fields.icd10Codes, onChangeField, onChangeCustomIcdCode])

    const onChangeSpecimenType = useCallback(typeId => {
        let types = fields.specimen.types
        let value = types.includes(typeId)
            ? types.remove(typeId)
            : types.add(typeId)

        onChangeField('specimen.types', value)
    }, [fields.specimen.types, onChangeField])

    const onChangeBirthDate = useCallback((field, value) => {
        onChangeField(field, formatDate(value))
    }, [onChangeField])

    const onChangePolicyHolderDOB = useCallback((field, value) => {
        onChangeField(field, formatDate(value))
    }, [onChangeField])

    const onChangePolicyHolderRelationName = useCallback((field, value) => {
        if (value === SELF) {
            onChangeFields({
                client: {
                    policyHolderDOB: '',
                    policyHolderName: ''
                }
            })
        }

        onChangeField(field, value)
    }, [onChangeField, onChangeFields])

    const onChangeSite = useCallback((field, id) => {
        setSite(id)
        onChangeField(field, id === OTHER_SITE ? '' : (
            communitiesOptions.find(o => o.value === id)?.text || ''
        ))
    }, [onChangeField, communitiesOptions])

    let { fetch: refetchClients } = useClientsQuery(useMemo(() => ({
        communityIds: [communityId],
        recordStatuses: ['ACTIVE']
    }), [communityId]))

    const onChangeClientData = useCallback(client => {
        onChangeFields({
            client: {
                ...client,
                fullName: `${client.firstName} ${client.lastName}`
            }
        })

        refetchClients()
        onChangedStep(STEPS.FORM)
    }, [onChangeFields, refetchClients, onChangedStep])

    const onScroll = useScrollToFormError('.LabOrderForm', scroll)
    const onScrollToTop = useCallback(() => scroll(0), [scroll])

    const doValidate = useCallback(() => {
        return validate(validationOptions).then(async () => {
            onScrollToTop()
            setNeedValidation(false)

            return true
        })
            .catch(() => {
                onScroll()
                setNeedValidation(true)

                return false
            })
    }, [onScroll, onScrollToTop, validate, validationOptions])

    function submit(e) {
        e.preventDefault()

        let doSubmit = async () => {
            let isValid = await doValidate()

            if (!isValid) return setIsFetching(false)

            if (step !== STEPS.REVIEW) {
                setStep(prevState => ++prevState)
            } else {
                setIsFetching(true)

                try {
                    onResponse(
                        await actions.submit(getData(fields))
                    )
                    setIsFetching(false)
                } catch (error) {
                    setIsFetching(false)
                }
            }
        }

        doSubmit()
    }

    function nextToReview() {
        doValidate()
            .then((isValid) => {
                if (isValid) {
                    onScrollToTop()
                    setStep(STEPS.REVIEW)
                    setNeedValidation(false)
                    onChangedStep(STEPS.REVIEW)
                } else {
                    onScroll()
                    setNeedValidation(true)
                }
            })
    }

    function validateIf() {
        if (needValidation) {
            validate(validationOptions)
                .then(() => setNeedValidation(false))
                .catch(() => setNeedValidation(true))
        }
    }

    const onSubmit = useCallback(submit, [actions.submit, onResponse, fields, doValidate, step])
    const onCancel = useCallback(cancel, [onClose, isChanged])
    const onNext = useCallback(nextToReview, [doValidate, onChangedStep, onScrollToTop])
    const onBack = useCallback(() => {
        setStep(prevState => {
            let newStep = prevState - 1

            onChangedStep(newStep)

            return newStep
        })
    }, [onChangedStep])

    useGendersQuery(useMemo(() => ({
        biologicalOnly: true
    }), []))

    useRacesQuery()
    useStatesQuery()
    useIcdCodesQuery()
    useSpecimenTypesQuery()
    useLabResearchReasonQuery()
    usePolicyHolderRelationsQuery()

    useEffect(validateIf, [needValidation, onScroll, validate, validationOptions])

    useEffect(function () {
        if (isClient) {
            loadDefaultData(clientId)
        }
    }, [actions.default, clientId, isClient, loadDefaultData])

    useEffect(setDefaultFormData, [specimenTypes, onChangeFields])

    useEffect(setDefaultClientData, [defaultData, isClient, onChangeFields, clearClientFields])

    useEffect(() => actions.default.clear, [actions.default])

    const Covid19CodeGroup = useMemo(() => () => (
        groupCodes(covid19Codes).map(section => (
            <Row key={section}>
                {section.map(code => (
                    <Col md="4" key={code}>
                        <CheckboxField
                            name={code}
                            label={code}
                            isDisabled={DefaultIcdCodes.includes(code)}
                            value={icd10Codes.includes(code)}
                            className="LabOrderForm-CheckboxField"
                            onChange={onChangeCovidIcdCheckbox}
                        />
                    </Col>
                ))}
            </Row>
        ))
    ), [
        covid19Codes,
        icd10Codes,
        onChangeCovidIcdCheckbox,
    ])

    const SpecimenTypes = useMemo(() => () => (
        specimenTypes.map((type, i, array) => (
            <Row key={type.id}>
                <Col>
                    <CheckboxField
                        name={type.title}
                        label={type.title}
                        value={specimenTypeList.includes(type.id)}
                        className="LabOrderForm-CheckboxField"
                        onChange={() => onChangeSpecimenType(type.id)}
                        hasError={!!errors.specimen?.types}
                        errorText={
                            i === array.length - 1
                                ? errors.specimen?.types
                                : ''
                        }
                    />
                </Col>
            </Row>
        ))
    ), [
        errors.specimen,
        specimenTypeList,
        specimenTypes,
        onChangeSpecimenType
    ])

    return (
        <>
            <Form className="LabOrderForm" onSubmit={onSubmit}>
                <Scrollable style={scrollableStyles}>
                    {step === STEPS.FORM && (
                        <>
                            <div className="LabOrderForm-Section">
                                <div className="LabOrderForm-SectionTitle">
                                    Order Information
                                </div>

                                <Row>
                                    <Col md="4" id="requisition-number">
                                        <TextField
                                            type="text"
                                            isDisabled
                                            maxLength={15}
                                            label="Requisition or Accession Number"
                                            className="LabOrderForm-TextField"
                                        />
                                    </Col>

                                    <Tooltip
                                        target="requisition-number"
                                        placement="top"
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
                                        The number will be generated automatically once the order is placed
                                    </Tooltip>

                                    <Col md="4">
                                        <SelectField
                                            options={reasonsOptions}
                                            name="reason"
                                            value={fields.reason}
                                            label="Reason for Testing*"
                                            className="LabOrderForm-SelectField"
                                            errorText={errors.reason}
                                            onChange={onChangeField}
                                        />
                                    </Col>

                                    <Col md="4">
                                        <TextField
                                            type="text"
                                            name="clinic"
                                            value={fields.clinic}
                                            label="Clinic"
                                            maxLength={256}
                                            className="LabOrderForm-TextField"
                                            onChange={onChangeField}
                                        />
                                    </Col>
                                </Row>

                                <Row>
                                    <Col md="8">
                                        <TextField
                                            type="text"
                                            name="clinicAddress"
                                            value={fields.clinicAddress}
                                            maxLength={256}
                                            label="Clinic Address"
                                            className="LabOrderForm-TextField"
                                            onChange={onChangeField}
                                        />
                                    </Col>
                                </Row>
                            </div>

                            <div className="LabOrderForm-Section">
                                <div className="LabOrderForm-SectionTitle">
                                    Client Information
                                </div>

                                <Row>
                                    <Col md="4">
                                        {isClient ? (
                                            <TextField
                                                type="text"
                                                value={fields.client.fullName}
                                                isDisabled
                                                label="Client name*"
                                                className="LabOrderForm-TextField"
                                            />
                                        ) : (
                                                <SelectField
                                                    name="client.id"
                                                    value={fields.client.id}
                                                    hasKeyboardSearch
                                                    hasKeyboardSearchText
                                                    options={clientsOptions}
                                                    label="Client name*"
                                                    className="LabOrderForm-SelectField"
                                                    onChange={onChangeClientField}
                                                    errorText={errors.client?.id}
                                                />
                                            )}
                                    </Col>

                                    <Col md="4">
                                        <RadioGroupField
                                            name="client.genderId"
                                            selected={fields.client.genderId}
                                            view="row"
                                            title="Sex*"
                                            options={gendersOptions}
                                            onChange={onChangeField}
                                            errorText={errors.client?.genderId}
                                            containerClass="LabOrderForm-RadioGroupField"
                                        />
                                    </Col>

                                    <Col md="4">
                                        <DateField
                                            type="text"
                                            value={formatStringDate(fields.client.birthDate)}
                                            name="client.birthDate"
                                            label="Date of Birth*"
                                            className="LabOrderForm-TextField"
                                            dateFormat="MM/dd/yyyy"
                                            errorText={errors.client?.birthDate}
                                            onChange={onChangeBirthDate}
                                        />
                                    </Col>
                                </Row>

                                <Row>
                                    <Col>
                                        <SelectField
                                            name='client.raceId'
                                            value={fields.client.raceId}
                                            options={racesOptions}
                                            label="Race*"
                                            className="LabOrderForm-SelectField"
                                            errorText={errors.client?.raceId}
                                            onChange={onChangeField}
                                        />
                                    </Col>
                                </Row>

                                <Row>
                                    <Col md="4">
                                        <TextField
                                            type="text"
                                            name="client.ssn"
                                            value={formatSSN(fields.client.ssn)}
                                            label="SSN"
                                            isDisabled
                                            maxLength={9}
                                            className="LabOrderForm-TextField"
                                            onChange={onChangeField}
                                        />
                                    </Col>

                                    <Col md="4">
                                        <PhoneField
                                            name="client.phone"
                                            value={fields.client.phone}
                                            label="Phone #*"
                                            className="LabOrderForm-TextField"
                                            errorText={errors.client?.phone}
                                            onChange={onChangeField}
                                        />
                                    </Col>

                                    <Col md="4">
                                        <TextField
                                            type="text"
                                            name="client.address.street"
                                            value={fields.client.address.street}
                                            maxLength={256}
                                            label="Street*"
                                            className="LabOrderForm-TextField"
                                            errorText={errors.client?.address?.street}
                                            onChange={onChangeField}
                                        />
                                    </Col>
                                </Row>

                                <Row>
                                    <Col md="4">
                                        <TextField
                                            type="text"
                                            name="client.address.city"
                                            value={fields.client.address.city}
                                            maxLength={256}
                                            label="City*"
                                            className="LabOrderForm-TextField"
                                            errorText={errors.client?.address?.city}
                                            onChange={onChangeField}
                                        />
                                    </Col>

                                    <Col md="4">
                                        <SelectField
                                            name="client.address.stateId"
                                            value={fields.client.address.stateId}
                                            options={statesOptions}
                                            label="State*"
                                            className="LabOrderForm-SelectField"
                                            errorText={errors.client?.address?.stateId}
                                            onChange={onChangeField}
                                        />
                                    </Col>

                                    <Col md="4">
                                        <TextField
                                            type="text"
                                            label="Zip Code*"
                                            name="client.address.zip"
                                            value={fields.client.address.zip}
                                            maxLength={5}
                                            className="LabOrderForm-TextField"
                                            errorText={errors.client?.address?.zip}
                                            onChange={onChangeField}
                                        />
                                    </Col>
                                </Row>

                                <Row>
                                    <Col md="4">
                                        <TextField
                                            type="text"
                                            name="client.insuranceNetwork"
                                            value={fields.client.insuranceNetwork}
                                            maxLength={256}
                                            label="Primary Insurance Provider*"
                                            className="LabOrderForm-TextField"
                                            errorText={errors.client?.insuranceNetwork}
                                            onChange={onChangeField}
                                        />
                                    </Col>

                                    <Col md="4">
                                        <TextField
                                            type="text"
                                            name="client.policyNumber"
                                            value={fields.client.policyNumber}
                                            label="Policy #*"
                                            maxLength={256}
                                            className="LabOrderForm-TextField"
                                            errorText={errors.client?.policyNumber}
                                            onChange={onChangeField}
                                        />
                                    </Col>

                                    <Col md="4">
                                        <SelectField
                                            name="client.policyHolderRelationName"
                                            value={fields.client.policyHolderRelationName}
                                            options={policyHolderRelationsOptions}
                                            label="Policy Holder*"
                                            className="LabOrderForm-SelectField"
                                            errorText={errors.client?.policyHolderRelationName}
                                            onChange={onChangePolicyHolderRelationName}
                                        />
                                    </Col>
                                </Row>

                                <Row>
                                    <Col md="4">
                                        <TextField
                                            type="text"
                                            name="client.policyHolderName"
                                            value={fields.client.policyHolderName}
                                            isDisabled={isSelfHolder || !policyHolderRelationName}
                                            label={decorateWithAsterix('Policy Holder Name (if spouse or parent)')}
                                            className="LabOrderForm-TextField"
                                            maxLength={256}
                                            errorText={errors.client?.policyHolderName}
                                            onChange={onChangeField}
                                        />
                                    </Col>

                                    <Col md="4">
                                        <DateField
                                            type="text"
                                            name="client.policyHolderDOB"
                                            value={formatStringDate(fields.client.policyHolderDOB)}
                                            isDisabled={isSelfHolder || !policyHolderRelationName}
                                            label={decorateWithAsterix('Policy Holder DOB')}
                                            className="LabOrderForm-TextField"
                                            dateFormat="MM/dd/yyyy"
                                            errorText={errors.client?.policyHolderDOB}
                                            onChange={onChangePolicyHolderDOB}
                                        />
                                    </Col>
                                </Row>
                            </div>

                            <div className="LabOrderForm-Section">
                                <div className="LabOrderForm-SectionTitle">
                                    Specimen Information
                                </div>

                                <span className={cn('LabOrderForm-FieldLabel', {
                                    'LabOrderForm-FieldLabel__Error': !!errors.specimen?.types
                                })}>
                                    Specimen*
                                </span>

                                <SpecimenTypes />

                                <Row>
                                    <Col md="4">
                                        <TextField
                                            type="text"
                                            name="specimen.collectorName"
                                            value={fields.specimen.collectorName}
                                            label="Collector's Name*"
                                            maxLength={256}
                                            errorText={errors.specimen?.collectorName}
                                            className="LabOrderForm-TextField"
                                            onChange={onChangeField}
                                        />
                                    </Col>

                                    <Col md="4">
                                        <SelectField
                                            name="specimen.site"
                                            value={site}
                                            options={siteOptions}
                                            label="Site*"
                                            hasKeyboardSearch
                                            hasKeyboardSearchText
                                            isDisabled={isFetchingCollectorSites}
                                            className="LabOrderForm-SelectField"
                                            errorText={site !== OTHER_SITE && errors.specimen?.site}
                                            onChange={onChangeSite}
                                        />
                                    </Col>

                                    <Col md="4">
                                        <DateField
                                            type="text"
                                            name="specimen.date"
                                            value={fields.specimen.date}
                                            hasTimeSelect
                                            label="Date & Time*"
                                            className="LabOrderForm-TextField"
                                            dateFormat="MM/dd/yyyy hh:mm a"
                                            timeFormat="h:mm aa"
                                            errorText={errors.specimen?.date}
                                            onChange={onChangeDateField}
                                        />
                                    </Col>
                                </Row>

                                {site === OTHER_SITE && (
                                    <Row>
                                        <Col>
                                            <TextField
                                                type="text"
                                                name="specimen.site"
                                                value={fields.specimen.site}
                                                label="Other*"
                                                maxLength={256}
                                                errorText={errors.specimen?.site}
                                                className="LabOrderForm-TextField"
                                                onChange={onChangeField}
                                            />
                                        </Col>
                                    </Row>
                                )}
                            </div>

                            <div className="LabOrderForm-Section">
                                <div className="LabOrderForm-SectionTitle">
                                    Ordering Provider
                                </div>

                                <Row>
                                    <Col md="4">
                                        <TextField
                                            type="text"
                                            name="providerFirstName"
                                            value={fields.providerFirstName}
                                            label="First Name*"
                                            maxLength={50}
                                            className="LabOrderForm-TextField"
                                            errorText={errors.providerFirstName}
                                            onChange={onChangeField}
                                        />
                                    </Col>

                                    <Col md="4">
                                        <TextField
                                            type="text"
                                            name="providerLastName"
                                            value={fields.providerLastName}
                                            label="Last Name*"
                                            maxLength={50}
                                            className="LabOrderForm-TextField"
                                            errorText={errors.providerLastName}
                                            onChange={onChangeField}
                                        />
                                    </Col>

                                    <Col md="4">
                                        <DateField
                                            type="text"
                                            name="orderDate"
                                            value={fields.orderDate}
                                            label="Date"
                                            className="LabOrderForm-TextField"
                                            dateFormat="MM/dd/yyyy"
                                            onChange={onChangeDateField}
                                        />
                                    </Col>
                                </Row>
                            </div>

                            <div className="LabOrderForm-Section">
                                <div className="LabOrderForm-SectionTitle">
                                    Panels
                                </div>

                                <div className="LabOrderForm-SectionSubTitle margin-top-30">
                                    Urgent COVID-19
                                </div>

                                <Row>
                                    <Col>
                                        <CheckboxField
                                            value={true}
                                            label={urgentMessage}
                                            isDisabled={true}
                                            className="LabOrderForm-CheckboxField no-pointer-events"
                                        />
                                    </Col>
                                </Row>
                            </div>

                            <div className="LabOrderForm-Section">
                                <div className="LabOrderForm-SectionTitle">
                                    ICD 10 Codes
                                </div>

                                <div className="LabOrderForm-FieldLabel">
                                    Diagnosis codes may be typed in this section or checked off in the <span>Commonly Used ICD 10 Codes</span> section below
                                </div>

                                <Row>
                                    <Col md="4">
                                        <TextField
                                            type="text"
                                            name="customIcdCodes.first"
                                            // value={fields.customIcdCodes.first} - CCN-4398. Uncomment it when it's needed
                                            label="ICD 10 Code"
                                            maxLength={50}
                                            className="LabOrderForm-TextField"
                                            errorText={errors.customIcdCodes?.first}
                                            // onChange={onChangeField}

                                            value={DefaultIcdCodes[0]} // - CCN-4398. Remove it when it's needed
                                            isDisabled={true} // - CCN-4398. Remove it when it's needed
                                        />
                                    </Col>

                                    <Col md="4">
                                        <TextField
                                            type="text"
                                            name="customIcdCodes.second"
                                            value={fields.customIcdCodes.second}
                                            label="ICD 10 Code"
                                            maxLength={50}
                                            className="LabOrderForm-TextField"
                                            errorText={errors.customIcdCodes?.second}
                                            onChange={onChangeField}

                                            isDisabled={fields.customIcdCodes.secondIsDisabled} // - CCN-4398. Remove it when it's needed
                                        />
                                    </Col>

                                    <Col md="4">
                                        <TextField
                                            type="text"
                                            name="customIcdCodes.third"
                                            value={fields.customIcdCodes.third}
                                            label="ICD 10 Code"
                                            maxLength={50}
                                            className="LabOrderForm-TextField"
                                            errorText={errors.customIcdCodes?.third}
                                            onChange={onChangeField}

                                            isDisabled={fields.customIcdCodes.thirdIsDisabled} // - CCN-4398. Remove it when it's needed
                                        />
                                    </Col>
                                </Row>
                            </div>

                            <div className="LabOrderForm-Section">
                                <div className="LabOrderForm-SectionTitle">
                                    Commonly Used ICD 10 Codes
                                </div>

                                <div className="LabOrderForm-FieldLabel margin-bottom-20">
                                    Must include at least one CMS approved diagnosis code supporting the medical necessity of all testing.
                                    Diagnosis codes may be typed in the "ICD 10 Codes" section above, checked off below, or contained in attached records.
                                </div>

                                <div className="LabOrderForm-SectionSubTitle">
                                    COVID-19 (SARS-CoV-2)
                                </div>

                                <Covid19CodeGroup />
                            </div>

                            <div className="LabOrderForm-Section">
                                <div className="LabOrderForm-SectionTitle">
                                    Additional Notes
                                </div>

                                <Row>
                                    <Col>
                                        <TextField
                                            label="Notes"
                                            name="notes"
                                            value={fields.notes}
                                            type="textarea"
                                            maxLength={80}
                                            className="LabOrderForm-TextArea"
                                            onChange={onChangeField}
                                        />
                                    </Col>
                                </Row>
                            </div>
                        </>
                    )}

                    {step === STEPS.REVIEW && (
                        <LabResearchOrderReviewDetails
                            data={fields}
                            onChange={onChangeClientData}
                        />
                    )}
                </Scrollable>

                <div className="LabOrderForm-Buttons">
                    {step === STEPS.REVIEW && (
                        <Button
                            color="link"
                            className="LabOrderForm-BackBtn"
                            onClick={onBack}
                        >
                            Back
                        </Button>
                    )}

                    <Button
                        outline
                        color="success"
                        onClick={onCancel}
                    >
                        Cancel
                    </Button>

                    {step === STEPS.FORM && (
                        <Button
                            color="success"
                            onClick={onNext}
                            disabled={isFetching}
                        >
                            Next
                        </Button>
                    )}

                    {step === STEPS.REVIEW && (
                        <Button
                            color="success"
                            disabled={isFetching}
                        >
                            Submit
                        </Button>
                    )}
                </div>
            </Form>
        </>
    )
}

export default compose(
    memo,
    connect(mapStateToProps, mapDispatchToProps)
)(LabOrderForm)
