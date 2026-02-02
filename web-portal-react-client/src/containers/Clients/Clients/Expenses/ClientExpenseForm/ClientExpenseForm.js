import React, {
	memo,
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
	ErrorViewer
} from 'components'

import {
	TextField,
	DateField,
	SelectField
} from 'components/Form'

import {
	ConfirmDialog
} from 'components/dialogs'

import {
	useForm,
	useScrollable,
	useScrollToFormError,
	useCustomFormFieldChange
} from 'hooks/common'

import {
	useClientExpenseTypesQuery
} from 'hooks/business/directory/query'

import {
	useClientExpenseSubmit,
	useClientExpenseTotalQuery,
} from 'hooks/business/client/expences'

import ClientExpense from 'entities/ClientExpense'
import ClientExpenseFormValidator from 'validators/ClientExpenseFormValidator'

import {
	map
} from 'lib/utils/ArrayUtils'

import './ClientExpenseForm.scss'

const BIG_EXPENSE_AMOUNT = 5000

const BIG_EXPENSE_WARNING_TEXT = `Total expense exceeds ${BIG_EXPENSE_AMOUNT}$. Please, confirm to proceed.`

const scrollableStyles = { flex: 1 }

function textValueMapper({ id, name, title }) {
	return { text: title, value: id ?? name }
}

function concertToCents(value = '0.00') {
	return Math.round(100 * Number(value))
}

function ClientExpenseForm(
	{
		clientId,
		onCancel,
		onSubmitSuccess
	}
) {
	const [error, setError] = useState(false)
	const [isFetching, setFetching] = useState(false)
	const [isValidationNeeded, setNeedValidation] = useState(false)
	const [isBigExpenseConfirmDialogOpen, toggleBigExpenseConfirmDialog] = useState(false)

	const {
		fields,
		errors,
		isValid,
		validate,
		isChanged,
		changeField
	} = useForm(
		'ClientExpenseForm',
		ClientExpense,
		ClientExpenseFormValidator
	)

	const {
		changeDateField,
		changeSelectField
	} = useCustomFormFieldChange(changeField)

	const { data: types } = useClientExpenseTypesQuery()

	const mappedTypes = useMemo(
		() => map(types, textValueMapper), [types]
	)

	const { Scrollable, scroll } = useScrollable()

	const scrollToError = useScrollToFormError('.ClientExpenseForm', scroll)

	const { data: total = 0 } = useClientExpenseTotalQuery(
		{ clientId }, { staleTime: 0 }
	)

	const { mutateAsync: submit } = useClientExpenseSubmit(
		{ clientId },
		{
			onError: setError,
			onSuccess: onSubmitSuccess
		}
	)

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

	const tryToSubmit = useCallback(() => {
		setFetching(true)

		validate()
			.then()
			.then(async () => {
				await submit({
					...fields.toJS(),
					cost: concertToCents(fields.cost),
					clientId
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
		clientId,
		validate,
		scrollToError
	])

	const onSubmit = useCallback(e => {
		e.preventDefault()

		if ((total / 100 + Number(fields.cost)) > BIG_EXPENSE_AMOUNT) {
			toggleBigExpenseConfirmDialog(true)
		} else tryToSubmit()
	}, [total, fields, tryToSubmit])

	const onConfirmBigExpense = useCallback(() => {
		tryToSubmit()
		toggleBigExpenseConfirmDialog(false)
	}, [tryToSubmit])

	useEffect(validateIf, [isValidationNeeded, validate])

	return (
		<>
			<Form className="ClientExpenseForm" onSubmit={onSubmit}>
				{isFetching && (
					<Loader style={{ position: 'fixed' }} hasBackdrop/>
				)}

				<Scrollable style={scrollableStyles} className="ClientExpenseForm-Sections">
					<div className="ClientExpenseForm-Section">
						<Row>
							<Col md={4}>
								<DateField
									hasTimeSelect
									name="date"
									value={fields.date}
									timeFormat="hh:mm aa"
									dateFormat="MM/dd/yyyy hh:mm a"
									label="Date of Expense*"
									onChange={changeDateField}
									errorText={errors.date}
									className="ClientExpenseForm-DateField"
								/>
							</Col>
							<Col md={4}>
								<SelectField
									type="text"
									name="typeName"
									value={fields.typeName}
									options={mappedTypes}
									label="Type of Expense*"
									placeholder="Select"
									errorText={errors.typeName}
									onChange={changeSelectField}
									className="ClientExpenseForm-SelectField"
								/>
							</Col>
							<Col md={4}>
								<TextField
									name="cost"
									type="text"
									value={fields.cost}
									mask="9{1,6}[.[9][9]]"
									label="Cost of Expense, $*"
									className="ClientExpenseForm-TextField"
									hasError={!!errors.cost}
									errorText={errors.cost}
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
									className="ClientExpenseForm-TextField"
									errorText={errors.comment}
									maxLength={256}
									onChange={changeField}
								/>
							</Col>
						</Row>
					</div>
				</Scrollable>

				<div className="ClientExpenseForm-Buttons">
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

			{isBigExpenseConfirmDialogOpen && (
				<ConfirmDialog
					isOpen
					title={BIG_EXPENSE_WARNING_TEXT}
					onConfirm={onConfirmBigExpense}
					onCancel={() => toggleBigExpenseConfirmDialog(false)}
				/>
			)}

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

export default memo(ClientExpenseForm)
