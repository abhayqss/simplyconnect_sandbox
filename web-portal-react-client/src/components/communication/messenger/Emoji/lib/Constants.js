import React from 'react'

import { ReactComponent as Like } from 'images/emodji-like.svg'
import { ReactComponent as Dislike } from 'images/emodji-dislike.svg'
import { ReactComponent as Smile } from 'images/emodji-regular-smile.svg'
import { ReactComponent as Checkmark } from 'images/emodji-checkmark.svg'

function RenderIcon(Icon) {
	return props => <Icon {...props}/>
}

export const EMOJI = [
	{ id: 0, name: 'LIKE', title: 'Like', renderIcon: RenderIcon(Like) },
	{ id: 1, name: 'DISLIKE', title: 'Dislike', renderIcon: RenderIcon(Dislike) },
	{ id: 2, name: 'SMILE_REGULAR', title: 'Smile Regular', renderIcon: RenderIcon(Smile) },
	{ id: 3, name: 'CHECKMARK', title: 'Checkmark', renderIcon: RenderIcon(Checkmark) },
]