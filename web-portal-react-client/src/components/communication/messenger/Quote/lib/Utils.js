import { QUOTE_TYPES } from './Constants'

const {
	FILE,
	IMAGE,
	TEXT,
	VIDEO,
	VOICE
} = QUOTE_TYPES

export function getQuoteTypeByMimeType(type) {
	if (!type) return
	if (type.includes('image')) return IMAGE
	if (type.includes('audio')) return VOICE
	if (type.includes('video')) return VIDEO
	if (type === 'text/plain') return TEXT
	return FILE
}