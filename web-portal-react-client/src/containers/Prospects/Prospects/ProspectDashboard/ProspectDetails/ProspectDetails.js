import React, {
	memo,
	useMemo,
	useState,
	useCallback
} from 'react'

import cn from 'classnames'

import {
	map,
	chain,
	reject
} from 'underscore'

import {
	Tabs,
	Loader,
	Dropdown,
	DataLoadable
} from 'components'

import {
	SuccessDialog
} from 'components/dialogs'

import {
	DemographicsDetails
} from 'components/business/common'

import {
	ProspectDemographicsDetails
} from 'components/business/Prospects'

import {
	useProspectQuery
} from 'hooks/business/Prospects'

import Avatar from 'containers/Avatar/Avatar'
import ContactViewer from 'containers/Admin/Contacts/ContactViewer/ContactViewer'
import ContactEditor from 'containers/Admin/Contacts/ContactEditor/ContactEditor'

import {
	isEmpty,
	isInteger,
	formatSSN,
	isNotEmpty
} from 'lib/utils/Utils'

import './ProspectDetails.scss'

const TAB = {
	DEMOGRAPHICS: 0,
	SECOND_OCCUPANT: 1
}

const TAB_TITLE = {
	[TAB.DEMOGRAPHICS]: 'Demographics',
	[TAB.SECOND_OCCUPANT]: '2nd Occupant'
}

const TABS = [
	{ name: TAB.DEMOGRAPHICS, title: TAB_TITLE[TAB.DEMOGRAPHICS] },
	{ name: TAB.SECOND_OCCUPANT, title: TAB_TITLE[TAB.SECOND_OCCUPANT] }
]

function getTabs(current) {
	return map(TAB, value => ({
		title: TAB_TITLE[value],
		isActive: +value === current
	}))
}

function ProspectDetails({ prospectId }) {
	const [tab, setTab] = useState(TAB.DEMOGRAPHICS)
	const [isContactViewerOpen, toggleContactViewer] = useState(false)
	const [isContactEditorOpen, toggleContactEditor] = useState(false)
	const [isSaveContactSuccessDialogOpen, toggleSaveContactSuccessDialog] = useState(false)

	const {
		refetch,
		isFetching,
		data: prospect
	} = useProspectQuery({ prospectId }, {
		enabled: isInteger(prospectId)
	})

	const contact = prospect?.associatedContact

	const tabs = useMemo(() => (
		chain(TABS).reject(o => (
			isEmpty(prospect?.secondOccupant)
			&& o.name === TAB.SECOND_OCCUPANT
		)).map(o => ({ ...o, isActive: o.name === tab })).value()
	), [tab, prospect])

	const options = useMemo(() => (
		map(tabs, o => ({
			value: o.name,
			text: o.title,
			isActive: o.name === tab,
			onClick: () => setTab(o.name)
		}))
	), [tab, tabs])

	const onChangeTab = useCallback(tab => setTab(tab), [])

	const onOpenContactViewer = useCallback(() => {
		toggleContactViewer(true)
	}, [])

	const onOpenContactEditor = useCallback(() => {
		toggleContactEditor(true)
	}, [])

	const onSaveContactSuccess = useCallback(() => {
		refetch()
		toggleSaveContactSuccessDialog(true)
	}, [refetch])

	return (
		<div className="ProspectDetails">
			{isFetching ? (
				<Loader/>
			) : (
				<>
					<div className="d-flex margin-bottom-40">
						<div className="margin-right-15">
							{tab === TAB.DEMOGRAPHICS && (
								<Avatar
									size={75}
									alt="Avatar"
									id={prospect.avatarId}
									name={prospect.fullName}
									className={cn(
										'ProspectDetails-Avatar',
										!prospect.isActive && 'black-white-filter'
									)}
								/>
							)}
							{tab === TAB.SECOND_OCCUPANT && (
								<Avatar
									size={75}
									alt="Avatar"
									id={prospect.secondOccupant?.avatarId}
									name={prospect.secondOccupant?.fullName}
									className={cn(
										'ProspectDetails-Avatar',
										!prospect.isActive && 'black-white-filter'
									)}
								/>
							)}
						</div>
                        {tab === TAB.DEMOGRAPHICS && (
                            <div className="d-inline-block">
                                <div className="ProspectDetails-FullName">
                                    {` ${prospect.fullName}`}
                                </div>
                                <div className="ProspectDetails-SSN">
                                    {prospect.ssn && formatSSN(prospect.ssn)}
                                </div>
                                <div
                                    className="ProspectDetails-Status"
                                    style={{ backgroundColor: prospect.isActive ? '#d5f3b8' : '#e0e0e0' }}
                                >
                                    {prospect.isActive ? 'Active' : 'Inactive'}
                                </div>
                            </div>
                        )}
                        {tab === TAB.SECOND_OCCUPANT && (
                            <div className="d-inline-block">
                                <div className="ProspectDetails-FullName">
                                    {` ${prospect.secondOccupant?.fullName}`}
                                </div>
                                <div className="ProspectDetails-SSN">
                                    {prospect.secondOccupant?.ssn && formatSSN(prospect.secondOccupant?.ssn)}
                                </div>
                                <div
                                    className="ProspectDetails-Status"
                                    style={{ backgroundColor: prospect.isActive ? '#d5f3b8' : '#e0e0e0' }}
                                >
                                    {prospect.isActive ? 'Active' : 'Inactive'}
                                </div>
                            </div>
                        )}
					</div>

					{tabs.length > 1 && (
						<Tabs
							items={tabs}
							onChange={onChangeTab}
							className="ProspectDetails-Tabs"
						/>
                    )}

                    {tabs.length > 1 && (
                        <Dropdown
                            value={tab}
                            items={options}
                            toggleText={TAB_TITLE[tab]}
                            className="ProspectDetails-Dropdown Dropdown_theme_blue"
                        />
                    )}

                    <div className="padding-top-30">
						{tab === TAB.DEMOGRAPHICS && (
							<DataLoadable
								data={prospect}
								isLoading={isFetching}
							>
								{data => (
									<ProspectDemographicsDetails
										data={data}
										onViewContact={onOpenContactViewer}
										onCreateContact={onOpenContactEditor}
									/>
								)}
							</DataLoadable>
						)}

						{tab === TAB.SECOND_OCCUPANT
							&& isNotEmpty(prospect?.secondOccupant) && (
								<DataLoadable
									data={prospect}
									isLoading={isFetching}
								>
									{data => (
										<DemographicsDetails
											data={{
												...data.secondOccupant,
												communityTitle: data.communityTitle,
												organizationTitle: data.organizationTitle
											}}
										/>
									)}
								</DataLoadable>
							)}
					</div>
				</>
			)}
			{isContactViewerOpen && (
				<ContactViewer
					isOpen
					contactId={contact.id}
					onClose={() => toggleContactViewer(false)}
				/>
			)}
			{isContactEditorOpen && (
				<ContactEditor
					isOpen
					prospectId={prospectId}
					onSaveSuccess={onSaveContactSuccess}
					onClose={() => toggleContactEditor(false)}
				/>
			)}
			{isSaveContactSuccessDialogOpen && (
				<SuccessDialog
					isOpen
					title="Contact has been created."
					buttons={[
						{
							text: 'Close',
							outline: true,
							className: 'min-width-120 margin-left-80',
							onClick: () => {
								toggleContactEditor(false)
								toggleSaveContactSuccessDialog(false)
							}
						},
						{
							text: 'View Details',
							className: 'min-width-120 margin-right-80',
							onClick: () => {
								toggleContactEditor(false)
								toggleContactViewer(true)
								toggleSaveContactSuccessDialog(false)
							}
						}
					]}
				/>
			)}
		</div>
	)
}

export default memo(ProspectDetails)
