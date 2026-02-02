import React, {
	useRef,
	useState,
	useCallback
} from 'react'

import $ from 'jquery'
import cn from 'classnames'

import {
	CollapsibleSection
} from 'components'

import {
	useDeferred,
	useEventEmitter
} from 'hooks/common'

import {
	useCommunityRemoving,
	useSavedCommunitiesQuery
} from 'hooks/business/Marketplace'

import {
	isNotEmpty
} from 'lib/utils/Utils'

import { ReactComponent as Like } from 'images/like.svg'
import { ReactComponent as Expand } from 'images/expand-2.svg'
import { ReactComponent as Top } from 'images/chevron-top.svg'
import { ReactComponent as Bottom } from 'images/chevron-bottom.svg'

import SavedCommunityList from '../SavedCommunitiesList/SavedCommunityList'

import './SavedCommunitiesPopup.scss'

export default function SavedCommunitiesPopup(
	{
		className,
		onExtendedMode,
		onCreateReferral
	}
) {
	const toggleRef = useRef()

	const [selected, setSelected] = useState(null)

	const emitter = useEventEmitter()

	const {
		data,
		refetch,
		isFetching
	} = useSavedCommunitiesQuery({}, {
		staleTime: 0
	})

	const {
		mutateAsync: remove,
		isLoading: isRemoving
	} = useCommunityRemoving({}, {
		onSuccess: () => {
			refetch()
			setSelected(null)
			emitter.fire(
				'Marketplace.Community:deleted',
				{ communityId: selected.communityId }
			)
		}
	})

	const removeDeferred = useDeferred(remove)

	const onClickHeader = useCallback(e => {
		if (!$(e.target).closest(toggleRef.current).length) {
			onExtendedMode()
		}
	}, [onExtendedMode])

	const onDelete = useCallback(o => {
		setSelected(o)
		removeDeferred(o)
	}, [removeDeferred])

	return isNotEmpty(data) && (
		<CollapsibleSection
			toggledBy="icon"
			renderHeader={(isOpen, toggle) => (
				<div
					onClick={onClickHeader}
					className="SavedCommunitiesPopup-HeaderBody"
				>
					<Like className="SavedCommunitiesPopup-HeaderIcon margin-right-10"/>
					<div className="flex-1">Saved for later ({data.length})</div>
					<Expand
						onClick={onExtendedMode}
						className="SavedCommunitiesPopup-HeaderIcon"
					/>
					<div onClick={toggle}>
						{isOpen ? (
							<Bottom ref={toggleRef} className="SavedCommunitiesPopup-ToggleIcon"/>
						) : (
							<Top ref={toggleRef} className="SavedCommunitiesPopup-ToggleIcon"/>
						)}
					</div>
				</div>
			)}
			className={cn("SavedCommunitiesPopup", className)}
			headerClassName="SavedCommunitiesPopup-Header"
			bodyClassName="SavedCommunitiesPopup-Body"
		>
			<SavedCommunityList
				data={data}
				isFetching={isFetching || isRemoving}
				onDelete={onDelete}
				onCreateReferral={onCreateReferral}
			/>
		</CollapsibleSection>
	)
}