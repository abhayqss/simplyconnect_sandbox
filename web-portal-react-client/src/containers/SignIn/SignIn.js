import React, {
	memo,
	useEffect,
	useCallback
} from 'react'

import PTypes from 'prop-types'

import cn from 'classnames'

import { connect } from 'react-redux'
import { bindActionCreators } from 'redux'
import { useLocation } from 'react-router-dom'

import * as loginActions from 'redux/auth/login/loginActions'
import * as sessionActions from 'redux/auth/session/sessionActions'

import { primaryOrganizationStore } from 'lib/stores'

import {
	ACTION_TYPES
} from 'lib/Constants'

import config from 'config'

import {
	getQueryParams
} from 'lib/utils/UrlUtils'

import {
	Response
} from 'lib/utils/AjaxUtils'

import { SignInForm } from './'

import './SignIn.scss'

const {
	SIGN_IN_SUCCESS,
	SIGN_IN_FAILURE
} = ACTION_TYPES

function mapDispatchToProps(dispatch) {
	return {
		actions: {
			...bindActionCreators(loginActions, dispatch),
			session: bindActionCreators(sessionActions, dispatch)
		}
	}
}

function SignIn({ actions, className }) {
	const location = useLocation()

	const params = getQueryParams(location.search)

	const {
		companyId,
		organizationCode,
		shouldRedirect = true
	} = params

	const onSubmit = useCallback(data => {
		actions.login(data).then(Response(() => {
				if (window.parent) {
					primaryOrganizationStore.save({
						companyId,
						shouldRedirect,
						code: organizationCode,
						url: `${config.location.protocol}//${organizationCode}.${config.location.domain}`
					})

					window.parent.postMessage(
						JSON.stringify({ type: SIGN_IN_SUCCESS }), '*'
					)
				}
			}
		)).catch(e => {
			if (window.parent) {
				e.data = data
				actions.session.clearError()
				window.parent.postMessage(
					JSON.stringify({ type: SIGN_IN_FAILURE, data: e }), '*'
				)
			}
		})
	}, [
		actions,
		companyId,
		shouldRedirect,
		organizationCode
	])

	const onForgotPassword = useCallback(() => {
		if (window.parent) {
			window.parent.postMessage(
				JSON.stringify({ type: 'FORGOT_PASSWORD' }), '*'
			)
		}
	}, [])

	return (
		<div className={cn("SignIn", className)}>
			<SignInForm
				companyId={companyId}
				className="h-100"
				onSubmit={onSubmit}
				onForgotPassword={onForgotPassword}
			/>
		</div>
	)
}

SignIn.propTypes = {
	className: PTypes.string
}

export default memo(connect(null, mapDispatchToProps)(SignIn))