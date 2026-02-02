import React, {
	memo,
	useMemo,
	useState,
	useEffect
} from 'react'

import {
	Col,
	Row,
	Form,
	Button
} from 'reactstrap'

import {
	Loader,
	ErrorViewer
} from 'components'

import {
	TextField,
	DateField
} from 'components/Form'

import {
	useForm,
	useScrollable,
	useScrollToFormError
} from 'hooks/common'

import {
	useClientActivation
} from 'hooks/business/client'

import {
	useClientQuery
} from 'hooks/business/client/queries'

import ClientActivation from 'entities/ClientActivation'
import ClientActivationFormValidator from 'validators/ClientActivationFormValidator'

import './ClientActivationForm.scss'

const TODAY = Date.now()

const scrollableStyles = { flex: 1 }

function ClientActivationForm(
	{
		onCancel,
		onSubmitSuccess,
		clientId
	}
) {
	const [error, setError] = useState(false)
	const [isFetching, setFetching] = useState(false)
	const [isValidationNeeded, setNeedValidation] = useState(false)

	const {
		fields,
		errors,
		isValid,
		validate,
		isChanged,
		changeField,
		changeDateField
	} = useForm(
		'ClientActivationForm',
		ClientActivation,
		ClientActivationFormValidator
	)

	const data = useMemo(() => fields.toJS(), [fields])

	const { data: client } = useClientQuery({ clientId })

	const { Scrollable, scroll } = useScrollable()

	const scrollToError = useScrollToFormError('.ClientActivationForm', scroll)

	const { mutateAsync: activate } = useClientActivation({
		...data,
		clientId,
		programType: client?.communityId
	}, {
		onError: setError,
		onSuccess: (data) => {
			onSubmitSuccess(data)
		}
	})

	function cancel() {
		onCancel(isChanged)
	}

	function validateIf() {
		if (isValidationNeeded) {
			validate()
				.then(() => setNeedValidation(false))
				.catch(() => setNeedValidation(true))
		}
	}

	function tryToSubmit(e) {
		e.preventDefault()

		setFetching(true)

		validate()
			.then()
			.then(async () => {
				await activate()
				setNeedValidation(false)
			})
			.catch(() => {
				scrollToError()
				setNeedValidation(true)
			})
			.finally(() => {
				setFetching(false)
			})
	}

    function init() {
        if (client) {
            changeField("programType", client.community, true)
        }
    }

	useEffect(init, [client])

	useEffect(validateIf, [isValidationNeeded, scrollToError, validate])

	return (
		<>
			<Form className="ClientActivationForm" onSubmit={tryToSubmit}>
				{(isFetching || !fields.programType) && (
					<Loader style={{ position: 'fixed' }} hasBackdrop/>
				)}

				<Scrollable style={scrollableStyles} className="ClientActivationForm-Sections">
					<div className="ClientActivationForm-Section">
						<Row>
							<Col md={4}>
								<DateField
									name="intakeDate"
									value={fields.intakeDate}
									dateFormat="MM/dd/yyyy"
									maxDate={TODAY}
									label="Intake date*"
									placeholder="Select date"
									onChange={changeDateField}
									errorText={errors.intakeDate}
									className="ClientActivationForm-DateField"
								/>
							</Col>
							<Col md={4}>
								<TextField
									type="text"
									name="fields.programType"
									value={fields.programType}
									label="Program Type"
									className="ClientActivationForm-TextField"
									isDisabled
									hasError={errors.programType}
									errorText={errors.programType}
									onChange={changeField}
								/>
							</Col>
						</Row>

						<Row>
							<Col md={12}>
								<TextField
									type="textarea"
									name="comment"
									value={fields.comment}
									label="Comment"
									numberOfRows={10}
									className="ClientActivationForm-TextField"
									errorText={errors.comment}
									maxLength={5000}
									onChange={changeField}
								/>
							</Col>
						</Row>
					</div>
				</Scrollable>

				<div className="ClientActivationForm-Buttons">
					<Button
						outline
						color="success"
						onClick={cancel}
					>
						Close
					</Button>

					<Button
						color="success"
						disabled={isFetching || !isValid}
					>
						Save
					</Button>
				</div>
			</Form>

			{error && (
				<ErrorViewer
					isOpen
					error={error}
					onClose={() => setError(null)}
				/>
			)}
		</>
	)
}

export default memo(ClientActivationForm)
