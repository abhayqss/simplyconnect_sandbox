import React, {
	memo,
	useMemo,
	useState,
	useEffect,
	useCallback
} from 'react'

import PTypes from 'prop-types'

import cn from 'classnames'

import {
	Row,
	Col
} from 'reactstrap'

import {
	Button
} from 'components/buttons'

import {
	DateField,
	SelectField
} from 'components/Form'

import {
	useCustomFormFieldChange
} from 'hooks/common'

import {
	useProspectEventFilter,
	useProspectEventFilterDirectory,
	useProspectEventFilterInitialization,
	useProspectEventFilterDefaultDataCache
} from 'hooks/business/Prospects/Events'

import Validator from 'validators/ProspectEventFilterValidator'

import {
	EVENT_GROUP_COLORS
} from 'lib/Constants'

import {
	map
} from 'lib/utils/ArrayUtils'

import {
	noop
} from 'lib/utils/FuncUtils'

import './EventFilter.scss'



function valueTextMapper({ id, name, title }) {
	return { value: id ?? name, text: title }
}

function getEventSelectSectionColor(section) {
	return EVENT_GROUP_COLORS[section.name]
}

function ProspectEventFilter(
	{
		prospectId,
		onChange,
		onApply,
		onReset,
		className
	}
) {
	const [shouldValidate, setShouldValidate] = useState(false)

	const {
		get: getDefaultData,
		update: updateDefaultData
	} = useProspectEventFilterDefaultDataCache({ prospectId })

	const {
		data,
		reset,
		apply,
		errors,
		isSaved,
		validate,
		changeField,
		changeFields
	} = useProspectEventFilter({
		getDefaultData,
		Validator,
		onApply,
		onReset
	})

	const {
		changeDateField
	} = useCustomFormFieldChange(changeField)

	const {
		types
	} = useProspectEventFilterDirectory({ prospectId })

	useProspectEventFilterInitialization({
		isSaved,
		prospectId,
		changeFields,
		updateDefaultData
	})

	const mappedTypes = useMemo(
		() => map(types, valueTextMapper), [types]
	)

	function validateIf() {
		if (shouldValidate) {
			validate()
				.then(() => setShouldValidate(false))
				.catch(() => setShouldValidate(true))
		}
	}

	function applyIfValid() {
		validate()
			.then(apply)
			.catch(() => setShouldValidate(true))
	}

	useEffect(validateIf, [validate, shouldValidate])

	return (
		<div className={cn("ProspectEventFilter", className)}>
			<Row>
				<Col lg={6}>
					<SelectField
						isSectioned
						optionType="tick"

						label="Event Type"
						name="eventTypeId"
						value={data.eventTypeId}
						placeholder="Select Event Type"

						hasSectionSeparator
						hasSectionIndicator
						sections={mappedTypes}
						sectionIndicatorColor={getEventSelectSectionColor}

						className="ProspectEventFilter-Field"

						onChange={changeField}
					/>
				</Col>
				<Col lg={4}>
					<DateField
						name="fromDate"
						label="Date From"
						value={data.fromDate}
						maxDate={data.toDate}
						errorText={errors.fromDate}
						onChange={changeDateField}
						className="ProspectEventFilter-Field"
					/>
				</Col>
				<Col lg={4}>
					<DateField
						name="toDate"
						label="Date To"
						value={data.toDate}
						minDate={data.fromDate}
						errorText={errors.toDate}
						onChange={changeDateField}
						className="ProspectEventFilter-Field"
					/>
				</Col>
				<Col lg={3} md={12}>
					<div className="ClientDocumentFilter-Buttons">
						<Button
							outline
							color='success'
							className="ProspectEventFilter-Btn"
							onClick={reset}
						>
							Clear
						</Button>
						<Button
							color='success'
							onClick={applyIfValid}
							className="ProspectEventFilter-Btn"
						>
							Apply
						</Button>
					</div>
				</Col>
			</Row>
		</div>
	)
}

ProspectEventFilter.propTypes = {
	className: PTypes.string,
	prospectId: PTypes.number,
	onChange: PTypes.func,
	onApply: PTypes.func,
	onReset: PTypes.func
}

ProspectEventFilter.defaultProps = {
	onChange: noop,
	onApply: noop,
	onReset: noop
}

export default memo(ProspectEventFilter)