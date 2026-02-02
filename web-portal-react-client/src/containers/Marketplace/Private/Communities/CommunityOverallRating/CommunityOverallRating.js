import React, {
    useState,
	useEffect,
    useCallback
} from 'react'

import cn from 'classnames'

import { Rater } from 'components'
import { IconButton } from 'components/buttons'

import {
	useEventEmitter,
	useQueryInvalidation
} from 'hooks/common'

import {
	useCommunitySaving,
	useCommunityRemoving
} from 'hooks/business/Marketplace'

import { isInteger } from 'lib/utils/Utils'

import { ReactComponent as Info } from 'images/info-2.svg'
import { ReactComponent as Heart } from 'images/heart.svg'
import { ReactComponent as Heart2 } from 'images/like.svg'

import './CommunityOverallRating.scss'

export default function CommunityOverallRating(
	{
		rating,
		onInfo,
		communityId,
		isSaved: isSavedByDefault
	}
) {
    const [isSaved, setSaved] = useState(isSavedByDefault)

	const emitter = useEventEmitter()
	const invalidateQuery = useQueryInvalidation()

	const {
		mutateAsync: save,
		isLoading: isSaving
	} = useCommunitySaving({ communityId }, {
		onSuccess: () => {
			setSaved(true)
			invalidateQuery('Marketplace.SavedCommunities')
		}
	})

	const {
		mutateAsync: remove,
		isLoading: isRemoving
	} = useCommunityRemoving({ communityId }, {
		onSuccess: () => {
			setSaved(false)
			invalidateQuery('Marketplace.SavedCommunities')
		}
	})

    const onClickHeart = useCallback(() => {
        if (isSaved) remove()
        else save()
    }, [save, remove, isSaved])

	const onCommunityDeleted = useCallback(({ data }) => {
		if (data.communityId === communityId) setSaved(false)
	}, [communityId])

	useEffect(() => {
		emitter.on('Marketplace.Community:deleted', onCommunityDeleted)
		return () => emitter.off('Marketplace.Community:deleted', onCommunityDeleted)
	}, [emitter, onCommunityDeleted])

	return (
		<div className="CommunityOverallRating">
			<div className="CommunityOverallRating-RatingInfo">
				<div className="flex-1 d-flex flex-row align-items-center">
					{isInteger(rating) && (
						<Rater
							total={5}
							withDigits
							rating={rating}
							className="CommunityOverallRating-Rater"
						/>
					)}
				</div>
			</div>
			<div className="d-flex flex-row flex-wrap justify-content-end">
				<IconButton
					size={20}
					Icon={Info}
					onClick={onInfo}
					shouldHighLight={false}
					className="CommunityOverallRating-ActionBtn"
				/>
                <IconButton
                    size={20}
                    shouldHighLight={false}
                    Icon={isSaved ? Heart2 : Heart}
                    onClick={onClickHeart}
                    className={cn(
                        'CommunityOverallRating-ActionBtn',
                        (isSaving || isRemoving) && 'CommunityOverallRating-ActionBtn_blocked'
                    )}
                />
			</div>
		</div>
	)
}