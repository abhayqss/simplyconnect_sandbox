import { any, all } from 'underscore'

import { Response } from 'lib/utils/AjaxUtils'
import { matches, getQueryParams } from 'lib/utils/UrlUtils'

import {
    SERVER_ERROR_CODES,
    AUTHENTICATION_EXCLUDED_PATHS
} from 'lib/Constants'

import AuthenticationError from 'lib/errors/AuthenticationError'

const {
    INVALID_TOKEN
} = SERVER_ERROR_CODES

// 验证外部提供商
function ValidateExternalProvider(context) {
    const {
        location,
        // 失败时
        onFailure
    } = context

    const { pathname, search } = location
    // 需要令牌
    const isTokenRequired = any(
        ['*/reset-password-request*'],
        t => !matches(t, pathname)
    )
    // 不排除的路径
    const isNotExcludedPath = all(
        AUTHENTICATION_EXCLUDED_PATHS, t => !matches(t, pathname)
    )

    const { token } = getQueryParams(search)
    const targetPath = isNotExcludedPath ? pathname : null

    if (token) {
        // 验证令牌
        context.validateToken(
            token, { isExternalProvider: true }
        ).then(Response(({ data: isValid }) => {
            if (isValid && isNotExcludedPath) {
                context.redirect(
                    `/external-provider/create-password?token=${token}`,
                    { nextPath: targetPath, shouldCancelTokenValidation: true }
                )
            } else if (!isValid) {
                context.redirectToFailurePath()

                onFailure(new AuthenticationError({
                    code: INVALID_TOKEN,
                    message: 'The session is closed'
                }))
            }
        })).catch(() => {
            context.redirectToFailurePath({ nextPath: targetPath })

            onFailure(new AuthenticationError({
                code: INVALID_TOKEN,
                message: 'The session is closed'
            }))
        })
    }

    else if (isTokenRequired) {
        context.redirectToFailurePath({ nextPath: targetPath })

        onFailure(new AuthenticationError({
            code: INVALID_TOKEN,
            message: 'The session is closed'
        }))
    }
}

export default ValidateExternalProvider
