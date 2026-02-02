import React, {
	memo,
	useEffect,
	useCallback
} from 'react'

import PTypes from 'prop-types'

import cn from 'classnames'

import DocumentTitle from 'react-document-title'

import {
	useSelector
} from 'react-redux'

import {
	useHistory
} from 'react-router-dom'

import { Row, Col } from 'reactstrap'

import {
	Logo,
	Loader
} from 'components'

import {
	useLocationState
} from 'hooks/common'

import {
	useBoundActions
} from 'hooks/common/redux'

import {
	useExternalProviderUrlCheck
} from 'hooks/business/external'

import * as oldPasswordFormActions from 'redux/auth/password/old/form/oldPasswordFormActions'

import OldPasswordForm from 'containers/Login/OldPasswordForm/OldPasswordForm'

import './OldPassword.scss'

function OldPassword({ className }) {
	const [{
		companyId, username
	}] = useLocationState() ?? {}

	const { clearError } = useBoundActions(oldPasswordFormActions)
	const error = useSelector(state => state.auth.password.old.form.error)
	const isFetching = useSelector(state => state.auth.password.old.form.isFetching)

	const history = useHistory()
	const isExternalProviderUrl = useExternalProviderUrlCheck()

	const onBack = useCallback(() => {
		history.goBack()
	}, [history])

	return (
		<DocumentTitle title="Simply Connect | Expired Password">
			<div className={cn("OldPassword", className)}>
				{isFetching && (
					<Loader hasBackdrop/>
				)}

				<div className="OldPassword-Body">
					<Logo iconSize={55} className="OldPassword-Logo" />
					<Row>
						<Col md={{ size: 4, offset: 4 }}>
							<div className="flex-1 d-flex flex-column align-items-center">
								<div className="flex-1">
									<div className="OldPassword-Title">
										Create New Password
									</div>
									<div className="d-flex flex-column">
										<div className="OldPassword-InfoText">
											Your password for {companyId}, {username} has expired and must be changed
										</div>
										<OldPasswordForm
											username={username}
											companyId={companyId}
											onCancel={onBack}
											onSubmitSuccess={onBack}
											isExternalProvider={isExternalProviderUrl}
										/>
										{error && (
											<div className="OldPassword-Alert">
												{error.message}
											</div>
										)}
									</div>
								</div>
							</div>
						</Col>
					</Row>
				</div>
			</div>
		</DocumentTitle>

	)
}

OldPassword.propTypes = {
	className: PTypes.string
}

export default memo(OldPassword)