import React, {
    memo,
    useMemo,
    useState,
    useEffect,
    useCallback,
} from 'react'

import { compose, bindActionCreators } from 'redux'
import { connect } from 'react-redux'

import moment from 'moment'

import { isNumber, noop } from 'underscore'

import {
    Form,
    Col,
    Row,
    Button,
    UncontrolledTooltip as Tooltip
} from 'reactstrap'

import { Loader, AlertPanel, BodyDiagram } from 'components'

import {
    TextField,
    DateField,
    PhoneField,
    CheckboxField,
    DropzoneField,
    RadioGroupField
} from 'components/Form'

import {
    NotificationSectionRow,
    OtherNotificationSectionRow,
    PersonNotificationSectionRow,
    PhysicianNotificationSectionRow,
} from './components'

import { ReactComponent as CrossIcon } from 'images/cross.svg'

import * as errorActions from 'redux/error/errorActions'
import incidentReportFormActions from 'redux/incident/report/form/incidentReportFormActions'
import incidentPictureActions from 'redux/incident/picture/details/incidentPictureDetailsActions'

import {
    useForm,
    useResponse,
    useScrollable,
    useDirectoryData,
    useScrollToFormError
} from 'hooks/common'

import {
    useGendersQuery,
    useIncidentTypesQuery,
    useIncidentPlacesQuery,
    useIncidentWeatherConditionTypesQuery,
} from 'hooks/business/directory'

import EventNotifications from '../EventNotifications/EventNotifications'

import {
    useIncidentReportDetails,
    useDefaultIncidentReport,
} from 'hooks/business/incident-report'

import Witness from 'entities/Witness'
import Individual from 'entities/Individual'
import IncidentReport from 'entities/IncidentReport'

import IncidentReportFormValidator from 'validators/IncidentReportFormValidator'

import {
    isInteger,
    isNotEmpty,
    getDataUrl,
    omitEmptyProps,
    DateUtils as DU
} from 'lib/utils/Utils'

import Converter from 'lib/converters/Converter'
import factory from 'lib/converters/ConverterFactory'

import './IncidentReportForm.scss'

const { format, formats } = DU

const DATE_FORMAT = formats.americanMediumDate

const YES_NO_OPTIONS = [
    { value: true, label: 'Yes' },
    { value: false, label: 'No' }
]

const formatDate = value => value ? format(value, DATE_FORMAT) : null
const formatStringDate = value => value ? moment(value, 'MM/DD/YYYY').toDate().getTime() : null

const converter = factory.getConverter(Converter.types.DATA_URL_TO_FILE)

const scrollableStyles = { flex: 1 }

const DRAFT = 'DRAFT'
const SUBMITTED = 'SUBMITTED'

const getData = (fields) => {
    let data = fields.toJS()

    let removeEmptyItems = arr => arr.filter(o => isNotEmpty(omitEmptyProps(o)))
    let removeDisabledNotifications = notifications => {
        return Object.keys(notifications).reduce((accum, name) => {
            if (!notifications[name].isNotified) {
                delete accum[name]
            }

            return accum
        }, notifications)
    }

    let trimPhoneNumber = o => ({
        ...o,
        phone: o.phone.trim()
    })

    return {
        ...data,
        client: trimPhoneNumber(data.client),
        vitalSigns: isNotEmpty(omitEmptyProps(data.vitalSigns)) ? data.vitalSigns : null,
        notification: removeDisabledNotifications(data.notification),
        witnesses: removeEmptyItems(data.witnesses).map(trimPhoneNumber),
        involvedIndividuals: data.wereOtherIndividualsInvolved ? removeEmptyItems(data.involvedIndividuals).map(trimPhoneNumber) : null,
        incidentParticipantHospitalName: data.wasIncidentParticipantTakenToHospital ? data.incidentParticipantHospitalName : '',
        injuries: data.wereApparentInjuries ? data.injuries : [],
        currentInjuredClientCondition: data.wereApparentInjuries ? data.currentInjuredClientCondition : '',
    }
}

const withAsterisk = (str, shouldDecorate) => (
    shouldDecorate ? (str + '*') : str
)

const mapDispatchToProps = (dispatch) => {
    return {
        actions: {
            ...bindActionCreators(incidentReportFormActions, dispatch),
            picture: bindActionCreators(incidentPictureActions, dispatch),
            error: bindActionCreators(errorActions, dispatch),
        }
    }
}

function IncidentReportForm(
    {
        onClose,
        actions,
        eventId,
        clientId,
        reportId,
        organizationId,
        onSubmitSuccess,
        forwardContext = noop,
    }
) {
    const isEditMode = isNumber(reportId)

    const {
        places,
        weatherConditions,
    } = useDirectoryData({
        places: ['incident', 'place'],
        weatherConditions: ['incident', 'weather', 'condition', 'type']
    })

    const {
        fields,
        errors,
        validate,
        isChanged,
        changeField: onChangeField,
        changeFields: onChangeFields,
        changeDateField: onChangeDateField
    } = useForm('IncidentReport', IncidentReport, IncidentReportFormValidator)

    const {
        state: {
            data: defaultData,
            isFetching: isFetchingDefaultData
        },
        fetch: fetchDefaultData,
    } = useDefaultIncidentReport({ eventId })

    const {
        state: {
            data: details,
            isFetching: isFetchingDetails
        },
        fetch: fetchDetails,
    } = useIncidentReportDetails(reportId)

    const [needValidation, setNeedValidation] = useState(false)
    const [isFetching, setIsFetching] = useState(false)
    const [status, setStatus] = useState(DRAFT)
    const [pictures, setPictures] = useState([])

    let isDraft = status === DRAFT

    const validationOptions = useMemo(() => ({
        included: { isDraft }
    }), [isDraft])

    const { Scrollable, scroll } = useScrollable()

    function cancel() {
        onClose(isChanged)
    }

    const onResponse = useResponse({
        onFailure: actions.error.change,
        onSuccess: useCallback(({ data }) => onSubmitSuccess(data, isDraft), [isDraft, onSubmitSuccess]),
        onUnknown: actions.error.change
    })

    function submit(statusName) {
        setIsFetching(true)
        setStatus(statusName)

        let isDraft = statusName === DRAFT

        validate({
            ...validationOptions,
            included: {
                ...validationOptions.included,
                isDraft
            }
        })
            .then(async () => {
                onResponse(
                    await actions.submit(getData(fields), isDraft)
                )
                setNeedValidation(false)
            })
            .catch(() => {
                onScroll()
                setNeedValidation(true)
            })
            .finally(() => {
                setIsFetching(false)
            })
    }

    function validateIf() {
        if (needValidation) {
            validate(validationOptions)
                .then(() => setNeedValidation(false))
                .catch(() => setNeedValidation(true))
        }
    }

    function setDefaultData() {
        if (defaultData != null) {
            onChangeFields(defaultData, true)
        }
    }

    async function fetchIncidentPictures(pictures) {
        let promises = pictures.map(picture => (
            actions.picture.load(picture.id)
        ))

        Promise.all(promises).then(result => {
            let fileNameRegexp = /(.+?)\.[^.]+$/

            setPictures(
                result.map((picture, index) => {
                    let name = pictures[index].name.match(fileNameRegexp)[1]

                    return {
                        ...picture,
                        name,
                    }
                })
            )
        })
    }

    function setFormData() {
        if (details != null) {
            let setIsNotified = o => Object.keys(o).reduce((notification, key) => (
                (notification[key].isNotified = true) && notification
            ), o)

            setStatus(details.statusName)
            onChangeFields({
                ...details,
                notification: details.notification ? setIsNotified(details.notification) : undefined
            }, true)

            if (details.incidentPictures) {
                fetchIncidentPictures(details.incidentPictures)
            }
        }
    }

    function setFilesToFormData() {
        if (pictures.length > 0) {
            setPictures([])

            try {
                let files = pictures.map(picture => (
                    converter.convert(getDataUrl(picture.data, picture.mediaType), picture.name)
                ))

                onChangeFields({ incidentPictureFiles: files }, true)
            } catch {
                console.error('Something went wrong during the pictures loading')
            }
        }
    }

    const onChangeStringDateField = (field, value) => (
        onChangeField(field, formatDate(value))
    )

    const onAddListItem = (field, Entity) => (
        onChangeField(field, fields.get(field).push(Entity()))
    )

    const onRemoveListItem = (field, index) => (
        onChangeField(field, fields.get(field).remove(index))
    )

    const onChangePlaces = (place, shouldAdd) => {
        let value = shouldAdd
            ? fields.places.push(place)
            : fields.places.filter(o => o.id !== place.id)

        onChangeField('places', value)
    }

    const onChangePlaceInfo = (id, text) => {
        let value = fields.places.update(
            fields.places.findIndex(o => o.id === id),
            item => ({ ...item, text })
        )

        onChangeField('places', value)
    }

    const onChangeWeatherConditions = (weather, shouldAdd) => {
        let value = shouldAdd
            ? fields.weatherConditions.push(weather)
            : fields.weatherConditions.filter(o => o.id !== weather.id)

        onChangeField('weatherConditions', value)
    }

    const onChangeWeatherInfo = (id, text) => {
        let value = fields.weatherConditions.update(
            fields.weatherConditions.findIndex(o => o.id === id),
            item => ({ ...item, text })
        )

        onChangeField('weatherConditions', value)
    }

    const onAddIndividual = () => onAddListItem('involvedIndividuals', Individual)
    const onRemoveIndividual = index => onRemoveListItem('involvedIndividuals', index)

    const onAddWitness = () => onAddListItem('witnesses', Witness)
    const onRemoveWitness = index => onRemoveListItem('witnesses', index)

    const onScroll = useScrollToFormError('.IncidentReportForm', scroll)

    const onSubmit = useCallback(submit, [
        validate, actions.submit,
        onResponse, fields, validationOptions
    ])

    const onCancel = useCallback(cancel, [onClose, isChanged])

    forwardContext({ onCancel })

    useGendersQuery(useMemo(() => ({
        biologicalOnly: true
    }), []))

    useIncidentTypesQuery()
    useIncidentPlacesQuery()
    useIncidentWeatherConditionTypesQuery()

    useEffect(validateIf, [needValidation, onScroll, validate, validationOptions])

    useEffect(() => {
        if (isEditMode) {
            fetchDetails()
        } else if (isInteger(eventId)) {
            fetchDefaultData()
        }
    }, [eventId, isEditMode, fetchDefaultData, fetchDetails])

    useEffect(() => {
        if (!isEditMode) {
            onChangeFields({ eventId }, true)
        }
    }, [eventId, isEditMode, onChangeFields])

    useEffect(setDefaultData, [defaultData, onChangeFields])
    useEffect(setFormData, [details, onChangeFields])

    useEffect(setFilesToFormData, [pictures, onChangeFields])

    return (
        <>
            <Form className="IncidentReportForm">
                <Scrollable style={scrollableStyles}>
                    {(isFetching || isFetchingDetails || isFetchingDefaultData) && (
                        <Loader
                            hasBackdrop
                            style={{ position: 'fixed' }}
                        />
                    )}

                    <div className="IncidentReportForm-Section">
                        <div className="IncidentReportForm-SectionTitle">
                            Client Info
                        </div>

                        <Row>
                            <Col md="4">
                                <TextField
                                    type="text"
                                    value={fields.client.fullName}
                                    isDisabled
                                    label="Class Member's Name*"
                                    className="IncidentReportForm-TextField"
                                />
                            </Col>

                            <Col md="4">
                                <TextField
                                    type="text"
                                    name="client.unit"
                                    label="Unit #*"
                                    value={fields.client.unit}
                                    maxLength={256}
                                    className="IncidentReportForm-TextField"
                                    errorText={errors.client?.unit}
                                    onChange={onChangeField}
                                />
                            </Col>

                            <Col md="4">
                                <PhoneField
                                    name="client.phone"
                                    label="Phone #*"
                                    value={fields.client.phone}
                                    className="IncidentReportForm-TextField"
                                    errorText={errors.client?.phone}
                                    onChange={onChangeField}
                                />
                            </Col>
                        </Row>

                        <Row>
                            <Col md="4">
                                <TextField
                                    type="text"
                                    name="client.siteName"
                                    label="Site Name*"
                                    value={fields.client.siteName}
                                    maxLength={256}
                                    className="IncidentReportForm-TextField"
                                    errorText={errors.client?.siteName}
                                    onChange={onChangeField}
                                />
                            </Col>

                            <Col md="8">
                                <TextField
                                    type="text"
                                    name="client.address"
                                    label="Address*"
                                    value={fields.client.address}
                                    maxLength={256}
                                    className="IncidentReportForm-TextField"
                                    errorText={errors.client?.address}
                                    onChange={onChangeField}
                                />
                            </Col>
                        </Row>
                    </div>

                    <div className="IncidentReportForm-Section">
                        <div className="IncidentReportForm-SectionSubTitle">
                            Incident Information
                        </div>

                        <Row>
                            <Col md="4">
                                <DateField
                                    name="incidentDate"
                                    className="IncidentReportForm-DateField"
                                    value={fields.incidentDate}
                                    label={"Date of Incident*"}
                                    errorText={errors.incidentDate}
                                    placeholder="mm/dd/yyyy"
                                    onChange={onChangeDateField}
                                />
                            </Col>

                            <Col md="4">
                                <DateField
                                    name="incidentDate"
                                    value={fields.incidentDate}
                                    label="Time of Incident*"
                                    className="NoteForm-DateField"
                                    dateFormat="h:mm aa"
                                    timeFormat="h:mm aa"
                                    placeholder="hh:mm"
                                    hasTimeSelect
                                    hasTimeSelectOnly
                                    errorText={errors.incidentDate}
                                    onChange={onChangeDateField}
                                />
                            </Col>

                            <Col md="4">
                                <DateField
                                    name="incidentDiscoveredDate"
                                    className="IncidentReportForm-DateField"
                                    value={formatStringDate(fields.incidentDiscoveredDate)}
                                    label="Date discovered by agency staff*"
                                    errorText={errors.incidentDiscoveredDate}
                                    placeholder="mm/dd/yyyy"
                                    onChange={onChangeStringDateField}
                                />
                            </Col>
                        </Row>

                        <Row>
                            <Col>
                                <RadioGroupField
                                    view="col"
                                    name="wasProviderPresentOrScheduled"
                                    className="IncidentReportForm-RadioGroupField"
                                    selected={fields.wasProviderPresentOrScheduled}
                                    title="Did the incident occur when a provider was present or was scheduled to be present?*"
                                    options={YES_NO_OPTIONS}
                                    errorText={errors.wasProviderPresentOrScheduled}
                                    onChange={onChangeField}
                                />
                            </Col>
                        </Row>

                        <div className="IncidentReportForm-FieldTitle">
                            Where did the incident take place?
                        </div>

                        <Row className="margin-bottom-20">
                            <Col>
                                {places.map(place => {
                                    let target = fields.places.find(o => o.id === place.id)

                                    return (
                                        <React.Fragment key={place.id}>
                                            <CheckboxField
                                                label={place.name}
                                                name="places"
                                                value={target}
                                                onChange={(_, value) => onChangePlaces(place, value)}
                                                errorText={errors.places}
                                                className="IncidentReportForm-CheckboxField"
                                            />

                                            {target && place.value && (
                                                <TextField
                                                    type="text"
                                                    name={place.id}
                                                    value={target?.text}
                                                    maxLength={256}
                                                    className="IncidentReportForm-TextField"
                                                    onChange={onChangePlaceInfo}
                                                />
                                            )}
                                        </React.Fragment>
                                    )
                                })}
                            </Col>
                        </Row>

                        <div className="IncidentReportForm-FieldTitle">
                            Weather conditions
                        </div>

                        <Row className="margin-bottom-20">
                            <Col>
                                {weatherConditions.map(o => {
                                    let target = fields.weatherConditions.find(weather => weather.id === o.id)

                                    return (
                                        <React.Fragment key={o.id}>
                                            <CheckboxField
                                                label={o.name}
                                                name="weather-condition"
                                                value={target}
                                                onChange={(_, value) => onChangeWeatherConditions(o, value)}
                                                className="IncidentReportForm-CheckboxField"
                                            />

                                            {target && o.value && (
                                                <TextField
                                                    type="text"
                                                    name={o.id}
                                                    value={target.text}
                                                    maxLength={256}
                                                    className="IncidentReportForm-TextField"
                                                    onChange={onChangeWeatherInfo}
                                                />
                                            )}
                                        </React.Fragment>
                                    )
                                })}
                            </Col>
                        </Row>

                        <Row>
                            <Col>
                                <TextField
                                    type="textarea"
                                    name="incidentDetails"
                                    label="Details of the alleged incident"
                                    value={fields.incidentDetails}
                                    className="IncidentReportForm-TextField"
                                    maxLength={20000}
                                    onChange={onChangeField}
                                />
                            </Col>
                        </Row>

                        <Row>
                            <Col>
                                <RadioGroupField
                                    view="col"
                                    name="wasIncidentParticipantTakenToHospital"
                                    className="IncidentReportForm-RadioGroupField"
                                    selected={fields.wasIncidentParticipantTakenToHospital}
                                    title="Was the participant taken to the hospital?"
                                    options={YES_NO_OPTIONS}
                                    onChange={onChangeField}
                                />
                            </Col>
                        </Row>

                        {fields.wasIncidentParticipantTakenToHospital && (
                            <Row>
                                <Col md="8">
                                    <TextField
                                        type="textarea"
                                        name="incidentParticipantHospitalName"
                                        label="Hospital name"
                                        value={fields.incidentParticipantHospitalName}
                                        className="IncidentReportForm-TextField"
                                        errorText={errors.incidentParticipantHospitalName}
                                        maxLength={256}
                                        onChange={onChangeField}
                                    />
                                </Col>
                            </Row>
                        )}

                        <Row>
                            <Col>
                                <RadioGroupField
                                    view="col"
                                    name="wereApparentInjuries"
                                    className="IncidentReportForm-RadioGroupField"
                                    selected={fields.wereApparentInjuries}
                                    title="Were there apparent injuries?"
                                    options={YES_NO_OPTIONS}
                                    onChange={onChangeField}
                                />
                            </Col>
                        </Row>

                        <Row>
                            <Col md="6">
                                <BodyDiagram
                                    id="injuries"
                                    name="injuries"
                                    value={fields.injuries}
                                    isDisabled={!fields.wereApparentInjuries}
                                    label={withAsterisk(
                                        'Click on the diagram to mark injuries',
                                        fields.wereApparentInjuries
                                    )}
                                    className="IncidentReport-BodyDiagram margin-bottom-20"
                                    error={errors.injuries}
                                    onChange={onChangeField}
                                />

                                {!fields.wereApparentInjuries && (
                                    <Tooltip
                                        flip={false}
                                        target="injuries"
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
                                        You have not specified that there were injuries
                                    </Tooltip>
                                )}
                            </Col>
                        </Row>

                        <Row>
                            <Col>
                                <TextField
                                    type="textarea"
                                    name="currentInjuredClientCondition"
                                    label={withAsterisk(
                                        'If injury, the current condition of the injured participant/resident',
                                        fields.wereApparentInjuries
                                    )}
                                    value={fields.currentInjuredClientCondition}
                                    isDisabled={!fields.wereApparentInjuries}
                                    className="IncidentReportForm-TextField"
                                    maxLength={5000}
                                    errorText={errors.currentInjuredClientCondition}
                                    onChange={onChangeField}
                                />
                            </Col>
                        </Row>
                    </div>

                    <div className="IncidentReportForm-Section">
                        <div className="IncidentReportForm-SectionTitle">
                            Vital Signs
                        </div>

                        <Row>
                            <Col md="4">
                                <TextField
                                    type="text"
                                    name="vitalSigns.bloodPressure"
                                    label="Blood Pressure"
                                    value={fields.vitalSigns.bloodPressure}
                                    className="IncidentReportForm-TextField"
                                    maxLength={256}
                                    onChange={onChangeField}
                                />
                            </Col>

                            <Col md="4">
                                <TextField
                                    type="text"
                                    name="vitalSigns.pulse"
                                    label="Pulse"
                                    value={fields.vitalSigns.pulse}
                                    className="IncidentReportForm-TextField"
                                    maxLength={256}
                                    onChange={onChangeField}
                                />
                            </Col>

                            <Col md="4">
                                <TextField
                                    type="text"
                                    name="vitalSigns.respirationRate"
                                    label="Respiration rate"
                                    value={fields.vitalSigns.respirationRate}
                                    className="IncidentReportForm-TextField"
                                    maxLength={256}
                                    onChange={onChangeField}
                                />
                            </Col>
                        </Row>

                        <Row>
                            <Col md="4">
                                <TextField
                                    type="text"
                                    name="vitalSigns.temperature"
                                    label="Temperature"
                                    value={fields.vitalSigns.temperature}
                                    className="IncidentReportForm-TextField"
                                    maxLength={256}
                                    onChange={onChangeField}
                                />
                            </Col>

                            <Col md="4">
                                <TextField
                                    type="text"
                                    name="vitalSigns.o2Saturation"
                                    label="O2 Saturation"
                                    value={fields.vitalSigns.o2Saturation}
                                    className="IncidentReportForm-TextField"
                                    maxLength={256}
                                    onChange={onChangeField}
                                />
                            </Col>

                            <Col md="4">
                                <TextField
                                    type="text"
                                    name="vitalSigns.bloodSugar"
                                    label="Blood sugar"
                                    value={fields.vitalSigns.bloodSugar}
                                    className="IncidentReportForm-TextField"
                                    maxLength={256}
                                    onChange={onChangeField}
                                />
                            </Col>
                        </Row>
                    </div>

                    <div className="IncidentReportForm-Section">
                        <div className="IncidentReportForm-SectionTitle IncidentReportForm-SectionTitle_withButton">
                            Witness

                            <Button
                                color="success"
                                onClick={onAddWitness}
                            >
                                Add Person
                            </Button>
                        </div>
                    </div>

                    {fields.witnesses.map((person, index) => (
                        <div className="IncidentReportForm-Section" key={person.id || index}>
                            <div className="IncidentReportForm-SectionSubTitle IncidentReportForm-SectionTitle_withButton">
                                Witness {index + 1}

                                <CrossIcon
                                    onClick={() => onRemoveWitness(index)}
                                    className="IncidentReportForm-CrossIcon"
                                />
                            </div>

                            <Row>
                                <Col md="4">
                                    <TextField
                                        type="text"
                                        name={`witnesses.${index}.name`}
                                        label="First and last name"
                                        value={person.name}
                                        className="IncidentReportForm-TextField"
                                        maxLength={512}
                                        onChange={onChangeField}
                                    />
                                </Col>

                                <Col md="4">
                                    <TextField
                                        type="text"
                                        name={`witnesses.${index}.relationship`}
                                        label="Relationship"
                                        value={person.relationship}
                                        className="IncidentReportForm-TextField"
                                        maxLength={256}
                                        onChange={onChangeField}
                                    />
                                </Col>

                                <Col md="4">
                                    <PhoneField
                                        name={`witnesses.${index}.phone`}
                                        label="Phone #"
                                        value={person.phone}
                                        className="IncidentReportForm-TextField"
                                        errorText={errors.witnesses?.[index]?.phone}
                                        onChange={onChangeField}
                                    />
                                </Col>
                            </Row>

                            <Row>
                                <Col>
                                    <TextField
                                        type="textarea"
                                        name={`witnesses.${index}.report`}
                                        label="Report"
                                        value={person.report}
                                        className="IncidentReportForm-TextField"
                                        maxLength={20000}
                                        onChange={onChangeField}
                                    />
                                </Col>
                            </Row>
                        </div>
                    ))}

                    <div className="IncidentReportForm-Section">
                        <div className="IncidentReportForm-SectionTitle IncidentReportForm-SectionTitle_withButton">
                            Individuals involved in the incident

                            {fields.wereOtherIndividualsInvolved && (
                                <Button
                                    color="success"
                                    onClick={onAddIndividual}
                                >
                                    Add Person
                                </Button>
                            )}
                        </div>

                        <Row>
                            <Col>
                                <RadioGroupField
                                    view="col"
                                    name="wereOtherIndividualsInvolved"
                                    className="IncidentReportForm-RadioGroupField"
                                    selected={fields.wereOtherIndividualsInvolved}
                                    title="Were other individuals involved in the incident?"
                                    options={YES_NO_OPTIONS}
                                    onChange={onChangeField}
                                />
                            </Col>
                        </Row>
                    </div>

                    {fields.wereOtherIndividualsInvolved && fields.involvedIndividuals.map((individual, index) => (
                        <div className="IncidentReportForm-Section" key={individual.id || index}>
                            <div className="IncidentReportForm-SectionSubTitle IncidentReportForm-SectionTitle_withButton">
                                Individual {index + 1}

                                <CrossIcon
                                    onClick={() => onRemoveIndividual(index)}
                                    className="IncidentReportForm-CrossIcon"
                                />
                            </div>

                            <Row>
                                <Col md="4">
                                    <TextField
                                        type="text"
                                        name={`involvedIndividuals.${index}.name`}
                                        label="First and last name"
                                        value={individual.name}
                                        className="IncidentReportForm-TextField"
                                        maxLength={256}
                                        onChange={onChangeField}
                                    />
                                </Col>

                                <Col md="4">
                                    <TextField
                                        type="text"
                                        name={`involvedIndividuals.${index}.relationship`}
                                        label="Relationship"
                                        value={individual.relationship}
                                        className="IncidentReportForm-TextField"
                                        maxLength={256}
                                        onChange={onChangeField}
                                    />
                                </Col>

                                <Col md="4">
                                    <PhoneField
                                        name={`involvedIndividuals.${index}.phone`}
                                        label="Phone #"
                                        value={individual.phone}
                                        className="IncidentReportForm-TextField"
                                        errorText={errors.involvedIndividuals?.[index]?.phone}
                                        onChange={onChangeField}
                                    />
                                </Col>
                            </Row>
                        </div>
                    ))}

                    <div className="IncidentReportForm-Section">
                        <div className="IncidentReportForm-SectionTitle">
                            Pictures of scene
                        </div>

                        <Row>
                            <Col>
                                <DropzoneField
                                    name="incidentPictureFiles"
                                    label="Pictures of scene"
                                    value={fields.incidentPictureFiles}
                                    maxCount={10}
                                    hintText="Supported file types: PDF, PNG, JPG, JPEG, GIF, TIFF | Max 20 mb"
                                    className="IncidentReportForm-DropzoneField"
                                    errors={errors.incidentPictureFiles}
                                    onChange={onChangeField}
                                />
                            </Col>
                        </Row>
                    </div>

                    <div className="IncidentReportForm-Section">
                        <div className="IncidentReportForm-SectionTitle">
                            Notified
                        </div>

                        <div className="IncidentReportForm-SectionSubTitle">
                            Notified about Event

                            <EventNotifications
                                eventId={eventId}
                                clientId={clientId}
                                organizationId={organizationId}
                            />
                        </div>

                        <div className="IncidentReportForm-SectionSubTitle">
                            Notified about Incident
                        </div>

                        <PersonNotificationSectionRow
                            label="Family"
                            data={fields.notification.family}
                            name="notification.family"
                            errors={errors?.notification?.family}
                            onChangeField={onChangeField}
                            onChangeDateField={onChangeDateField}
                        />

                        <PersonNotificationSectionRow
                            label="Friend"
                            data={fields.notification.friend}
                            name="notification.friend"
                            errors={errors?.notification?.friend}
                            onChangeField={onChangeField}
                            onChangeDateField={onChangeDateField}
                        />

                        <PhysicianNotificationSectionRow
                            label="Physician"
                            data={fields.notification.physician}
                            name="notification.physician"
                            errors={errors?.notification?.physician}
                            onChangeField={onChangeField}
                            onChangeDateField={onChangeDateField}
                        />

                        <NotificationSectionRow
                            label="Adult Protective Services"
                            data={fields.notification.adultProtectiveServices}
                            name="notification.adultProtectiveServices"
                            errors={errors?.notification?.adultProtectiveServices}
                            onChangeField={onChangeField}
                            onChangeDateField={onChangeDateField}
                        />

                        <PersonNotificationSectionRow
                            label="Care Manager"
                            data={fields.notification.careManager}
                            name="notification.careManager"
                            errors={errors?.notification?.careManager}
                            onChangeField={onChangeField}
                            onChangeDateField={onChangeDateField}
                            hint="CM must be notified within 1 business day from the date we are notified of incident for Passport, AL-waiver, ADC"
                        />

                        <NotificationSectionRow
                            label="Ohio department of Health"
                            data={fields.notification.ohioHealthDepartment}
                            name="notification.ohioHealthDepartment"
                            errors={errors?.notification?.ohioHealthDepartment}
                            onChangeField={onChangeField}
                            onChangeDateField={onChangeDateField}
                            hint="ODH (for AL only) prefer report within 5 business days.  If required"
                        />

                        <NotificationSectionRow
                            label="9-1-1"
                            data={fields.notification.emergency}
                            name="notification.emergency"
                            errors={errors?.notification?.emergency}
                            onChangeField={onChangeField}
                            onChangeDateField={onChangeDateField}
                        />

                        <NotificationSectionRow
                            label="Police"
                            data={fields.notification.police}
                            name="notification.police"
                            errors={errors?.notification?.police}
                            onChangeField={onChangeField}
                            onChangeDateField={onChangeDateField}
                        />

                        <OtherNotificationSectionRow
                            label="Other"
                            data={fields.notification.other}
                            name="notification.other"
                            errors={errors?.notification?.other}
                            onChangeField={onChangeField}
                            onChangeDateField={onChangeDateField}
                        />

                        <Row>
                            <Col>
                                <TextField
                                    type="textarea"
                                    name="immediateIntervention"
                                    label="Immediate intervention*"
                                    value={fields.immediateIntervention}
                                    className="IncidentReportForm-TextField IncidentReportForm-TextField_size_big"
                                    maxLength={20000}
                                    errorText={errors.immediateIntervention}
                                    onChange={onChangeField}
                                />
                            </Col>
                        </Row>

                        <Row>
                            <Col>
                                <TextField
                                    type="textarea"
                                    name="followUpInformation"
                                    label="Follow up information*"
                                    value={fields.followUpInformation}
                                    className="IncidentReportForm-TextField IncidentReportForm-TextField_size_big"
                                    maxLength={20000}
                                    errorText={errors.followUpInformation}
                                    onChange={onChangeField}
                                />
                            </Col>
                        </Row>

                        <div className="IncidentReportForm-SectionSubTitle">
                            Reporting
                        </div>

                        <Row>
                            <Col md="4">
                                <TextField
                                    type="text"
                                    name="completedBy"
                                    label="Completed by whom*"
                                    value={fields.completedBy}
                                    className="IncidentReportForm-TextField"
                                    errorText={errors.completedBy}
                                    maxLength={256}
                                    onChange={onChangeField}
                                />
                            </Col>

                            <Col md="4">
                                <TextField
                                    type="text"
                                    name="completedByPosition"
                                    label="Position*"
                                    value={fields.completedByPosition}
                                    className="IncidentReportForm-TextField"
                                    maxLength={256}
                                    errorText={errors.completedByPosition}
                                    onChange={onChangeField}
                                />
                            </Col>

                            <Col md="4">
                                <PhoneField
                                    name="completedByPhone"
                                    label="Phone number*"
                                    value={fields.completedByPhone}
                                    className="IncidentReportForm-TextField"
                                    errorText={errors.completedByPhone}
                                    onChange={onChangeField}
                                />
                            </Col>
                        </Row>

                        <Row>
                            <Col md="4">
                                <DateField
                                    name="completedDate"
                                    className="IncidentReportForm-DateField"
                                    value={fields.completedDate}
                                    label="Date completed*"
                                    placeholder="mm/dd/yyyy"
                                    errorText={errors.completedDate}
                                    onChange={onChangeDateField}
                                />
                            </Col>
                        </Row>

                        <Row>
                            <Col md="4">
                                <TextField
                                    type="text"
                                    name="reportedBy"
                                    label="Reported by whom*"
                                    value={fields.reportedBy}
                                    className="IncidentReportForm-TextField"
                                    errorText={errors.reportedBy}
                                    maxLength={256}
                                    onChange={onChangeField}
                                />
                            </Col>

                            <Col md="4">
                                <TextField
                                    type="text"
                                    name="reportedByPosition"
                                    label="Position*"
                                    value={fields.reportedByPosition}
                                    className="IncidentReportForm-TextField"
                                    errorText={errors.reportedByPosition}
                                    maxLength={256}
                                    onChange={onChangeField}
                                />
                            </Col>

                            <Col md="4">
                                <PhoneField
                                    name="reportedByPhone"
                                    label="Phone #*"
                                    value={fields.reportedByPhone}
                                    className="IncidentReportForm-TextField"
                                    errorText={errors.reportedByPhone}
                                    onChange={onChangeField}
                                />
                            </Col>
                        </Row>

                        <Row>
                            <Col md="4">
                                <DateField
                                    name="reportDate"
                                    hasTimeSelect
                                    timeFormat="hh:mm aa"
                                    dateFormat="MM/dd/yyyy hh:mm a"
                                    className="IncidentReportForm-DateField"
                                    value={fields.reportDate}
                                    label={`Date & time of Report*`}
                                    placeholder="mm/dd/yyyy"
                                    errorText={errors.reportDate}
                                    onChange={onChangeDateField}
                                />
                            </Col>
                        </Row>

                        <AlertPanel>
                            <div>PRIVILEGED AND CONFIDENTIAL REPORT</div>
                            <div>PREPARED IN CONNECTION WITH QUALITY ASSURANCE COMMITTEE AND AT DIRECTION OF COUNSEL AND PROTECTED BY RISK MANAGEMENT, PEER REVIEW, AND ATTORNEY CLIENT PRIVILEGES</div>
                        </AlertPanel>
                    </div>
                </Scrollable>

                <div className="IncidentReportForm-Buttons">
                    <Button
                        outline
                        color="success"
                        onClick={() => onSubmit(SUBMITTED)}
                        disabled={isFetching}
                    >
                        Submit
                    </Button>

                    {(fields.statusName !== SUBMITTED &&
                        <Button
                            color="success"
                            onClick={() => onSubmit(DRAFT)}
                            disabled={isFetching}
                        >
                            Save Draft
                        </Button>
                    )}
                </div>
            </Form>
        </>
    )
}

export default compose(
    memo,
    connect(null, mapDispatchToProps)
)(IncidentReportForm)
