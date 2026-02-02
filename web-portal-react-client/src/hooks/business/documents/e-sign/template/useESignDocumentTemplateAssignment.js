import { useMutation } from 'hooks/common'

import service from 'services/DocumentTemplateService'

function submit(data) {
  return service.assignToFolder(data)
}

function useESignDocumentTemplateAssignment(params, options) {
  return useMutation(params, submit, options)
}

export default useESignDocumentTemplateAssignment
