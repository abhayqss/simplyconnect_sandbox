import React, {
	memo,
	useMemo,
	useState,
	useEffect,
	useContext,
	useCallback
} from 'react'

import {
	reject
} from 'underscore'

import {
	Col,
	Row,
	Form
} from 'reactstrap'

import {
	Loader,
	ErrorViewer
} from 'components'

import {
	SelectField
} from 'components/Form'

import {
	useForm,
	useScrollable,
	useScrollToFormError,
	useCustomFormFieldChange
} from 'hooks/common'

import {
	useOrganizationsQuery
} from 'hooks/business/directory/query'

import { ESignDocumentTemplateContext } from 'contexts'
import Entity from 'entities/e-sign/ESignDocumentTemplateOrganization'
import Validator from 'validators/ESignDocumentTemplateOrganizationFormValidator'

import { map } from 'lib/utils/ArrayUtils'

import './ESignDocumentTemplateOrganizationForm.scss'

function ESignDocumentTemplateOrganizationForm(
	{
		organizationId,

		children,

		onChanged,
		onSubmitSuccess,
		onCancel: onCancelCb
	}
) {
	const [error, setError] = useState(false)
	const [isFetching, setFetching] = useState(false)
	const [needValidation, setNeedValidation] = useState(false)
	const [isNextButtonDisabled, setNextButtonDisabled] = useState(true)

	const {
		fields,
		errors,
		isValid,
		validate,
		isChanged,
		changeField
	} = useForm('ESignDocumentTemplateOrganizationForm', Entity, Validator)

	const {
		changeSelectField
	} = useCustomFormFieldChange(changeField)

	const data = useMemo(() => fields.toJS(), [fields])

	useEffect(() => {
		setNextButtonDisabled(!data.organizationId)
	}, [data])

	const { Scrollable, scroll } = useScrollable()

	const {
		templateData
	} = useContext(ESignDocumentTemplateContext)

	const {
		data: organizations,
		isFetching: isFetchingOrganizations,
	} = useOrganizationsQuery(
		{ isESignEnabled: true }, { staleTime: 0 },
	)

	const mappedOrganizations = useMemo(() => map(
			reject(organizations, o => o.id === organizationId),
			o => ({ text: o.label, value: o.id }),
		), [organizations, organizationId],
	)

	function init() {
		changeSelectField('organizationId', templateData?.organizationId)
	}

	function validateIf() {
		if (needValidation) {
			validate({})
				.then(() => setNeedValidation(false))
				.catch(() => setNeedValidation(true))
		}
	}

	function tryToSubmit(e) {
		e.preventDefault();

		setFetching(true)

		validate({})
			.then(async () => {
				setFetching(false)
				setNeedValidation(false)

				try {
					onSubmitSuccess({ ...data })
				} catch (e) {
					setError(e)
				}
			})
			.catch(() => {
				scrollToError()
				setFetching(false)
				setNeedValidation(true)
			})
	}

	const scrollToError = useScrollToFormError(
		'.ESignDocumentTemplateOrganizationForm', scroll
	)

	const onCancel = useCallback(
		() => onCancelCb(isChanged),
		[onCancelCb, isChanged]
	)

	const onSubmit = useCallback(
		tryToSubmit,
		[
			data,
			validate,
			scrollToError
		]
	)

	useEffect(init, [templateData, changeSelectField])

	useEffect(validateIf, [
		validate,
		scrollToError,
		needValidation
	])

	useEffect(() => {
		onChanged(isChanged)
	}, [isChanged, onChanged])

	return (
		<>
			<Form className="ESignDocumentTemplateOrganizationForm" onSubmit={onSubmit}>
				{isFetching && (
					<Loader hasBackdrop />
				)}
				<Scrollable className="ESignDocumentTemplateOrganizationForm-Sections">
					<div className="ESignDocumentTemplateOrganizationForm-Section">
						<Row>
							<Col>
								<SelectField
									label="Organization*"
									name="organizationId"
									value={fields.organizationId}
									options={mappedOrganizations}
									errorText={errors.organizationId}

									hasTooltip
									hasKeyboardSearch
									hasKeyboardSearchText
									isDisabled={isFetchingOrganizations}
									placeholder="Select"

									onChange={changeSelectField}
								/>
							</Col>
						</Row>
					</div>
				</Scrollable>

				<div className="ESignDocumentTemplateOrganizationForm-Footer">
					{children?.({
						cancel: onCancel,
						isValidToSubmit: !isNextButtonDisabled && isValid
					})}
				</div>
			</Form>
			{error && (
				<ErrorViewer
					isOpen
					error={error}
					onClose={() => setError(null)}
				/>
			)}</>
	)
}


export default memo(ESignDocumentTemplateOrganizationForm)
