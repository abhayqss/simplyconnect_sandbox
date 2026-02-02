import BaseService from './BaseService'

export class EsignService extends BaseService {
  getEsignToken(params) {
    return super.request({
      method: 'POST',
      url: '/documents/e-sign/generateToken',
      body: params
    })
  }

  /**
   * 查询重复姓名
   * @param title
   * @return {Promise<unknown>|*}
   */
  queryForDuplicateNames(title) {
    return super.request({
      url: '/documents/e-sign/titleExist',
      params: { title },
    })
  }

  /**
   * 保存多人签字模版
   * @param {Object} params - 参数对象
   * @param {string} params.title - 文档标题
   * @param {string[]} params.documentUrls - 文档链接数组
   * @returns {Promise<unknown>} - 返回一个Promise对象，表示生成的文档
   */
  multipleSignatureTemplateAdded(params) {
    return super.request({
      method:'POST',
      url: '/documents/e-sign/multiSignTemplates',
      body:params,
    })
  }

  // 复制多人签字模版
  copyMultiPersonSignatureTemplate(params) {
    return super.request({
      method:'POST',
      url: '/documents/e-sign/copyTemplates',
      body:params,
    })
  }
  multipleSignatureTemplateEdit(params) {
    return super.request({
      method: 'PUT',
      type: 'multipart/form-data',
      url: `/documents/e-sign/multiTemplates/${params.templateId}`,
      body: params,
    });
  }

  /**
   * 归档模版
   * @param {Object} params
   * @param {String} params.templateId - pdf id
   * @param {String} params.applicationKey - 模版 key
   * @return {Promise<unknown>|*}
   */
  archivingTemplates(params) {
    return super.request({
      method:'POST',
      url: '/documents/e-sign/avaterTemplate',
      body:params,
    })
  }

}

const service = new EsignService()
export default service
