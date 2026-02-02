import React, {
	memo,
	useMemo,
	useState,
	useEffect,
	useCallback
} from 'react'

import {
	findWhere
} from 'underscore'

import {
	Form,
	Button
} from 'reactstrap'

import {
	Loader,
	AlertPanel,
	ErrorViewer
} from 'components'

import {
	ActionBar,
	RadioGroupField
} from 'components/Form'

import {
	ConfirmDialog
} from 'components/dialogs'

import HIEConsentPolicy from 'entities/HIEConsentPolicy'

import HIEConsentPolicyFormValidator from 'validators/HIEConsentPolicyFormValidator'

import {
	useForm,
	useScrollable,
	useScrollToFormError
} from 'hooks/common'

import {
	useAuthUser
} from 'hooks/common/redux'

import {
	useHIEConsentPolicySubmit
} from 'hooks/business/policies/hie'

import {
	SYSTEM_ROLES
} from 'lib/Constants'

import { ReactComponent as Warning } from 'images/alert-yellow.svg'

import {
	HIE_CONSENT_POLICIES
} from 'lib/Constants'

import {
	HIE_DEFINITION_TEXT
} from '../lib/Constants'

import './HIEConsentPolicyForm.scss'

const {
	PARENT_GUARDIAN,
	PERSON_RECEIVING_SERVICES
} = SYSTEM_ROLES

const CONSENT_POLICIES = [
	{
		name: HIE_CONSENT_POLICIES.OPT_OUT,
		description: (
			<>
				<p className="mb-0">
					<strong>OPT-OUT for all health information: </strong>
					I DO NOT want any of my information visible within the HIEs in which Simply Connect participates.
				</p>
				<ul className="mb-0">
					<li>
						I understand that the applicable health information received by any Simply Connect provider WILL NOT 
						BE VISIBLE in the HIEs in which Simply Connect participates. THIS INCLUDES EMERGENCY SITUATIONS.
					</li>
					<li>
						I understand that I am free to revoke this Opt-Out request at any time and can do so by completing a 
						new Opt-In/Opt-Out form.
					</li>
					<li>
						I understand that this request only applies to sharing my health information with HIEs and that a 
						health care provider may request and receive my medical information from other providers using other 
						methods permitted by law. If you have previously opted out of participating in HIEs and want to reverse 
						that decision, check the box below. Your health information from the period during which you had 
						opted-out may be available through the HIEs after you decide to opt back in.
					</li>
				</ul>
			</>
		)
	},
	{
		name: HIE_CONSENT_POLICIES.OPT_IN,
		description: (
			<>
				<b>OPT-IN/Cancel OPT-OUT:</b> I WANT my information visible in the HIEs in which Simply Connect
				participates.
			</>
		)
	}
]

function HIEConsentPolicyForm({ onSubmitSuccess }) {
	const [error, setError] = useState(false)
	const [isFetching, setFetching] = useState(false)
	const [isValidationNeeded, setNeedValidation] = useState(false)
	const [isPrimaryContactDeletionConfirmDialogOpen, togglePrimaryContactDeletionConfirmDialog] = useState(false)

	const {
		fields,
		errors,
		isValid,
		validate,
		changeField
	} = useForm(
		'HIEConsentPolicyForm',
		HIEConsentPolicy,
		HIEConsentPolicyFormValidator
	)

	const user = useAuthUser()

	const associatedClient = findWhere(user?.associatedClients, { shouldConfirmHieConsentPolicy: true })

	const primaryContact = associatedClient?.primaryContact

	const { Scrollable, scroll } = useScrollable()

	const scrollToError = useScrollToFormError('.HIEConsentPolicyForm', scroll)

	const { mutateAsync: submit } = useHIEConsentPolicySubmit({
		onError: setError,
		onSuccess: (data) => {
			onSubmitSuccess(data)
		}
	})

	const mappedConsentPolicies = useMemo(() => CONSENT_POLICIES.map(o => (
		{ value: o.name, label: o.description }
	)), [])

	function setDefaultData() {
		if (associatedClient) {
			changeField('value', associatedClient.hieConsentPolicyName)
		}
	}

	const validateIf = useCallback(() => {
		if (isValidationNeeded) {
			validate()
				.then(() => setNeedValidation(false))
				.catch(() => setNeedValidation(true))
		}
	}, [validate, isValidationNeeded])

	const tryToSubmit = useCallback((e = null) => {
		e && e.preventDefault()

		setFetching(true)

		validate()
			.then()
			.then(async () => {
				await submit({
					clientId: associatedClient?.id,
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
		associatedClient
	])

	const onSubmit = useCallback(e => {
		if (fields.value === HIE_CONSENT_POLICIES.OPT_OUT
			&& primaryContact?.typeName === 'CARE_TEAM_MEMBER'
			&& [
				PARENT_GUARDIAN,
				PERSON_RECEIVING_SERVICES
			].includes(primaryContact?.roleName)) {
			e.preventDefault()
			togglePrimaryContactDeletionConfirmDialog(true)
		} else tryToSubmit(e)
	}, [
		fields,
		tryToSubmit,
		primaryContact
	])

	const onConfirmSubmit = useCallback(() => {
		tryToSubmit()
		togglePrimaryContactDeletionConfirmDialog(false)
	}, [tryToSubmit])

	useEffect(setDefaultData, [user, changeField])
	useEffect(validateIf, [isValidationNeeded, scrollToError, validate])

	return (
		<Form className="HIEConsentPolicyForm" onSubmit={tryToSubmit}>
			{isFetching && (
				<Loader style={{ position: 'fixed' }} hasBackdrop/>
			)}

			<Scrollable style={{ flex: 1 }}>
				<div className="HIEConsentPolicyForm-Section">
					<AlertPanel className="HIEConsentPolicyForm-SectionDefinition">
						{HIE_DEFINITION_TEXT}
						{'\n'}
						COMMUNITY NAME: {associatedClient?.communityName}
					</AlertPanel>

					<RadioGroupField
						view="col"
						name="value"
						className="HIEConsentPolicyForm-RadioGroupField"
						selected={fields.value}
						hasError={!!errors.value}
						errorText={errors.value}
						options={mappedConsentPolicies}
						onChange={changeField}
					/>
				</div>
			</Scrollable>

			<ActionBar className="HIEConsentPolicyForm-Actions">
				<Button
					color="success"
					onClick={onSubmit}
					disabled={isFetching || !isValid}
				>
					Save
				</Button>
			</ActionBar>

			{isPrimaryContactDeletionConfirmDialogOpen && (
				<ConfirmDialog
					isOpen
					icon={Warning}
					confirmBtnText="Confirm"
					title={`As a result of your Opt Out selection, ${primaryContact.firstName} ${primaryContact.lastName} will no 
					longer be a primary contact on your care team and no longer have associated capabilities`}
					onConfirm={onConfirmSubmit}
					onCancel={() => togglePrimaryContactDeletionConfirmDialog(false)}
				/>
			)}

			{error && (
				<ErrorViewer
					isOpen
					error={error}
					onClose={() => setError(null)}
				/>
			)}
		</Form>
	)
}

export default memo(HIEConsentPolicyForm)