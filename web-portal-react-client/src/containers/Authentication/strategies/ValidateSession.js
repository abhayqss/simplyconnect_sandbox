import { all } from 'underscore'

import config from 'config'

import { AUTHENTICATION_EXCLUDED_PATHS, SYSTEM_ROLES } from 'lib/Constants'

import authUserStore from 'lib/stores/AuthUserStore'

import { matches } from 'lib/utils/UrlUtils'

import ValidateLoggedInUser from './ValidateLoggedInUser'
import ValidateExternalProvider from './ValidateExternalProvider'

const { EXTERNAL_PROVIDER } = SYSTEM_ROLES

function ValidateSession(context) {
  const {
    location,
    // 是外部提供商的网址
    isExternalProviderUrl,
    // 应该通过失败进行重定向
    shouldRedirectByFailure,
  } = context

  const { pathname } = location

  // 获取的本人登录信息
  const user = authUserStore.get()

  // 是相关主机
  const isRelevantHost = (
    config.location.host === window?.location?.host
  )

  //  不排除的路径
  const isNotExcludedPath = all(
    AUTHENTICATION_EXCLUDED_PATHS, t => !matches(t, pathname)
  )

  //  if 是外部提供商网址 user 角色名 不是 EXTERNAL_PROVIDER
  if (isExternalProviderUrl && user?.roleName !== EXTERNAL_PROVIDER) {
    //  验证外部提供商
    ValidateExternalProvider(context)
  } else if (user) {
    ValidateLoggedInUser(context)
  } else if (isRelevantHost && shouldRedirectByFailure && isNotExcludedPath) {
    context.captureAndRedirectToFailurePath()
  }
}

export default ValidateSession
