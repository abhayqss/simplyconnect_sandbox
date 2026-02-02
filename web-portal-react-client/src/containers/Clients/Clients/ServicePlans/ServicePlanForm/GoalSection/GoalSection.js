import React, { Component } from 'react'

import memo from 'memoize-one'
import PropTypes from 'prop-types'

import { map, last } from 'underscore'

import { Col, Row, Button } from 'reactstrap'

import { connect } from 'react-redux'

import withDirectoryData from 'hocs/withDirectoryData'

import {
    TextField,
    DateField,
    PhoneField,
    SelectField,
    CheckboxField,
    RadioGroupField
} from 'components/Form'

import {
    isEmpty,
    trimStartIfString
} from 'lib/utils/Utils'

import {
    noop
} from 'lib/utils/FuncUtils'

import { ReactComponent as Info } from 'images/info.svg'
import { ReactComponent as Delete } from 'images/delete.svg'

import ServiceProviderPicker from '../ServiceProviderPicker/ServiceProviderPicker'

import './GoalSection.scss'

const YES_NO_RADIO_GROUP_OPTIONS = [
    { value: true, label: 'Yes' },
    { value: false, label: 'No' }
]

function mapStateToProps(state) {
    const { can } = state.client.servicePlan

    const canReviewByClinician = can.reviewByClinician.value

    return {
        canReviewByClinician,
        directory: state.directory
    }
}

class GoalSection extends Component {

    static propTypes = {
        clientId: PropTypes.number,
        domainId: PropTypes.number,
        programSubTypeId: PropTypes.number,

        error: PropTypes.string,
        index: PropTypes.number,
        needIndex: PropTypes.number,
        fields: PropTypes.object,

        isValid: PropTypes.bool,

        onDelete: PropTypes.func,
        onChangeField: PropTypes.func,
        onChangeFields: PropTypes.func
    }

    static defaultProps = {
        fields: {},
        onDelete: () => { },
        onChangeField: () => { },
        onChangeFields: () => { }
    }

    state = {
        isServiceProviderPickerOpen: false
    }

    constructor(props) {
        super(props)

        this.getServiceStatusSelectOpts = memo(
            this.getServiceStatusSelectOpts
        )

        this.getServiceControlRequestStatusSelectOpts = memo(
            this.getServiceControlRequestStatusSelectOpts
        )
    }

    onDelete = () => {
        this.props.onDelete(
            this.props.index
        )
    }

    onChangeField = (name, value) => {
        this.props.onChangeField(
            this.props.index,
            last(name.split(':')),
            trimStartIfString(value)
        )
    }

    onChangeFields = (changes) => {
        this.props.onChangeFields(
            this.props.index, changes
        )
    }

    onChangeDateField = (name, value) => {
        this.props.onChangeField(
            this.props.index,
            last(name.split(':')),
            value ? new Date(value).getTime() : null
        )
    }

    onChangeGoalCompletionField = (name, value) => {
        if (isEmpty(value) || /^0*(?:[1-9][0-9]?|100)$/.test(value)) {
            this.onChangeField(name, value)
        }
    }

    onOpenProviderPicker = () => {
        this.setState({
            isServiceProviderPickerOpen: true
        })
    }

    onCloseServiceProviderPicker = () => {
        this.setState({
            isServiceProviderPickerOpen: false
        })
    }

    onChooseServiceProvider = provider => {
        this.onChangeFields({
            providerName: provider.name,
            providerEmail: provider.email,
            providerPhone: provider.phone,
            providerAddress: `${provider.displayAddress}`
        })

        this.onCloseServiceProviderPicker()
    }

    getDirectoryData() {
        return this.props.getDirectoryData({
            serviceStatuses: ['service', 'status'],
            serviceControlRequestStatuses: (
                ['service', 'control', 'request', 'status']
            )
        })
    }

    getServiceControlRequestStatusSelectOpts() {
        const statuses = (
            this.getDirectoryData()
                .serviceControlRequestStatuses
        )

        return map(statuses, o => ({
            text: o.title, value: o.id
        }))
    }

    getServiceStatusSelectOpts() {
        const statuses = (
            this.getDirectoryData()
                .serviceStatuses
        )

        return map(statuses, o => ({
            text: o.title, value: o.id
        }))
    }

    render() {
        const {
            index,
            fields,
            clientId,
            domainId,
            needIndex,
            programSubTypeId,
            canReviewByClinician
        } = this.props

        const {
            serviceStatuses,
            serviceControlRequestStatuses
        } = this.getDirectoryData()

        const { isServiceProviderPickerOpen } = this.state

        const prefix = `${needIndex}:${index}`

        return (
            <div className="GoalSection">
                <div className="GoalSection-Header">
                    <div className="GoalSection-Title">
                        Goal #{index + 1}
                    </div>
                    <Delete
                        className="DeleteBtn"
                        onClick={this.onDelete}
                    />
                </div>
                <Row>
                    <Col md={12}>
                        <TextField
                            type="text"
                            name={prefix + ':goal'}
                            value={fields.goal}
                            label="Goal*"
                            className="GoalSection-TextField"
                            hasError={fields.goalHasError}
                            errorText={fields.goalErrorText}
                            onChange={this.onChangeField}
                        />
                    </Col>
                </Row>
                <Row>
                    <Col md={12}>
                        <TextField
                            type="textarea"
                            name={prefix + ':barriers'}
                            value={fields.barriers}
                            label="Barriers"
                            renderLabelIcon={canReviewByClinician ? () => (
                                <Info
                                    id="barriers-info-hint"
                                    className="GoalSection-SelectFieldLabelIcon"
                                />
                            ) : noop()}
                            tooltip={canReviewByClinician && {
                                target: 'barriers-info-hint',
                                text: 'Please provide the details'
                            }}
                            className="GoalSection-TextAreaField"
                            hasError={fields.barriersHasError}
                            errorText={fields.barriersErrorText}
                            onChange={this.onChangeField}
                        />
                    </Col>
                </Row>
                <Row>
                    <Col md={12}>
                        <TextField
                            type="textarea"
                            name={prefix + ':interventionAction'}
                            value={fields.interventionAction}
                            label="Intervention/Action"
                            className="GoalSection-TextAreaField"
                            hasError={fields.interventionActionHasError}
                            errorText={fields.interventionActionErrorText}
                            onChange={this.onChangeField}
                        />
                    </Col>
                </Row>
                <div className="GoalSection-Header margin-top-15">
                    <div className="GoalSection-Title">
                        Service
                    </div>
                    {/*<Button
                        color={'success'}
                        className="GoalSection-SearchServiceProviderBtn"
                        onClick={this.onOpenProviderPicker}
                    >
                        Search a Provider
                    </Button>*/}
                </div>
                <Row>
                    <Col md={4}>
                        <TextField
                            type="text"
                            name={prefix + ':providerName'}
                            value={fields.providerName}
                            label="Provider Name"
                            className="GoalSection-TextField"
                            hasError={fields.providerNameHasError}
                            errorText={fields.providerNameErrorText}
                            onChange={this.onChangeField}
                        />
                    </Col>
                    <Col md={4}>
                        <TextField
                            type="text"
                            name={prefix + ':providerEmail'}
                            value={fields.providerEmail}
                            label="Provider Email"
                            className="GoalSection-TextField"
                            hasError={fields.providerEmailHasError}
                            errorText={fields.providerEmailErrorText}
                            onChange={this.onChangeField}
                        />
                    </Col>
                    <Col md={4}>
                        <PhoneField
                            name={prefix + ':providerPhone'}
                            value={fields.providerPhone}
                            label="Provider Phone"
                            className="GoalSection-PhoneField"
                            errorText={fields.providerPhoneErrorText}
                            onChange={this.onChangeField}
                        />
                    </Col>
                </Row>
                <Row>
                    <Col md={8}>
                        <TextField
                            type="text"
                            name={prefix + ':providerAddress'}
                            value={fields.providerAddress}
                            label="Address"
                            maxLength={256}
                            className="GoalSection-TextField"
                            onChange={this.onChangeField}
                        />
                    </Col>
                    <Col md={4}>
                        <CheckboxField
                            name={prefix + ':wasPreviouslyInPlace'}
                            value={fields.wasPreviouslyInPlace}
                            label="Service was previously in place"
                            className="GoalSection-CheckboxField"
                            onChange={this.onChangeField}
                        />
                    </Col>
                </Row>
                <Row>
                    <Col md={8}>
                        <TextField
                            type="text"
                            name={prefix + ':resourceName'}
                            value={fields.resourceName}
                            label="Resource Name"
                            className="GoalSection-TextField"
                            hasError={fields.resourceNameHasError}
                            errorText={fields.resourceNameErrorText}
                            onChange={this.onChangeField}
                        />
                    </Col>
                    <Col md={4}>
                        <RadioGroupField
                            title="Ongoing Service?*"
                            name={prefix + ':isOngoingService'}
                            selected={fields.isOngoingService}

                            options={YES_NO_RADIO_GROUP_OPTIONS}

                            view="row"

                            hasError={fields.isOngoingServiceHasError}
                            errorText={fields.isOngoingServiceErrorText}

                            onChange={this.onChangeField}
                            className="GoalSection-TextField"
                        />
                    </Col>
                </Row>
                <Row>
                    <Col md={4}>
                        <TextField
                            type="text"
                            name={prefix + ':contactName'}
                            value={fields.contactName}
                            label="Contact Name"
                            className="GoalSection-TextField"
                            hasError={fields.contactNameHasError}
                            errorText={fields.contactNameErrorText}
                            onChange={this.onChangeField}
                        />
                    </Col>
                    <Col md={4}>
                        <SelectField
                            label="Request Status"
                            name={prefix + ':serviceCtrlReqStatusId'}
                            value={fields.serviceCtrlReqStatusId}

                            options={this.getServiceControlRequestStatusSelectOpts(
                                serviceControlRequestStatuses.length
                            )}

                            isMultiple={false}

                            placeholder="Select"
                            className="GoalSection-SelectField"

                            onChange={this.onChangeField}
                        />
                    </Col>
                    <Col md={4}>
                        <SelectField
                            label="Service Status"
                            name={prefix + ':serviceStatusId'}
                            value={fields.serviceStatusId}

                            options={this.getServiceStatusSelectOpts(
                                serviceStatuses.length
                            )}

                            isMultiple={false}

                            placeholder="Select"
                            className="GoalSection-SelectField"

                            onChange={this.onChangeField}
                        />
                    </Col>
                </Row>
                <Row>
                    <Col md={4}>
                        <DateField
                            name={prefix + ':targetCompletionDate'}
                            className="GoalSection-DateField"
                            value={fields.targetCompletionDate}
                            timeFormat='hh:mm a'
                            dateFormat="MM/dd/yyyy hh:mm a"
                            label="Target Completion Date*"
                            hasTimeSelect={true}
                            hasError={fields.targetCompletionDateHasError}
                            errorText={fields.targetCompletionDateErrorText}
                            onChange={this.onChangeDateField}
                        />
                    </Col>
                    <Col md={4}>
                        <DateField
                            name={prefix + ':completionDate'}
                            className="GoalSection-DateField"
                            value={fields.completionDate}
                            timeFormat='hh:mm a'
                            dateFormat="MM/dd/yyyy hh:mm a"
                            label="Completion Date"
                            hasTimeSelect={true}
                            hasError={fields.completionDateHasError}
                            errorText={fields.completionDateErrorText}
                            onChange={this.onChangeDateField}
                        />
                    </Col>
                    <Col md={4}>
                        <TextField
                            type="text"
                            name={prefix + ':goalCompletion'}
                            value={fields.goalCompletion}
                            label="Goal Completion, %"
                            className="GoalSection-TextField"
                            hasError={fields.goalCompletionHasError}
                            errorText={fields.goalCompletionErrorText}
                            onChange={this.onChangeGoalCompletionField}
                        />
                    </Col>
                </Row>
                {isServiceProviderPickerOpen && (
                    <ServiceProviderPicker
                        isOpen
                        clientId={clientId}
                        domainId={domainId}
                        programSubTypeId={programSubTypeId}

                        onPick={this.onChooseServiceProvider}
                        onClose={this.onCloseServiceProviderPicker}
                    />
                )}
            </div>
        )
    }
}

export default connect(mapStateToProps)(
    withDirectoryData(GoalSection)
)
