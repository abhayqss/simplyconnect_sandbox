import React from 'react'

import { ReactComponent as File } from 'images/unknown-file.svg'
import { ReactComponent as Image } from 'images/camera.svg'
import { ReactComponent as Video } from 'images/play.svg'

function RenderIcon(Icon) {
	return props => <Icon {...props}/>
}

export const QUOTE_TYPES = {
	TEXT: 'TEXT',
	IMAGE: 'IMAGE',
	VIDEO: 'VIDEO',
	FILE: 'FILE',
	VOICE: 'VOICE'
}

export const QUOTE_TYPE_ICONS = {
	[QUOTE_TYPES.FILE]: RenderIcon(File),
	[QUOTE_TYPES.IMAGE]: RenderIcon(Image),
	[QUOTE_TYPES.VIDEO]: RenderIcon(Video),
	[QUOTE_TYPES.VOICE]: RenderIcon(Video)
}