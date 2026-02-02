import { lazy } from 'react'

const DocumentSigned = lazy(() => import('containers/DocumentSigned/DocumentSigned'))

export default {
    component: DocumentSigned,
    path: '/document-signed'
}
