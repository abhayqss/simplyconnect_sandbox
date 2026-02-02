import React, {
	memo,
	useState,
	useEffect,
	useCallback
} from 'react'

import PTypes from 'prop-types'

import cn from 'classnames'

import {
	Form
} from 'reactstrap'

import {
	Loader
} from 'components'

import {
	TextField
} from 'components/Form'

import {
	Button
} from 'components/buttons'

import SignIn from 'entities/SignIn'
import SignInFormValidator from 'validators/SignInFormValidator'

import {
	useForm
} from 'hooks/common'

import config from 'config'

import {
	noop
} from 'lib/utils/FuncUtils'

import './SignInForm.scss'

function SignInForm(
	{
		companyId,
		className,
		onSubmit,
		onForgotPassword
	}
) {
	const [isFetching, setFetching] = useState(false)
	const [isValidationNeeded, setNeedValidation] = useState(false)

	const {
		fields,
		errors,
		validate,
		changeField
	} = useForm('SignInForm', SignIn, SignInFormValidator)

	function validateIf() {
		if (isValidationNeeded) {
			validate()
				.then(() => setNeedValidation(false))
				.catch(() => setNeedValidation(true))
		}
	}

	function setDefaultData() {
		changeField('companyId', companyId)
	}

	const tryToSubmit = useCallback(e => {
		e.preventDefault()

		setFetching(true)

		validate()
			.then(async () => {
				setNeedValidation(false)
				onSubmit(fields.toJS())
			})
			.catch(() => {
				setNeedValidation(true)
			})
			.finally(() => {
				setFetching(false)
			})
	}, [
		fields,
		validate,
		onSubmit
	])

	useEffect(setDefaultData, [companyId, changeField])
	useEffect(validateIf, [isValidationNeeded, validate])

	return (
		<Form className={cn("SignInForm", className)}>
			{isFetching && (
				<Loader hasBackdrop style={{ position: 'fixed' }}/>
			)}

			<div className="SignInForm-Section">
				<TextField
					type="text"
					name="username"
					value={fields.username}
					errorText={errors.username}
					className="SignInForm-TextField"
					label="Login"
					onChange={changeField}
				/>
				<TextField
					type="password"
					name="password"
					errorText={errors.password}
					className="SignInForm-TextField"
					label="Password"
					onChange={changeField}
				/>
			</div>

			<div className="SignInForm-Buttons">
				<div
					onClick={onForgotPassword}
					className="SignInForm-ForgetPasswordLink"
				>
					Forgot Password?
				</div>
				<Button
					color="success"
					className="LoginForm-LoginBtn"
					onClick={tryToSubmit}
				>
					Sign in
				</Button>
			</div>
		</Form>
	)
}

SignInForm.propTypes = {
	companyId: PTypes.string,
	className: PTypes.string,
	onSubmit: PTypes.func,
	onForgotPassword: PTypes.func
}

SignInForm.defaultProps = {
	onSubmit: noop,
	onForgotPassword: noop
}

export default memo(SignInForm)