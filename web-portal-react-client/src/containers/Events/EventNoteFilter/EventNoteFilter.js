import React, {
	useMemo,
	useCallback,
} from 'react'

import {
	map,
	reject
} from 'underscore'

import {
	add
} from 'date-arithmetic'

import { connect } from 'react-redux'
import { bindActionCreators } from 'redux'

import { Row, Col, Button } from 'reactstrap'

import {
	useMemoEffect,
	useDirectoryData
} from 'hooks/common'

import {
	useClientsQuery,
	useNoteTypesQuery,
	useEventTypesQuery
} from 'hooks/business/directory'

import { useFilter } from 'hooks/common/filter'
import { useCustomFilter } from 'hooks/common/redux'

import DateField from 'components/Form/DateField/DateField'
import SelectField from 'components/Form/SelectField/SelectField'
import CheckboxField from 'components/Form/CheckboxField/CheckboxField'

import eventNoteComposedListActions from 'redux/event/note/composed/list/eventNoteComposedListActions'

import {
	isInteger,
	DateUtils as DU
} from 'lib/utils/Utils'

import {
	getEndOfDayTime,
	getStartOfDayTime
} from 'lib/utils/DateUtils'

import { EVENT_GROUP_COLORS } from 'lib/Constants'

import { NAME as PRIMARY_FILTER_NAME } from '../EventNotePrimaryFilter/EventNotePrimaryFilter'

import './EventNoteFilter.scss'

export const NAME = 'EVENT_NOTE_FILTER'

const DEFAULT_DATA = {
	clientId: null,
	eventTypeId: null,
	noteTypeId: null,
	onlyEventsWithIR: false
}

function threeMonthsAgo() {
	return add(new Date(), -3, 'month')
}

function valueTextMapper({ id, title, fullName }) {
	return { value: id, text: title || fullName }
}

function getEventSelectSectionColor(section) {
	return EVENT_GROUP_COLORS[section.name]
}

function mapStateToProps(state) {
	const { list } = state.event.note.composed

	return {
		fields: list.getFilter(),
		isFetching: list.isFetching,
		isChanged: list.isFilterChanged()
	}
}

function mapDispatchToProps(dispatch) {
	return {
		actions: bindActionCreators(eventNoteComposedListActions, dispatch)
	}
}

function EventNoteFilter(
	{
		fields,
		actions,
		clientId,
		isChanged,
		areNotesExcluded = false
	}
) {
	const {
		communityIds,
		organizationId
	} = fields

	const isClient = isInteger(clientId)

	const name = (isClient ? 'CLIENT_' : '') + NAME

	const {
		clients,
		noteTypes,
		eventTypes
	} = useDirectoryData({
		clients: ['client'],
		noteTypes: ['note', 'type'],
		eventTypes: ['event', 'type']
	})

	const {
		save: savePrimary
	} = useFilter(PRIMARY_FILTER_NAME)

	const {
		reset,
		apply,
		changeField
	} = useCustomFilter(name, fields, actions, {
		isChanged,
		onApplied: () => {
			if (!isClient) {
				savePrimary({ organizationId, communityIds })
			}
		},
		defaultData: {
			...DEFAULT_DATA,
			fromDate: getStartOfDayTime(threeMonthsAgo()),
			toDate: getEndOfDayTime(Date.now())
		}
	})

	const mappedClients = useMemo(
		() => map(clients, valueTextMapper), [clients]
	)

	const mappedNoteTypes = useMemo(
		() => map(noteTypes, valueTextMapper), [noteTypes]
	)

	const mappedEventTypes = useMemo(
		() => map(
			reject(
				eventTypes,
				o => o?.isService
			),
			group => ({
				id: group?.id,
				name: group?.name,
				title: group?.title,
				options: map(
					reject(group?.eventTypes, o => o?.isService),
					o => ({ text: o?.title, value: o?.id })
				)
			})
		), [eventTypes]
	)

	useClientsQuery(
		{ organizationId, communityIds },
		{
			condition: prevParams => (
				!isClient && isInteger(organizationId) && (
					communityIds !== prevParams.communityIds
				)
			)
		}
	)

	useEventTypesQuery()
	useNoteTypesQuery()

	const onChangeField = useCallback((name, value) => {
		changeField(name, value, false)
	}, [changeField])

	const onChangeDateField = useCallback((name, value) => {
		const f = name === 'fromDate' ? 'startOf' : 'endOf'

		changeField(name, value ? DU[f](value, 'day').getTime() : null, false)
	}, [changeField])

	useMemoEffect(m => {
		if (organizationId !== m()?.organizationId) {
			changeField('fromDate', getStartOfDayTime(threeMonthsAgo()), false, true)
			changeField('toDate', getEndOfDayTime(Date.now()), false, true)
		}

		m({ organizationId })
	}, [changeField, organizationId])

	return (
		<div className="EventNoteFilter">
			<Row>
				<Col lg={12} md={12}>
					<Row>
						{!isClient && (
							<Col>
								<SelectField
									label="Client Name"
									name="clientId"
									hasKeyboardSearch
									hasKeyboardSearchText
									value={fields.clientId}
									placeholder="Select Client"
									options={mappedClients}
									onChange={onChangeField}
								/>
							</Col>
						)}
						<Col>
							<SelectField
								isSectioned
								label="Event Type"
								name="eventTypeId"
								placeholder="Select Event Type"
								value={fields.eventTypeId}

								optionType="tick"
								hasSectionSeparator
								hasSectionIndicator
								sectionIndicatorColor={getEventSelectSectionColor}

								sections={mappedEventTypes}

								onChange={onChangeField}
							/>
						</Col>
						{!areNotesExcluded && (
							<Col>
								<SelectField
									label="Note Type"
									name="noteTypeId"
									hasKeyboardSearch
									hasKeyboardSearchText
									value={fields.noteTypeId}
									placeholder="Select Note Type"
									options={mappedNoteTypes}
									onChange={onChangeField}
								/>
							</Col>
						)}
					</Row>
				</Col>
				<Col lg={12} md={12}>
					<Row>
						<Col>
							<DateField
								name="fromDate"
								label="Date From"
								value={fields.fromDate}
								maxDate={fields.toDate}
								onChange={onChangeDateField}
							/>
						</Col>
						<Col>
							<DateField
								name="toDate"
								label="Date To"
								value={fields.toDate}
								minDate={fields.fromDate}
								onChange={onChangeDateField}
							/>
						</Col>
					</Row>
				</Col>
			</Row>
			<Row>
				{!areNotesExcluded && (
					<Col xs={12} className="col-md-auto">
						<CheckboxField
							name="onlyEventsWithIR"
							className="margin-bottom-20"
							value={fields.onlyEventsWithIR}
							label="Only events with incident report"
							onChange={onChangeField}
						/>
					</Col>
				)}
				<Col xs={12} className="col-md-auto">
					<Button
						data-testid="clear-btn"

						outline
						color="success"
						onClick={reset}
						className="EventNoteFilter-Btn"
					>
						Clear
					</Button>
					<Button
						data-testid="apply-btn"

						color="success"
						onClick={apply}
						className="EventNoteFilter-Btn"
					>
						Apply
					</Button>
				</Col>
			</Row>
		</div>
	)
}

export default connect(mapStateToProps, mapDispatchToProps)(EventNoteFilter)