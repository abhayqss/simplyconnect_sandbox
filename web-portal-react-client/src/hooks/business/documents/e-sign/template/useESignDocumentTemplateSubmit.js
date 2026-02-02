import { useMutation } from 'hooks/common'

import service from 'services/DocumentESignService'

function submit({ isAutoSave, ...data } = {}) {
  // function submit(data) {
// 	return service.saveTemplate(data)
  const method = !isAutoSave ? 'saveTemplate' : 'autoSaveTemplate'

  return service[method](data)
}

function useESignDocumentTemplateSubmit(params, options) {
	return useMutation(params, submit, options)
}

export default useESignDocumentTemplateSubmit
