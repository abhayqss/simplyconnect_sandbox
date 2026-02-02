import React, {
    useMemo,
    useState,
    useEffect,
    useCallback
} from 'react'

import {
	Col,
	Row,
	Form,
	Button
} from 'reactstrap'

import {
    Loader,
	AlertPanel,
	ErrorViewer
} from 'components'

import {
	DateField
} from 'components/Form'

import {
	useForm,
	useScrollable,
	useScrollToFormError
} from 'hooks/common'

import { useMarkAsDoneSubmit } from 'hooks/business/inquiry'

import ContactToIndividual from 'entities/ContactToIndividual'
import ContactToIndividualFormValidator from 'validators/ContactToIndividualFormValidator'

import './ContactToIndividualConfirmForm.scss'

const scrollableStyles = { flex: 1 }

const ContactToIndividualConfirmForm = ({
    inquiryId,
    inquiryDate,
    onCancel,
    onSubmitSuccess
}) => {
    const [error, setError] = useState(false)
	const [isFetching, setFetching] = useState(false)
	const [isValidationNeeded, setNeedValidation] = useState(false)

    const {
		fields,
		errors,
		isValid,
		validate,
		isChanged,
		changeField
	} = useForm(
		'ContactToIndividualConfirmForm',
		ContactToIndividual,
		ContactToIndividualFormValidator
	)

    const { Scrollable, scroll } = useScrollable()

	const scrollToError = useScrollToFormError('.ContactToIndividualConfirmForm', scroll)

	const { mutateAsync: submit } = useMarkAsDoneSubmit({ inquiryId }, {
		onError: setError,
		onSuccess: onSubmitSuccess
	})

    const cancel = () => {
		onCancel(isChanged)
	}

    const validationOptions = useMemo(() => {
        return {
            included: {
                inquiryDate
            }
        }
    }, [
        inquiryDate
    ])

    const validateIf = () => {
		if (isValidationNeeded) {
			validate(validationOptions)
				.then(() => setNeedValidation(false))
				.catch(() => setNeedValidation(true))
		}
	}

    const tryToSubmit = useCallback(() => {
		setFetching(true)

		validate(validationOptions)
			.then()
			.then(async () => {
				await submit({
					...fields.toJS()
				})

				setNeedValidation(false)
			})
			.catch(() => {
				scrollToError()
				setNeedValidation(true)
			})
			.finally(() => {
				setFetching(false)
			})
	}, [
		fields,
		submit,
		validate,
		scrollToError,
        validationOptions
	])

	const changeDateField = useCallback((name, value) => {
		changeField(name, new Date(value).setHours(23, 59))
	}, [changeField])

    const onSubmit = useCallback(e => {
		e.preventDefault()

		tryToSubmit()
	}, [tryToSubmit])

	useEffect(validateIf, [
        validate,
        validationOptions,
        isValidationNeeded
    ])

    return (
        <>
            <Form className="ContactToIndividualConfirmForm" onSubmit={onSubmit}>
                {isFetching && (
					<Loader style={{ position: 'fixed' }} hasBackdrop/>
				)}
				<Scrollable style={scrollableStyles} className="ContactToIndividualConfirmForm-Sections">
					<div className="ContactToIndividualConfirmForm-Section">
						<Row>
							<Col md={6}>
								<DateField
									minDate={inquiryDate}
									placeholder="mm/dd/yyyy"
									name="contactedDate"
									value={fields.contactedDate}
									dateFormat="MM/dd/yyyy"
									label="Date of contact"
									onChange={changeDateField}
									errorText={errors.contactedDate}
									className="ContactToIndividualConfirmForm-DateField"
								/>
							</Col>
                        </Row>
						<Row>
							<Col md={12}>
								<AlertPanel>By clicking on the "Mark as done" button, you confirm that you have contacted the individual.</AlertPanel>
							</Col>
						</Row>
					</div>
				</Scrollable>

				<div className="ContactToIndividualConfirmForm-Buttons">
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

export default ContactToIndividualConfirmForm