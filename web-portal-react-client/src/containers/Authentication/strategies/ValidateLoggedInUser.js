import { SERVER_ERROR_CODES } from 'lib/Constants'

import AuthenticationError from 'lib/errors/AuthenticationError'

const {
    INVALID_TOKEN
} = SERVER_ERROR_CODES
/**
 * 函数的描述信息
 *
 * @param {Object} context - 上下文对象
 * @param {boolean} context.shouldRedirectBySuccess - 是否通过成功重定向
 * @param {boolean} context.shouldRedirectByFailure - 是否通过失败重定向
 * @param {Function} context.onSuccess - 成功回调函数
 * @param {Function} context.onFailure - 失败回调函数
 * @param {Function} context.validateSession - 验证会话是否有效，并根据结果执行相应操作。
 * @param {Function} context.restoreUser - 恢复用户
 * @param {Function} context.startSessionMonitor - 开始会话监视器
 * @param {Function} context.redirectToSuccessPath - 重定向到成功路径
 * @param {Function} context.logout - 登出
 * @param {Function} context.redirectToFailurePath - 重定向到失败路径
 */
function ValidateLoggedInUser(context) {
    const {
        // 应该通过成功重定向
        shouldRedirectBySuccess,
        // 应该通过失败进行重定向
        shouldRedirectByFailure,

        onSuccess,
        onFailure
    } = context

    context.validateSession()
        .then(response => {
            if (response.success && response.data) {
                // 会话有效，恢复用户状态并启动会话监控器

                context.restoreUser()

                context.startSessionMonitor()

                if (shouldRedirectBySuccess) {
                    context.redirectToSuccessPath()
                }

                onSuccess()
            } else {
                // 会话无效，抛出认证错误并登出用户
                onFailure(new AuthenticationError({
                    code: INVALID_TOKEN,
                    message: 'The session is expired'
                }))

                // 登出用户，并在需要时进行失败重定向

                context.logout()
                    .then(() => {
                        if (shouldRedirectByFailure) {
                            context.redirectToFailurePath({
                                isSessionExpired: true
                            })
                        }
                    })
            }
        })
        .catch(e => {
            onFailure(e)

            context.logout()
                .then(() => {
                    if (shouldRedirectByFailure) {
                        context.redirectToFailurePath({
                            isSessionExpired: true
                        })
                    }
                })
        })
}

export default ValidateLoggedInUser
