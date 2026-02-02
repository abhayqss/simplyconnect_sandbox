import React, { useEffect, useMemo } from 'react'

import { connect } from 'react-redux'
import { bindActionCreators } from 'redux'

import { useDirectoryData } from 'hooks/common'
import {
    useNoteTypesQuery,
    useEncounterTypesQuery
} from 'hooks/business/directory'

import { Button } from 'reactstrap'

import noteDetailsActions from 'redux/note/history/details/noteHistoryDetailsActions'

import Modal from 'components/Modal/Modal'
import Loader from 'components/Loader/Loader'
import Detail from 'components/business/common/Detail/Detail'

import { isEmpty, DateUtils as DU } from 'lib/utils/Utils'
import calcTotalTimeRangeUnits from '../calcTotalTimeRangeUnits'

import './NoteViewer.scss'

const { format, formats } = DU

const TIME_FORMAT = formats.time
const DATE_FORMAT = formats.americanMediumDate
const DATE_AND_TIME_FORMAT = formats.longDateMediumTime12

const getFullResourceName = o => [o.providerName, o.resourceName].filter(v => v).join(', ')

function mapStateToProps(state) {
    const { details } = state.note.history

    return {
        data: details.data || {},
        error: details.error,
        isFetching: details.isFetching,
        shouldReload: details.shouldReload,
    }
}

function mapDispatchToProps(dispatch) {
    return {
        actions: {
            details: bindActionCreators(noteDetailsActions, dispatch),
        }
    }
}

function NoteViewer({
    data,
    noteId,
    isOpen,
    onClose,
    actions,
    clientId,
    isFetching,
    organizationId
}) {
    const { noteTypes, encounterTypes } = useDirectoryData({
        noteTypes: ['note', 'type'],
        encounterTypes: ['note', 'encounter', 'type']
    })
    const selectedNoteType = useMemo(() => noteTypes.find(type => type.id === data.subTypeId), [data.subTypeId, noteTypes])
    const selectedEncounterType = useMemo(() => encounterTypes.find(type => type.id === data.encounter?.typeId), [data.encounter, encounterTypes])

    const { range, units, totalTime } = calcTotalTimeRangeUnits(data.encounter?.fromDate, data.encounter?.toDate)

    useNoteTypesQuery()
    useEncounterTypesQuery()

    useEffect(() => {
        actions.details.load({ noteId, clientId, organizationId })
    }, [noteId, clientId, organizationId, actions.details])

    let content = isFetching ? <Loader /> : <div>No data</div>

    if (!isEmpty(data)) {
        content = (
            <div className="NoteViewer">
                <div className="NoteViewer-Section">
                    <Detail title="PERSON SUBMITTING NOTE">
                        {data.author}
                    </Detail>
                    <Detail title="CLIENT">
                        {data.clientName}
                    </Detail>
                    <Detail title="CLIENTS">
                        {data.clients?.map(client => client.name).join(', ')}
                    </Detail>
                    <Detail title="TYPE">
                        {data.typeTitle}
                    </Detail>
                    <Detail title="SUBTYPE">
                        {selectedNoteType.title}
                    </Detail>
                    <Detail title="NOTE NAME">
                        {data.noteName}
                    </Detail>
                    <Detail title="ADMIT / INTAKE DATE">
                        {format(data.admitDate, DATE_AND_TIME_FORMAT)}
                    </Detail>
                    <Detail title="NOTE DATE AND TIME">
                        {format(data.noteDate, DATE_AND_TIME_FORMAT)}
                    </Detail>
                    {data.serviceStatusCheck && (
                            <>
                                <Detail title="SERVICE PLAN">
                                    {format(data.serviceStatusCheck.servicePlanCreatedDate, DATE_AND_TIME_FORMAT)}
                                </Detail>
                                <Detail title="RESOURCE NAME">
                                    {getFullResourceName(data.serviceStatusCheck)}
                                </Detail>
                                <Detail title="PERSON WHO DID THE AUDIT / CHECK">
                                    {data.serviceStatusCheck.auditPerson}
                                </Detail>
                                <Detail title="DATE OF CHECK">
                                    {format(data.serviceStatusCheck.checkDate, DATE_AND_TIME_FORMAT)}
                                </Detail>
                                <Detail title="NEXT DATE OF CHECK">
                                    {format(data.serviceStatusCheck.nextCheckDate, DATE_AND_TIME_FORMAT)}
                                </Detail>
                                <Detail title="IS THE SERVICE BEING PROVIDED?">
                                    {data.serviceStatusCheck.serviceProvided ? 'Yes' : 'No'}
                                </Detail>
                            </>
                        )
                    }
                    {data.encounter && (
                        <>
                            <Detail title="PERSON COMPLETING THE ENCOUNTER">
                                {data.encounter.clinicianTitle || data.encounter.otherClinician}
                            </Detail>
                            <Detail title="ENCOUNTER TYPE">
                                {selectedEncounterType?.label || data.encounter.typeTitle}
                            </Detail>
                            <Detail title="ENCOUNTER DATE FROM">
                                {format(data.encounter.fromDate, DATE_FORMAT)}
                            </Detail>
                            <Detail title="TIME FROM">
                                {format(data.encounter.fromDate, TIME_FORMAT)}
                            </Detail>
                            <Detail title="ENCOUNTER DATE TO">
                                {format(data.encounter.toDate, DATE_FORMAT)}
                            </Detail>
                            <Detail title="TIME TO">
                                {format(data.encounter.toDate, TIME_FORMAT)}
                            </Detail>
                            <Detail title="TOTAL SPENT TIME">
                                {totalTime}
                            </Detail>
                            <Detail title="RANGE">
                                {range}
                            </Detail>
                            <Detail title="UNITS">
                                {units}
                            </Detail>
                        </>
                    )}
                    <Detail title="SUBJECTIVE">
                        {data.subjective}
                    </Detail>
                    <Detail title="OBJECTIVE">
                        {data.objective}
                    </Detail>
                    <Detail title="ASSESSMENT">
                        {data.assessment}
                    </Detail>
                    <Detail title="PLAN">
                        {data.plan}
                    </Detail>
                </div>
            </div>
        )
    }

    return (
        <Modal
            isOpen={isOpen}
            onClose={onClose}
            hasCloseBtn={false}
            title={`View Note`}
            className="NoteViewer"
            renderFooter={() => (
                <Button color='success' onClick={onClose}>
                    Close
                </Button>
            )}>
            {content}
        </Modal>
    )
}

export default connect(mapStateToProps, mapDispatchToProps)(NoteViewer)