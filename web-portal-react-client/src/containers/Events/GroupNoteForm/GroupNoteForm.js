import React, { useCallback, memo, useMemo, useEffect } from 'react'

import { connect } from 'react-redux'
import { bindActionCreators, compose } from 'redux'

import { isNumber } from 'underscore'

import { Form, Col, Row, Button } from 'reactstrap'

import {
    useScrollable,
    useDirectoryData,
    useSelectOptions,
    useScrollToFormError
} from 'hooks/common'

import {
    useNoteTypesQuery,
    useEncounterTypesQuery
} from 'hooks/business/directory'
import {
    useClientProgramNoteTypesQuery,
} from 'hooks/business/directory/query'

import {
    useActiveClientList
} from 'hooks/business/event'

import { useFormSubmit } from 'hooks/common/redux'

import noteFormActions from 'redux/group-note/form/groupNoteFormActions'
import * as errorActions from 'redux/error/errorActions'

import TextField from 'components/Form/TextField/TextField'
import DateField from 'components/Form/DateField/DateField'
import SelectField from 'components/Form/SelectField/SelectField'

import { map } from 'lib/utils/ArrayUtils'

import { useNoteFormBehaviour } from '../hooks'

import EncounterSection from '../NoteForm/EncounterSection/EncounterSection'

import {
    HIE_CONSENT_POLICIES
} from 'lib/Constants'

import '../NoteForm/NoteForm.scss'

const scrollableStyles = { flex: 1 }

const CLIENT_PROGRAM = 'CLIENT_PROGRAM'

const isClientProgramType = type => type?.name === CLIENT_PROGRAM

function mapStateToProps(state) {
    return {
        state: state.groupNote.form,
        user: state.auth.login.user.data,
        details: state.note.details.data,
    }
}

function mapDispatchToProps(dispatch) {
    return {
        actions: {
            ...bindActionCreators(noteFormActions, dispatch),
            error: bindActionCreators(errorActions, dispatch),
        }
    }
}

function GroupNoteForm(
    {
        user,
        state,
        actions,
        noteId,
        details,
        eventId,
        onClose,
        clientId,
        communityId,
        organizationId,
        onSubmitSuccess
    }
) {
    const {
        fields,
        isFetching,
        validation: { errors }
    } = state

    const {
        plan,
        noteName,
        noteDate,
        encounter,
        subTypeId,
        objective,
        assessment,
        subjective,
        clientProgram,
    } = fields

    const isEditMode = isNumber(noteId)
    const params = useMemo(() => ({ clientId, organizationId }), [clientId, organizationId])
    const clients = useMemo(() => fields.clients.toArray(), [fields.clients])

    const { noteTypes } = useDirectoryData({
        noteTypes: ['note', 'type'],
    })

    const activeClientsParams = useMemo(() => ({
        organizationId,
        communityIds: [communityId],
        hieConsentPolicyName: HIE_CONSENT_POLICIES.OPT_IN
    }), [organizationId, communityId])

    const {
        state: {
            dataSource: { data: clientList }
        },
        fetch: onFetchClients
    } = useActiveClientList(activeClientsParams)

    const { data: clientProgramNoteTypes } = useClientProgramNoteTypesQuery()

    const selectedNoteType = useMemo(() => (
        noteTypes.find(type => type.id === subTypeId)
    ), [noteTypes, subTypeId])

    const isClientProgramTypeSelected = isClientProgramType(selectedNoteType)

    const noteTypeOptions = useMemo(() => {
        return noteTypes
            .filter(type => type.canCreate)
            .filter(type => type.canCreateGroupNote)
            .map(type => ({
                text: type.title,
                value: type.id,
            }))
    }, [noteTypes])

    const clientsOptions = useMemo(() => {
        return clientList.map(client => {
            const o = {
                text: client.fullName,
                value: client.id
            }

            if (
                client.hieConsentPolicyName === HIE_CONSENT_POLICIES.OPT_OUT
                && client.primaryContactTypeName === 'CARE_TEAM_MEMBER'
            ) {
                o.tooltip = 'Group notes can\'t be created for Opted out clients'
                o.isDisabled = true
            }

            return o
        })
    }, [clientList])

    const EncounterSubTypes = useMemo(() => {
        return noteTypes
            .filter(type => type.encounterCode)
            .map(type => type.id)
    }, [noteTypes])

    const clientProgramNoteTypeOptions = useSelectOptions(clientProgramNoteTypes)

    const { scroll, Scrollable } = useScrollable()
    const scrollToError = useScrollToFormError('.NoteForm', scroll)

    const validationOptions = useMemo(() => (
        {
            included: {
                EncounterSubTypes,
                clientProgramSelected: isClientProgramTypeSelected,
            }
        }
    ), [EncounterSubTypes, isClientProgramTypeSelected])

    const validate = useCallback((data) => {
        actions.validate(data, validationOptions)
    }, [actions, validationOptions])

    const validation = useMemo(() => ({
        options: validationOptions
    }), [validationOptions])

    const submit = useFormSubmit(state, actions, params, { validation })

    const onChangeField = useCallback((field, value) => {
        actions.changeField(field, value)
    }, [actions])

    const onChangeFields = useCallback(changes => {
        actions.changeFields(changes)
    }, [actions])

    const onChangeDateField = useCallback((field, dateValue) => {
        const value = dateValue ? dateValue.getTime() : ''

        onChangeField(field, value)
    }, [onChangeField])

    const onChangeEncounterField = useCallback((field, value) => (
        onChangeField(`encounter.${field}`, value)
    ), [onChangeField])

    const onChangeEncounterFields = useCallback(changes => (
        onChangeFields({ encounter: changes })
    ), [onChangeFields])

    const onCancel = useCallback(() => onClose(
        state.isChanged()
    ), [onClose, state])

    const data = useMemo(() => {
        return {
            ...fields.toJS(),
            clients: clients.map(id => ({ id })),
            clientProgram: isClientProgramTypeSelected ? clientProgram.toJS() : null,
            encounter: {
                ...encounter.toJS(),
                clinicianId: encounter.clinicianId > 0 ? encounter.clinicianId : null
            }
        }
    }, [clientProgram, clients, encounter, fields, isClientProgramTypeSelected])

    const onSubmitFailure = useCallback(e => {
        actions.error.change(e)
    }, [actions])

    const { onSubmit } = useNoteFormBehaviour(
        state,
        {
            validate,
            scrollToError,
            submit: useCallback(() => submit(data), [data, submit])
        },
        {
            onCancel,
            onSuccess: onSubmitSuccess,
            onFailure: onSubmitFailure
        }
    )

    useNoteTypesQuery()
    useEncounterTypesQuery()

    useEffect(() => {
        if (!isEditMode) {
            actions.changeFields({
                eventId,
                noteDate: new Date().getTime(),
            }, true)
        } else if (details) {
            actions.changeFields({
                ...details,
                clients: map(details.clients, o => o.id)
            }, true)
        }
    }, [actions, details, eventId, isEditMode])

    useEffect(() => () => actions.clear(), [actions])

    useEffect(() => { onFetchClients() }, [onFetchClients])

    return (
        <Form className="NoteForm" onSubmit={onSubmit}>
            <Scrollable style={scrollableStyles}>
                <div className="NoteForm-Section">
                    <Row>
                        <Col md={6}>
                            <TextField
                                type="text"
                                name="author"
                                value={user?.fullName}
                                isDisabled
                                label="Person Submitting Note*"
                                className="NoteForm-TextField"
                            />
                        </Col>
                        <Col md={6}>
                            <DateField
                                type="text"
                                hasTimeSelect
                                name="noteDate"
                                value={noteDate}
                                isDisabled={isEditMode}
                                label="Note Date and Time*"
                                className="NoteForm-TextField"
                                dateFormat="MM/dd/yyyy hh:mm a"
                                timeFormat="h:mm aa"
                                errorText={errors.noteDate}
                                onChange={onChangeDateField}
                            />
                        </Col>
                    </Row>
                    <Row>
                        <Col md={6}>
                            <SelectField
                                type="text"
                                hasSearchBox
                                name="subTypeId"
                                value={subTypeId}
                                isDisabled={isEditMode}
                                options={noteTypeOptions}
                                label="Note Type*"
                                className="NoteForm-SelectField"
                                errorText={errors.subTypeId}
                                onChange={onChangeField}
                            />
                        </Col>
                        <Col md={6}>
                            <TextField
                                type="text"
                                name="noteName"
                                value={noteName}
                                maxLength={256}
                                label="Note name"
                                className="NoteForm-TextField"
                                onChange={onChangeField}
                            />
                        </Col>
                    </Row>
                    <Row>
                        <Col>
                            <SelectField
                                hasTags
                                isMultiple
                                hasSearchBox
                                name="clients"
                                value={clients}
                                options={clientsOptions}
                                label="Clients*"
                                className="NoteForm-SelectField"
                                placeholder="Select Client"
                                isDisabled={isEditMode}
                                errorText={errors.clients}
                                onChange={onChangeField}
                            />
                        </Col>
                    </Row>
                    {isClientProgramTypeSelected && (
                        <>
                            <Row>
                                <Col md={6}>
                                    <SelectField
                                        hasSearchBox
                                        name="clientProgram.typeId"
                                        value={clientProgram.typeId}
                                        options={clientProgramNoteTypeOptions}
                                        label="Program sub type*"
                                        className="NoteForm-SelectField"
                                        errorText={errors.clientProgram?.typeId}
                                        onChange={onChangeField}
                                    />
                                </Col>
                                <Col md={6}>
                                    <TextField
                                        type="text"
                                        name="clientProgram.serviceProvider"
                                        value={clientProgram.serviceProvider}
                                        label="Service Provider for Program*"
                                        className="NoteForm-TextField"
                                        maxLength={256}
                                        errorText={errors.clientProgram?.serviceProvider}
                                        onChange={onChangeField}
                                    />
                                </Col>
                            </Row>
                            <Row>
                                <Col md={6}>
                                    <DateField
                                        type="text"
                                        name="clientProgram.startDate"
                                        value={clientProgram.startDate}
                                        label="Start Date of Program*"
                                        className="NoteForm-DateField"
                                        dateFormat="MM/dd/yyyy"
                                        maxTime={clientProgram.endDate}
                                        maxDate={clientProgram.endDate}
                                        errorText={errors.clientProgram?.startDate}
                                        onChange={onChangeDateField}
                                    />
                                </Col>
                                <Col md={6}>
                                    <DateField
                                        type="text"
                                        popperPlacement="bottom-end"
                                        name="clientProgram.endDate"
                                        value={clientProgram.endDate}
                                        label="End Date of Program*"
                                        className="NoteForm-DateField"
                                        dateFormat="MM/dd/yyyy"
                                        minTime={clientProgram.startDate}
                                        minDate={clientProgram.startDate}
                                        errorText={errors.clientProgram?.endDate}
                                        onChange={onChangeDateField}
                                    />
                                </Col>
                            </Row>
                        </>
                    )}


                    <EncounterSection
                        noteId={noteId}
                        noteTypeId={subTypeId}
                        state={encounter}
                        errors={errors.encounter}
                        onChangeField={onChangeEncounterField}
                        onChangeFields={onChangeEncounterFields}
                    />

                    <Row>
                        <Col md={12}>
                            <TextField
                                type="textarea"
                                name="subjective"
                                value={subjective}
                                label="Subjective*"
                                className="NoteForm-TextArea"
                                maxLength={20000}
                                errorText={errors.subjective}
                                onChange={onChangeField}
                            />
                        </Col>
                    </Row>
                    <Row>
                        <Col md={12}>
                            <TextField
                                type="textarea"
                                name="objective"
                                value={objective}
                                label="Objective"
                                maxLength={20000}
                                className="NoteForm-TextArea"
                                onChange={onChangeField}
                            />
                        </Col>
                    </Row>
                    <Row>
                        <Col md={12}>
                            <TextField
                                type="textarea"
                                name="assessment"
                                value={assessment}
                                label="Assessment"
                                maxLength={20000}
                                className="NoteForm-TextArea"
                                onChange={onChangeField}
                            />
                        </Col>
                    </Row>
                    <Row>
                        <Col md={12}>
                            <TextField
                                type="textarea"
                                name="plan"
                                value={plan}
                                label="Plan"
                                maxLength={20000}
                                className="NoteForm-TextArea"
                                onChange={onChangeField}
                            />
                        </Col>
                    </Row>
                </div>
            </Scrollable>

            <div className="NoteForm-Buttons">
                <Button outline color='success' onClick={onCancel}>Cancel</Button>
                <Button
                    color='success'
                    disabled={isFetching}
                >
                    {isEditMode ? 'Save' : 'Submit'}
                </Button>
            </div>
        </Form>
    )
}

export default compose(
    memo,
    connect(mapStateToProps, mapDispatchToProps),
)(GroupNoteForm)