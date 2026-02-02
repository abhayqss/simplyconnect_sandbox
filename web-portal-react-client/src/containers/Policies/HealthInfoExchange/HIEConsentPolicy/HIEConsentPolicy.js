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
	useAuthUser,
	useAuthUserFetch
} from 'hooks/common/redux'

import {
	SYSTEM_ROLES
} from 'lib/Constants'

import HIEConsentPolicyEditor from '../HIEConsentPolicyEditor/HIEConsentPolicyEditor'

const { PERSON_RECEIVING_SERVICES } = SYSTEM_ROLES

function HIEConsentPolicy() {
	const [isEditorOpen, toggleEditor] = useState(false)

	const user = useAuthUser()

	const {
		fetch: updateAuthUser
	} = useAuthUserFetch()

	const unconfirmedAssociatedClient = useMemo(() => {
		return findWhere(
			user?.associatedClients,
			{ shouldConfirmHieConsentPolicy: true }
		)
	}, [user?.associatedClients])

	useEffect(() => {
		if (user?.roleName === PERSON_RECEIVING_SERVICES && !!unconfirmedAssociatedClient)
			toggleEditor(true)
	}, [
		user,
		unconfirmedAssociatedClient
	])

	const closeEditor = useCallback(() => {
		if (!unconfirmedAssociatedClient?.id) toggleEditor(false)
	}, [unconfirmedAssociatedClient])

	const onSaveSuccess = useCallback(() => {
		updateAuthUser()
	}, [updateAuthUser])

	return isEditorOpen && (
		<HIEConsentPolicyEditor
			isOpen
			onClose={closeEditor}
			onSaveSuccess={onSaveSuccess}
		/>
	)
}

export default memo(HIEConsentPolicy)