import { integer, Shape, ListOf } from './types'

const Folder = Shape({
    folderId: integer().nullable().required(),
    communityId: integer().nullable().required(),
})

const Scheme = Shape({
    value: ListOf(Folder)
})

export default Scheme