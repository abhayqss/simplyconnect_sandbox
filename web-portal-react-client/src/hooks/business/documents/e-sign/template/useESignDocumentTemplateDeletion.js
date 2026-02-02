import { useMutation } from '@tanstack/react-query'
import service from 'services/DocumentESignService'

function fetch({ templateId, ...params }) {
    return service.deleteTemplateById(templateId, params)
}

export default function useESignDocumentTemplateDeletion(options) {
    return useMutation(fetch, options)
}