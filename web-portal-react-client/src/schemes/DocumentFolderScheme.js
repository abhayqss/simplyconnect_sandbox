import { Shape, string } from './types'

const DocumentFolderScheme = Shape({
    name: string().trim().required()
})

export default DocumentFolderScheme
