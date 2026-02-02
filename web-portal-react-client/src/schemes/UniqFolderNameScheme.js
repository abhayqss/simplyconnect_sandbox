import { Shape } from './types'

import service from 'services/DocumentFolderService'

export default function UniqFolderNameScheme(field) {
    return Shape().test({
        name: 'UniqFolderNameScheme',
        test: async (data) => {
            try {
                return await service.validateUniqName(data)
            } catch (error) {
                return false
            }
        },
        message: `Folder with name "${field}" already exists.`,
        exclusive: true,
    })
}
