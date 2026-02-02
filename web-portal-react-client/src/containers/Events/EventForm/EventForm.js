import React, { Component } from 'react'

import $ from 'jquery'
import memo from 'memoize-one'
import PropTypes from 'prop-types'

import {
    map,
    noop,
    first,
    reject,
    debounce,
    findWhere
} from 'underscore'

import { connect } from 'react-redux'
import { bindActionCreators, compose } from 'redux'

import { Form, Col, Row, Collapse, Button } from 'reactstrap'

import Action from 'components/Action/Action'
import Loader from 'components/Loader/Loader'
import Scrollable from 'components/Scrollable/Scrollable'
import TextField from 'components/Form/TextField/TextField'
import DateField from 'components/Form/DateField/DateField'
import ErrorViewer from 'components/ErrorViewer/ErrorViewer'
import SelectField from 'components/Form/SelectField/SelectField'
import CheckboxField from 'components/Form/CheckboxField/CheckboxField'

import withDirectoryData from 'hocs/withDirectoryData'

import LoadStatesAction from 'actions/directory/LoadStatesAction'
import LoadCTMemberRoles from 'actions/directory/LoadCTMemberRoles'
import LoadEventTypeListAction from 'actions/directory/LoadEventTypesAction'
import LoadNotViewableEventTypesAction from 'actions/clients/LoadNotViewableEventTypesAction'

import eventFormActions from 'redux/event/form/eventFormActions'

import { isInteger } from 'lib/utils/Utils'
import { Response } from 'lib/utils/AjaxUtils'

import {
    EVENT_GROUP_COLORS,
    SERVER_ERROR_CODES
} from 'lib/Constants'

import './EventForm.scss'

const CSS_SELECTORS = {
    ERRORS: '.EventForm .form-control.is-invalid'
}

function getErrorFieldElements() {
    return $(CSS_SELECTORS.ERRORS)
}

function isIgnoredError(e = {}) {
    return e.code === SERVER_ERROR_CODES.ACCOUNT_INACTIVE
}

function mapStateToProps(state) {
    const { form } = state.event

    return {
        error: form.error,
        fields: form.fields,
        isValid: form.isValid(),
        validation: form.validation,
        isFetching: form.isFetching,
        getHashCode: form.getHashCode,
        updateHashCode: form.updateHashCode,

        auth: state.auth,
        client: state.client,
        directory: state.directory
    }
}

function mapDispatchToProps(dispatch) {
    return {
        actions: bindActionCreators(eventFormActions, dispatch)
    }
}

class EventForm extends Component {
    static propTypes = {
        clientId: PropTypes.number,

        onCancel: PropTypes.func,
        onSubmitSuccess: PropTypes.func
    }

    static defaultProps = {
        onCancel: noop(),
        onSubmitSuccess: noop()
    }

    scrollableRef = React.createRef()

    constructor(props) {
        super(props)

        this.getStateSelectOptions = memo(
            this.getStateSelectOptions
        )

        this.getAuthorRoleSelectOptions = memo(
            this.getAuthorRoleSelectOptions
        )

        this.getEventTypeSelectSections = memo(
            this.getEventTypeSelectSections
        )

        this.validateWithDebounce = debounce(
            this.validateWithDebounce, 300
        )
    }

    componentDidMount() {
        this.actions.changeFields({
            essentials: {
                date: Date.now(),
                author: this.authUser?.fullName
            }
        }, true)
    }

    onResetError = () => {
        this.actions.clearError()
    }

    onCancel = () => {
        this.props.onCancel()
    }

    onChangeField = (name, value) => {
        this.actions.changeField(name, value)

        this.validateIfInvalid()
    }

    onChangeDateField = (name, value) => {
        this.actions.changeField(
            name, value ? value.getTime() : null
        )

        this.validateIfInvalid()
    }

    onSubmit = (e) => {
        e.preventDefault()

        this.validate().then(success => {
            if (success) {
                this.submit().then(Response(
                    ({ data }) => {
                        this.props.onSubmitSuccess(data)
                    }
                ))
            }

            else this.scroll(first(
                getErrorFieldElements()
            ))
        })
    }

    get actions() {
        return this.props.actions
    }

    get authUser() {
        return this.props.auth.login.user.data
    }

    scroll(...args) {
        this.scrollableRef.current?.scroll(...args) || noop()
    }

    scrollTop(duration) {
        this.scrollableRef.current?.scrollTop(duration) || noop()
    }

    validate() {
        return this.actions.validate(
            this.props.fields.toJS()
        )
    }

    validateWithDebounce = () => {
        return this.validate()
    }

    validateIfInvalid = () => {
        if (!this.props.isValid) {
            this.validateWithDebounce()
        }
    }

    submit() {
        return this.actions.submit(
            this.getData(),
            { clientId: this.props.clientId }
        )
    }

    getData(isMinified = true) {
        const data = this.props.fields.toJS()

        if (isMinified) {
            const {
                treatment,
                registeredNurse,
                hasRegisteredNurse,
                hasResponsibleManager
            } = data

            if (!treatment.physician.hasAddress) {
                delete treatment.physician.address
            }

            if (!treatment.hasPhysician) {
                delete treatment.physician
            }

            if (!treatment.hospital.hasAddress) {
                delete treatment.hospital.address
            }

            if (!treatment.hasHospital) {
                delete treatment.hospital
            }

            if (!(treatment.hasPhysician || treatment.hasHospital)) {
                delete data.treatment
            }

            if (!registeredNurse.hasAddress) {
                delete registeredNurse.address
            }

            if (!hasRegisteredNurse) {
                delete data.registeredNurse
            }

            if (!hasResponsibleManager) {
                delete data.responsibleManager
            }
        }

        return data
    }

    getDirectoryData() {
        return this.props.getDirectoryData({
            states: ['state'],
            eventTypes: ['event', 'type'],
            roles: ['care', 'team', 'role'],
        })
    }

    getEventTypeById(id) {
        const groups = (
            this.getDirectoryData().eventTypes
        )

        for (let i = 0; i < groups.length; i++) {
            const type = findWhere(
                groups[i].eventTypes,
                { id }
            )

            if (type) return type
        }
    }

    getNotViewableEventTypeIds() {
        return (
            this.props
                .client
                .event
                .notViewable
                .type
                .list
                .dataSource
                .data
        ) || []
    }

    getStateSelectOptions() {
        return this.getDirectoryData().states.map(
            ({ id, label }) => ({
                value: id,
                text: label
            })
        )
    }

    getAuthorRoleSelectOptions() {
        return this.getDirectoryData().roles.map(
            ({ title }) => ({
                value: title,
                text: title
            })
        )
    }

    getEventTypeSelectSections() {
        const typeIds = (
            this.getNotViewableEventTypeIds()
        )

        return map(
            reject(
                this.getDirectoryData().eventTypes,
                o => o.isService
            ),
            group => ({
                id: group.id,
                name: group.name,
                title: group.title,
                options: map(
                    reject(group.eventTypes, o => o.isService),
                    o => ({
                        text: o.title,
                        value: o.id,
                        isDisabled: typeIds.includes(o.id)
                    })
                )
            })
        )
    }

    getEventTypeSelectSectionColor(section) {
        return EVENT_GROUP_COLORS[section.name]
    }

    render() {
        const {
            error,
            fields,
            clientId,
            isFetching,
            validation: { errors }
        } = this.props

        const client = (
            this.props.client.details.data || {}
        )

        const {
            roles,
            states,
            eventTypes
        } = this.getDirectoryData()

        const {
            essentials,
            description,
            treatment,

            hasResponsibleManager,
            responsibleManager,

            hasRegisteredNurse,
            registeredNurse
        } = fields
        return (
            <>
                <Form className="EventForm" onSubmit={this.onSubmit}>
                    <LoadStatesAction/>
                    <LoadEventTypeListAction/>
                    <LoadCTMemberRoles params={{ clientId }}/>
                    <LoadNotViewableEventTypesAction
                        params={{ clientId }}
                        shouldPerform={() => isInteger(clientId)}
                    />

                    <Action
                        isMultiple
                        params={{ user: this.authUser }}
                        shouldPerform={prev => !prev.user && this.authUser}
                        action={() => {
                            this.actions.changeFields({
                                essentials: { author: this.authUser.fullName }
                            }, true)
                        }}
                    />

                    <Action
                        performingPhase="unmounting"
                        action={this.actions.clear}
                    />

                    {isFetching && (
                        <Loader
                            hasBackdrop
                            style={{ position: 'fixed' }}
                        />
                    )}

                    <Scrollable ref={this.scrollableRef} style={{ flex: 1 }}>
                        <div className="EventForm-Section EventForm-ClientInfoSection">
                            <div className="EventForm-SectionTitle">
                                Client Info
                            </div>
                            <Row>
                                <Col md={6}>
                                    <TextField
                                        label="Community"

                                        name="community"
                                        value={client.community}

                                        isDisabled

                                        className="EventForm-TextField"
                                    />
                                </Col>
                                <Col md={6}>
                                    <TextField
                                        label="Organization"

                                        name="organization"
                                        value={client.organization}

                                        isDisabled

                                        className="EventForm-TextField"
                                    />
                                </Col>
                            </Row>
                            <Row>
                                <Col md={4}>
                                    <TextField
                                        label="First Name"

                                        name="firstName"
                                        value={client.firstName}

                                        isDisabled

                                        className="EventForm-TextField"
                                    />
                                </Col>
                                <Col md={4}>
                                    <TextField
                                        label="Last Name"

                                        name="lastName"
                                        value={client.lastName}

                                        isDisabled

                                        className="EventForm-TextField"
                                    />
                                </Col>
                                <Col md={4}>
                                    <TextField
                                        label="Social Security Number"

                                        name="ssn"
                                        value={client.ssn && `###-##-${client.ssn.substr(-4, 4)}`}

                                        isDisabled

                                        className="EventForm-TextField"
                                    />
                                </Col>
                            </Row>
                        </div>
                        <div className="EventForm-Section EventForm-EssentialsSection">
                            <div className="EventForm-SectionTitle">
                                Event Essentials
                            </div>
                            <Row>
                                <Col md={4}>
                                    <TextField
                                        label="Person Submitting Event"

                                        isDisabled

                                        name="essentials.author"
                                        value={essentials.author}

                                        className="EventForm-TextField"
                                    />
                                </Col>
                                <Col md={4}>
                                    <SelectField
                                        label="Care Team Role*"

                                        name="essentials.authorRole"
                                        value={essentials.authorRole}
                                        options={this.getAuthorRoleSelectOptions(roles.length)}

                                        hasError={errors.essentials.authorRole}
                                        errorText={errors.essentials.authorRole}

                                        onChange={this.onChangeField}

                                        className="EventForm-TextField"
                                    />
                                </Col>
                                <Col md={4}>
                                    <DateField
                                        label="Event Date and Time*"

                                        name="essentials.date"
                                        value={essentials.date}

                                        hasTimeSelect
                                        timeFormat="hh:mm aa"
                                        dateFormat="MM/dd/yyyy hh:mm a"

                                        hasError={errors.essentials.date}
                                        errorText={errors.essentials.date}

                                        onChange={this.onChangeDateField}

                                        className="EventForm-TextField"
                                    />
                                </Col>
                            </Row>
                            <Row>
                                <Col md={8}>
                                    <SelectField
                                        label="Event Type*"

                                        name="essentials.typeId"
                                        value={essentials.typeId}

                                        isSectioned

                                        optionType="tick"
                                        hasSectionIndicator
                                        hasSectionSeparator
                                        sectionIndicatorColor={this.getEventTypeSelectSectionColor}
                                        sections={this.getEventTypeSelectSections(
                                            eventTypes.length,
                                            this.getNotViewableEventTypeIds().length
                                        )}

                                        hasError={errors.essentials.typeId}
                                        errorText={errors.essentials.typeId}

                                        onChange={this.onChangeField}

                                        className="EventForm-EventTypeSelectField"
                                    />
                                </Col>
                            </Row>
                            <Row>
                                <Col md={8} className="d-flex margin-top-16">
                                    <CheckboxField
                                        label="Emergency Department Visit"

                                        name="essentials.isEmergencyDepartmentVisit"
                                        value={essentials.isEmergencyDepartmentVisit}

                                        onChange={this.onChangeField}

                                        className="EventForm-CheckboxField"
                                    />
                                    <CheckboxField
                                        label="Overnight In-patient"

                                        name="essentials.isOvernightInpatient"
                                        value={essentials.isOvernightInpatient}

                                        onChange={this.onChangeField}

                                        className="EventForm-CheckboxField margin-left-20"
                                    />
                                </Col>
                            </Row>
                        </div>
                        <div className="EventForm-Section EventForm-DescriptionSection">
                            <div className="EventForm-SectionTitle">
                                Event Description
                            </div>
                            <Row>
                                <Col md={12}>
                                    <TextField
                                        label="Location"

                                        name="description.location"
                                        value={description.location}

                                        type='textarea'

                                        hasError={errors.description.location}
                                        errorText={errors.description.location}

                                        onChange={this.onChangeField}

                                        className="EventForm-TextArea"
                                    />
                                </Col>
                            </Row>
                            <Row>
                                <Col md={12}>
                                    <TextField
                                        label="Situation"

                                        name="description.situation"
                                        value={description.situation}

                                        type='textarea'

                                        hasError={errors.description.situation}
                                        errorText={errors.description.situation}

                                        onChange={this.onChangeField}

                                        className="EventForm-TextArea"
                                    />
                                </Col>
                            </Row>
                            <Row>
                                <Col md={12}>
                                    <TextField
                                        label="Background"

                                        name="description.background"
                                        value={description.background}

                                        type='textarea'

                                        hasError={errors.description.background}
                                        errorText={errors.description.background}

                                        onChange={this.onChangeField}

                                        className="EventForm-TextArea"
                                    />
                                </Col>
                            </Row>
                            <Row>
                                <Col md={12}>
                                    <TextField
                                        label="Assessment"

                                        name="description.assessment"
                                        value={description.assessment}

                                        type='textarea'

                                        hasError={errors.description.assessment}
                                        errorText={errors.description.assessment}

                                        onChange={this.onChangeField}

                                        className="EventForm-TextArea"
                                    />
                                </Col>
                            </Row>
                            <Row>
                                <Col md={6}>
                                    <CheckboxField
                                        label="Injury"

                                        name="description.hasInjury"
                                        value={description.hasInjury}

                                        onChange={this.onChangeField}

                                        className="EventForm-CheckboxField margin-bottom-16"
                                    />
                                </Col>
                            </Row>
                            <Row>
                                <Col md={6}>
                                    <CheckboxField
                                        label="Follow up expected"

                                        name="description.isFollowUpExpected"
                                        value={description.isFollowUpExpected}

                                        onChange={this.onChangeField}

                                        className="EventForm-CheckboxField"
                                    />
                                </Col>
                            </Row>
                            <Collapse isOpen={description.isFollowUpExpected}>
                                <Row>
                                    <Col md={12}>
                                        <TextField
                                            label="Follow Up*"

                                            name="description.followUpDetails"
                                            value={description.followUpDetails}

                                            type='textarea'

                                            hasError={errors.description.followUpDetails}
                                            errorText={errors.description.followUpDetails}

                                            onChange={this.onChangeField}

                                            className="EventForm-TextArea"
                                        />
                                    </Col>
                                </Row>
                            </Collapse>
                        </div>
                        <div className="EventForm-Section EventForm-TreatmentSection">
                            <div className="EventForm-SectionTitle">
                                Treatment Details
                            </div>
                            <Row>
                                <Col md={6}>
                                    <CheckboxField
                                        label="Include details of treating physician"

                                        name="treatment.hasPhysician"
                                        value={treatment.hasPhysician}

                                        onChange={this.onChangeField}

                                        className="EventForm-CheckboxField"
                                    />
                                </Col>
                            </Row>
                            <Collapse isOpen={treatment.hasPhysician}>
                                <Row>
                                    <Col md={4}>
                                        <TextField
                                            label="First Name*"

                                            name="treatment.physician.firstName"
                                            value={treatment.physician.firstName}

                                            placeholder="At least 2 characters"

                                            hasError={errors.treatment.physician.firstName}
                                            errorText={errors.treatment.physician.firstName}

                                            onChange={this.onChangeField}

                                            className="EventForm-TextField"
                                        />
                                    </Col>
                                    <Col md={4}>
                                        <TextField
                                            label="Last Name*"

                                            name="treatment.physician.lastName"
                                            value={treatment.physician.lastName}

                                            placeholder="At least 2 characters"

                                            hasError={errors.treatment.physician.lastName}
                                            errorText={errors.treatment.physician.lastName}

                                            onChange={this.onChangeField}

                                            className="EventForm-TextField"
                                        />
                                    </Col>
                                    <Col md={4}>
                                        <TextField
                                            label="Phone #"

                                            name="treatment.physician.phone"
                                            value={treatment.physician.phone}

                                            hasError={errors.treatment.physician.phone}
                                            errorText={errors.treatment.physician.phone}

                                            maxLength={16}
                                            onChange={this.onChangeField}

                                            className="EventForm-TextField"
                                        />
                                    </Col>
                                </Row>
                                <Row>
                                    <Col md={6}>
                                        <CheckboxField
                                            label="Physician Address"

                                            name="treatment.physician.hasAddress"
                                            value={treatment.physician.hasAddress}

                                            onChange={this.onChangeField}

                                            className="EventForm-CheckboxField"
                                        />
                                    </Col>
                                </Row>
                                <Collapse isOpen={treatment.physician.hasAddress}>
                                    <Row>
                                        <Col md={4}>
                                            <TextField
                                                label="Street*"

                                                name="treatment.physician.address.street"
                                                value={treatment.physician.address.street}

                                                hasError={errors.treatment.physician.address.street}
                                                errorText={errors.treatment.physician.address.street}

                                                onChange={this.onChangeField}

                                                className="EventForm-TextField"
                                            />
                                        </Col>
                                        <Col md={4}>
                                            <TextField
                                                label="City*"

                                                name="treatment.physician.address.city"
                                                value={treatment.physician.address.city}

                                                hasError={errors.treatment.physician.address.city}
                                                errorText={errors.treatment.physician.address.city}

                                                onChange={this.onChangeField}

                                                className="EventForm-TextField"
                                            />
                                        </Col>
                                        <Col md={4}>
                                            <SelectField
                                                label="State*"

                                                name="treatment.physician.address.stateId"
                                                value={treatment.physician.address.stateId}
                                                options={this.getStateSelectOptions(states.length)}

                                                placeholder="Select State"

                                                hasError={errors.treatment.physician.address.stateId}
                                                errorText={errors.treatment.physician.address.stateId}

                                                onChange={this.onChangeField}

                                                className="EventForm-SelectField"
                                            />
                                        </Col>
                                    </Row>
                                    <Row>
                                        <Col md={4}>
                                            <TextField
                                                label="Zip Code*"

                                                name="treatment.physician.address.zip"
                                                value={treatment.physician.address.zip}

                                                hasError={errors.treatment.physician.address.zip}
                                                errorText={errors.treatment.physician.address.zip}

                                                onChange={this.onChangeField}

                                                className="EventForm-TextField"
                                            />
                                        </Col>
                                    </Row>
                                </Collapse>
                            </Collapse>
                            <Row>
                                <Col md={6}>
                                    <CheckboxField
                                        label="Include details of treating hospital"

                                        name="treatment.hasHospital"
                                        value={treatment.hasHospital}

                                        onChange={this.onChangeField}

                                        className="EventForm-CheckboxField"
                                    />
                                </Col>
                            </Row>
                            <Collapse isOpen={treatment.hasHospital}>
                                <Row>
                                    <Col md={8}>
                                        <TextField
                                            label="Hospital Name*"

                                            name="treatment.hospital.name"
                                            value={treatment.hospital.name}

                                            placeholder="At least 2 characters"

                                            hasError={errors.treatment.hospital.name}
                                            errorText={errors.treatment.hospital.name}

                                            onChange={this.onChangeField}

                                            className="EventForm-TextField"
                                        />
                                    </Col>
                                    <Col md={4}>
                                        <TextField
                                            label="Phone #"

                                            name="treatment.hospital.phone"
                                            value={treatment.hospital.phone}

                                            hasError={errors.treatment.hospital.phone}
                                            errorText={errors.treatment.hospital.phone}

                                            maxLength={16}
                                            onChange={this.onChangeField}

                                            className="EventForm-TextField"
                                        />
                                    </Col>
                                </Row>
                                <Row>
                                    <Col md={6}>
                                        <CheckboxField
                                            label="Hospital Address"

                                            name="treatment.hospital.hasAddress"
                                            value={treatment.hospital.hasAddress}

                                            onChange={this.onChangeField}

                                            className="EventForm-CheckboxField"
                                        />
                                    </Col>
                                </Row>
                                <Collapse isOpen={treatment.hospital.hasAddress}>
                                    <Row>
                                        <Col md={4}>
                                            <TextField
                                                label="Street*"

                                                name="treatment.hospital.address.street"
                                                value={treatment.hospital.address.street}

                                                hasError={errors.treatment.hospital.address.street}
                                                errorText={errors.treatment.hospital.address.street}

                                                onChange={this.onChangeField}

                                                className="EventForm-TextField"
                                            />
                                        </Col>
                                        <Col md={4}>
                                            <TextField
                                                label="City*"

                                                name="treatment.hospital.address.city"
                                                value={treatment.hospital.address.city}

                                                hasError={errors.treatment.hospital.address.city}
                                                errorText={errors.treatment.hospital.address.city}

                                                onChange={this.onChangeField}

                                                className="EventForm-TextField"
                                            />
                                        </Col>
                                        <Col md={4}>
                                            <SelectField
                                                label="State*"

                                                name="treatment.hospital.address.stateId"
                                                value={treatment.hospital.address.stateId}
                                                options={this.getStateSelectOptions(states.length)}

                                                placeholder="Select State"

                                                hasError={errors.treatment.hospital.address.stateId}
                                                errorText={errors.treatment.hospital.address.stateId}

                                                onChange={this.onChangeField}

                                                className="EventForm-SelectField"
                                            />
                                        </Col>
                                    </Row>
                                    <Row>
                                        <Col md={4}>
                                            <TextField
                                                label="Zip Code*"

                                                name="treatment.hospital.address.zip"
                                                value={treatment.hospital.address.zip}

                                                hasError={errors.treatment.hospital.address.zip}
                                                errorText={errors.treatment.hospital.address.zip}

                                                onChange={this.onChangeField}

                                                className="EventForm-TextField"
                                            />
                                        </Col>
                                    </Row>
                                </Collapse>
                            </Collapse>
                        </div>
                        <div className="EventForm-Section EventForm-ResponsibleManagerSection">
                            <div className="EventForm-SectionTitle">
                                Details of Responsible Manager
                            </div>
                            <Row>
                                <Col md={6}>
                                    <CheckboxField
                                        label="Include details of responsible manager"

                                        name="hasResponsibleManager"
                                        value={hasResponsibleManager}

                                        onChange={this.onChangeField}

                                        className="EventForm-CheckboxField"
                                    />
                                </Col>
                            </Row>
                            <Collapse isOpen={hasResponsibleManager}>
                                <Row>
                                    <Col md={4}>
                                        <TextField
                                            label="First Name*"

                                            name="responsibleManager.firstName"
                                            value={responsibleManager.firstName}

                                            placeholder="At least 2 characters"

                                            hasError={errors.responsibleManager.firstName}
                                            errorText={errors.responsibleManager.firstName}

                                            onChange={this.onChangeField}

                                            className="EventForm-TextField"
                                        />
                                    </Col>
                                    <Col md={4}>
                                        <TextField
                                            label="Last Name*"

                                            name="responsibleManager.lastName"
                                            value={responsibleManager.lastName}

                                            placeholder="At least 2 characters"

                                            hasError={errors.responsibleManager.lastName}
                                            errorText={errors.responsibleManager.lastName}

                                            onChange={this.onChangeField}

                                            className="EventForm-TextField"
                                        />
                                    </Col>
                                    <Col md={4}>
                                        <TextField
                                            label="Phone #"

                                            name="responsibleManager.phone"
                                            value={responsibleManager.phone}


                                            hasError={errors.responsibleManager.phone}
                                            errorText={errors.responsibleManager.phone}

                                            maxLength={16}
                                            onChange={this.onChangeField}

                                            className="EventForm-TextField"
                                        />
                                    </Col>
                                </Row>
                                <Row>
                                    <Col md={8}>
                                        <TextField
                                            label="Email"

                                            name="responsibleManager.email"
                                            value={responsibleManager.email}

                                            hasError={errors.responsibleManager.email}
                                            errorText={errors.responsibleManager.email}

                                            onChange={this.onChangeField}

                                            className="EventForm-TextField"
                                        />
                                    </Col>
                                </Row>
                            </Collapse>
                        </div>
                        <div className="EventForm-Section EventForm-RegisteredNurseSection">
                            <div className="EventForm-SectionTitle">
                                Details of Registered Nurse (RN)
                            </div>
                            <Row>
                                <Col md={6}>
                                    <CheckboxField
                                        label="Include details of registered nurse"

                                        name="hasRegisteredNurse"
                                        value={hasRegisteredNurse}

                                        onChange={this.onChangeField}

                                        className="EventForm-CheckboxField"
                                    />
                                </Col>
                            </Row>
                            <Collapse isOpen={hasRegisteredNurse}>
                                <Row>
                                    <Col md={4}>
                                        <TextField
                                            label="First Name*"

                                            name="registeredNurse.firstName"
                                            value={registeredNurse.firstName}

                                            placeholder="At least 2 characters"

                                            hasError={errors.registeredNurse.firstName}
                                            errorText={errors.registeredNurse.firstName}

                                            onChange={this.onChangeField}

                                            className="EventForm-TextField"
                                        />
                                    </Col>
                                    <Col md={4}>
                                        <TextField
                                            label="Last Name*"

                                            name="registeredNurse.lastName"
                                            value={registeredNurse.lastName}

                                            placeholder="At least 2 characters"

                                            hasError={errors.registeredNurse.lastName}
                                            errorText={errors.registeredNurse.lastName}

                                            onChange={this.onChangeField}

                                            className="EventForm-TextField"
                                        />
                                    </Col>
                                </Row>
                                <Row>
                                    <Col md={6}>
                                        <CheckboxField
                                            label="RN Address"

                                            name="registeredNurse.hasAddress"
                                            value={registeredNurse.hasAddress}

                                            onChange={this.onChangeField}

                                            className="EventForm-CheckboxField"
                                        />
                                    </Col>
                                </Row>
                                <Collapse isOpen={registeredNurse.hasAddress}>
                                    <Row>
                                        <Col md={4}>
                                            <TextField
                                                label="Street*"

                                                name="registeredNurse.address.street"
                                                value={registeredNurse.address.street}

                                                hasError={errors.registeredNurse.address.street}
                                                errorText={errors.registeredNurse.address.street}

                                                onChange={this.onChangeField}

                                                className="EventForm-TextField"
                                            />
                                        </Col>
                                        <Col md={4}>
                                            <TextField
                                                label="City*"

                                                name="registeredNurse.address.city"
                                                value={registeredNurse.address.city}

                                                hasError={errors.registeredNurse.address.city}
                                                errorText={errors.registeredNurse.address.city}

                                                onChange={this.onChangeField}

                                                className="EventForm-TextField"
                                            />
                                        </Col>
                                        <Col md={4}>
                                            <SelectField
                                                label="State*"

                                                name="registeredNurse.address.stateId"
                                                value={registeredNurse.address.stateId}
                                                options={this.getStateSelectOptions(states.length)}

                                                placeholder="Select State"

                                                hasError={errors.registeredNurse.address.stateId}
                                                errorText={errors.registeredNurse.address.stateId}

                                                onChange={this.onChangeField}

                                                className="EventForm-SelectField"
                                            />
                                        </Col>
                                    </Row>
                                    <Row>
                                        <Col md={4}>
                                            <TextField
                                                label="Zip Code*"

                                                name="registeredNurse.address.zip"
                                                value={registeredNurse.address.zip}

                                                hasError={errors.registeredNurse.address.zip}
                                                errorText={errors.registeredNurse.address.zip}

                                                onChange={this.onChangeField}

                                                className="EventForm-TextField"
                                            />
                                        </Col>
                                    </Row>
                                </Collapse>
                            </Collapse>
                        </div>
                    </Scrollable>
                    <div className="EventForm-Buttons">
                        <Button
                            outline
                            color='success'
                            disabled={isFetching}
                            onClick={this.onCancel}
                        >
                            Cancel
                        </Button>
                        <Button
                            color='success'
                            disabled={isFetching}
                        >
                            Submit
                        </Button>
                    </div>
                </Form>
                {error && !isIgnoredError(error) && (
                    <ErrorViewer
                        isOpen
                        error={error}
                        onClose={this.onResetError}
                    />
                )}
            </>
        )
    }
}

export default compose(
    connect(mapStateToProps, mapDispatchToProps),
    withDirectoryData
)(EventForm)