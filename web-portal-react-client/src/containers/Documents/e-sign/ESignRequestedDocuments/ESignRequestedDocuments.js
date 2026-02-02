import React, {
	memo,
	useMemo,
	useState,
	useEffect,
	useCallback
} from 'react'

import DocumentTitle from 'react-document-title'

import {
	Badge,
} from 'reactstrap'

import {
	Logo,
	Footer,
	ErrorViewer
} from 'components'

import {
	SuccessDialog,
	WarningDialog
} from 'components/dialogs'

import {
	useQueryParams
} from 'hooks/common'

import {
	useESignRequestedDocumentsQuery
} from 'hooks/business/documents/e-sign'

import {
	isNotEmpty
} from 'lib/utils/Utils'

import {
	E_SIGN_STATUSES,
	SERVER_ERROR_CODES,
	E_SIGN_BULK_STATUSES
} from 'lib/Constants'

import { ReactComponent as Close } from 'images/delete.svg'

import ESignRequestedDocumentList from './ESignRequestedDocumentList/ESignRequestedDocumentList'

import './ESignRequestedDocuments.scss'

const {
	SENT
} = E_SIGN_STATUSES

const {
	SIGNED,
	EXPIRED,
	REVIEWED,
	CANCELED,
	SIGNATURE_FAILED
} = E_SIGN_BULK_STATUSES

function isIgnoredError(e = {}) {
	return e.code === SERVER_ERROR_CODES.ACCOUNT_INACTIVE
}

function ESignRequestedDocuments() {
	const [selected, setSelected] = useState(null)

	const [isErrorViewerOpen, toggleErrorViewer] = useState(false)

	const [isReadyToSignDialogOpen, toggleReadyToSignDialog] = useState(false)
	const [areAllAlreadySignedDialogOpen, toggleAllAlreadySignedDialog] = useState(false)
	const [isNoDocumentsToSignDialogOpen, toggleNoDocumentsToSignDialog] = useState(false)

	const { token } = useQueryParams()

	const {
		error,
		isFetching,
		data: {
			clientName,
			documents: data = []
		} = {}
	} = useESignRequestedDocumentsQuery(
		{ token }, { enabled: isNotEmpty(token) }
	)

	const areAllSigned = useMemo(() => (
			isNotEmpty(data) && data.every(o => (
				[SIGNED, REVIEWED].includes(o.signature.statusTitle)
			))
		), [data]
	)

	const areAllExpiredOrCancelledOrFailed = useMemo(() => (
		isNotEmpty(data) && data.every(o => (
			[EXPIRED, CANCELED, SIGNATURE_FAILED].includes(o.signature.statusTitle)
		))
	), [data])

	const openPdcFlow = useCallback(pdcFlowLink => {
		window.open(pdcFlowLink)
	}, [])

	const sign = useCallback(o => {
		openPdcFlow(o.signature.pdcFlowLink)
	}, [openPdcFlow])

	const signSelected = useCallback(() => {
		sign(selected)
	}, [sign, selected])

	const onSign = useCallback(o => {
		if (o.signature.hasPin) {
			setSelected(o)
			toggleReadyToSignDialog(true)
		} else sign(o)
	}, [sign])

	useEffect(() => {
		if (areAllSigned) {
			toggleAllAlreadySignedDialog(true)
		}
	}, [areAllSigned])

	useEffect(() => {
		if (areAllExpiredOrCancelledOrFailed) {
			toggleNoDocumentsToSignDialog(true)
		}
	}, [areAllExpiredOrCancelledOrFailed])

	useEffect(() => {
		if (error && !isIgnoredError(error)) {
			toggleErrorViewer(true)
		}
	}, [error])

	return (
		<DocumentTitle title="Simply Connect | Documents | E-sign requested">
			<div className="ESignRequestedDocuments">
				<Logo
					iconSize={52}
					className="ESignRequestedDocuments-Logo"
				/>
				<div className="ESignRequestedDocuments-Body">
					<div className="page-header">
						<div className="page-header-item">
							<div className="page-title">
								<div className="page-title-text">
									Documents
								</div>
								<div className="d-inline-block text-nowrap">
									{clientName && (
										<span className="ESignRequestedDocuments-ClientName">
												&nbsp;/&nbsp;{clientName}
											</span>
									)}
									{isNotEmpty(data) && (
										<Badge color="info" className="Badge Badge_place_top-right">
											{data.length}
										</Badge>
									)}
								</div>
							</div>
						</div>
					</div>

					<ESignRequestedDocumentList
						data={data}
						isFetching={isFetching}
						onSign={onSign}
					/>
				</div>

				<Footer hasLogo theme="gray">
					<div className="ESignRequestedDocuments-OrganizationPhone">
						Call (844) 666-3038
					</div>
				</Footer>

				{isReadyToSignDialogOpen && (
					<SuccessDialog
						isOpen
						title={`${selected.title} is ready ${selected.signature.statusName === SENT ? 'for review' : 'and available to sign'}.`}
						text="Upon the click you will be redirected to a new page where you should enter the PIN from received SMS to open the document."
						buttons={[
							{
								text: 'Close',
								outline: true,
								color: 'success',
								onClick: () => {
									setSelected(null)
									toggleReadyToSignDialog(false)
								}
							},
							{
								text: selected.signature.statusName === SENT ? 'Review' : 'Sign',
								color: 'success',
								onClick: signSelected
							}
						]}
					/>
				)}

				{areAllAlreadySignedDialogOpen && (
					<SuccessDialog
						isOpen
						title="Thank you! All documents have been signed"
						buttons={[{ text: 'Close', onClick: () => toggleAllAlreadySignedDialog(false) }]}
					/>
				)}

				{isNoDocumentsToSignDialogOpen && (
					<WarningDialog
						isOpen
						title="There are no documents to sign. Request is expired or was cancelled, please contact your Administrator"
						buttons={[{ text: 'Close', onClick: () => toggleNoDocumentsToSignDialog(false) }]}
					/>
				)}

				<ErrorViewer
					error={error}
					isOpen={isErrorViewerOpen}
					onClose={() => toggleErrorViewer(false)}
				/>
			</div>
		</DocumentTitle>
	)
}

export default memo(ESignRequestedDocuments)