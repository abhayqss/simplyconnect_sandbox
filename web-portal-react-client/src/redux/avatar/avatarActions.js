import service from 'services/AvatarService'

import { getDataUrl } from 'lib/utils/Utils'
import Converter from 'lib/converters/Converter'
import factory from 'lib/converters/ConverterFactory'

const converter = factory.getConverter(Converter.types.BINARY_TO_BASE_64)

export function download(id) {
    return async () => {
        const response = await service.findById(id)
        const { data, mediaType } = response

        return getDataUrl(converter.convert(data), mediaType)
    }
}
