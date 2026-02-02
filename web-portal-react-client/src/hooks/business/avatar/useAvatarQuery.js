import { useQuery } from '@tanstack/react-query'

import service from 'services/AvatarService'

import {
    isInteger,
    getDataUrl
} from 'lib/utils/Utils'

import Converter from 'lib/converters/Converter'
import factory from 'lib/converters/ConverterFactory'

const converter = factory.getConverter(Converter.types.BINARY_TO_BASE_64)

const fetch = async (avatarId) => {
    if (isInteger(avatarId)) {
        const response = await service.findById(avatarId)

        const { data, mediaType } = response

        return getDataUrl(converter.convert(data), mediaType)
    }

    return null
}

export default function useAvatarQuery(avatarId, options) {
    return useQuery(['Avatar', avatarId], () => fetch(avatarId), options)
}