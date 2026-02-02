import React, { memo, useCallback, useEffect, useMemo, useRef, useState } from 'react'

import PropTypes from 'prop-types'

import { withCookies } from 'react-cookie'

import { useHistory, useLocation } from 'react-router-dom'

import { bindActionCreators } from 'redux'
import { connect, useSelector } from 'react-redux'

import { withExternalProviderUrlCheck } from 'hocs'

import { TrackingService } from 'factories'

import { ErrorViewer } from 'components'

import { Dialog } from 'components/dialogs'

import { useMemoEffect } from 'hooks/common'

import { useAuthUser } from 'hooks/common/redux'

import * as tokenActions from 'redux/auth/token/tokenActions'
import * as loginActions from 'redux/auth/login/loginActions'
import * as logoutActions from 'redux/auth/logout/logoutActions'
import * as sessionActions from 'redux/auth/session/sessionActions'
import * as invitationRequestTokenActions from 'redux/auth/invitation/request/token/invitationRequestTokenActions'
import * as resetPasswordRequestTokenActions
  from 'redux/auth/password/reset/request/token/resetPasswordRequestTokenActions'

import config from 'config'

import { primaryOrganizationStore } from 'lib/stores'

import { AUTHENTICATION_EXCLUDED_PATHS } from 'lib/Constants'

import { DateUtils as DU } from 'lib/utils/Utils'

import { noop } from 'lib/utils/FuncUtils'

import { matches } from 'lib/utils/UrlUtils'

import { path } from 'lib/utils/ContextUtils'

import { Response } from 'lib/utils/AjaxUtils'

import ValidateSession from './strategies/ValidateSession'

import { ReactComponent as Warning } from 'images/alert-yellow.svg'
import userLocalStorage from "../../hooks/common/redux/useLogo";

const INTERVAL = 1000

const REMIND_TIME_MOMENT = 60 * 1000
const SESSION_ABOUT_TO_EXPIRE_TIME_MOMENT = 3 * 60 * 1000

let isSessionAboutToExpire = false

const trackingService = new TrackingService()

function mapStateToProps(state) {
  return { auth: state.auth }
}

function mapDispatchToProps(dispatch) {
  return {
    actions: {
      auth: {
        token: bindActionCreators(tokenActions, dispatch),
        login: bindActionCreators(loginActions, dispatch),
        logout: bindActionCreators(logoutActions, dispatch),
        session: bindActionCreators(sessionActions, dispatch),
        invitation: {
          request: {
            token: bindActionCreators(invitationRequestTokenActions, dispatch)
          }
        },
        password: {
          reset: {
            request: {
              token: bindActionCreators(resetPasswordRequestTokenActions, dispatch)
            }
          }
        }
      }
    }
  }
}


/**
 * 认证函数
 *
 * @param {Object} options - 选项对象
 * @param {Object} options.actions - 包含认证操作的对象
 * @param {Object} options.cookies - 包含 cookie 操作的对象
 * @param {string} options.successRedirectPath - 成功重定向路径
 * @param {string} options.failureRedirectPath - 失败重定向路径
 * @param {boolean} options.shouldRedirectBySuccess - 是否通过成功进行重定向，默认为 true
 * @param {boolean} options.shouldRedirectByFailure - 是否通过失败进行重定向，默认为 true
 * @param {function()} options.onSuccess - 成功回调函数
 * @param {function()} options.onFailure - 失败回调函数
 */
function Authentication(
  {
    actions,
    cookies,

    successRedirectPath,
    failureRedirectPath,

    shouldRedirectBySuccess,
    shouldRedirectByFailure,

    onSuccess,
    onFailure
  }
) {
  const [isReminderOpen, toggleReminder] = useState(false)

  const [isSessionValidationNeed, setSessionValidationNeed] = useState(true)
  const [isSessionMonitorEnabled, setSessionMonitorEnabled] = useState(true)

  const sessionMonitorIdRef = useRef(0)

  const isReminderOpenRef = useRef(false)
  isReminderOpenRef.current = isReminderOpen

  const user = useAuthUser()
  const local = userLocalStorage();
  const history = useHistory()
  const location = useLocation()
  const session = useSelector(s => s.auth.session)

  if (local === 'undefined') {
    localStorage.clear();
  }

  const primaryOrganization = primaryOrganizationStore.get();
  /**
   * 清除会话错误
   *
   * @returns {Promise<void>} 返回一个 Promise 对象，表示清除会话错误操作是否成功
   */
  const clearSessionError = useCallback(() => {
    return actions.auth.session.clearError();
  }, [actions]);

  /**
   * 恢复用户登录状态
   *
   * @returns {void} 无返回值
   */
  const restoreUser = useCallback(() => {
    actions.auth.login.restore();
  }, [actions]);

  /**
   * 移除用户登录状态
   *
   * @returns {void} 无返回值
   */
  const removeUser = useCallback(() => {
    actions.auth.login.remove();
  }, [actions]);

  /**
   * 验证会话状态
   *
   * @returns {Promise<boolean>} 返回一个 Promise 对象，表示验证会话状态操作是否成功，并且包含一个布尔值表示验证结果（true 表示通过，false 表示未通过）
   */
  const validateSession = useCallback(() => {
    return actions.auth.session.validate();
  }, [actions]);

  /**
   * 验证邀请令牌
   *
   * @param {string} token - 令牌
   * @param {Object} params - 参数
   */
  const validateInvitationToken = useCallback((token, params) => {
    actions.auth.invitation.request.token.validate(token, params)
  }, [actions])

  /**
   * 验证重置密码令牌
   *
   * @param {string} token - 令牌
   */
  const validateResetPasswordToken = useCallback(token => {
    actions.auth.password.reset.request.token.validate(token)
  }, [actions])

  /**
   * 验证令牌
   *
   * @param {string} token - 令牌
   * @param {Object} params - 参数
   */
  const validateToken = useCallback((token, params) => {
    if (matches('*/reset-password*', location.pathname)) {
      return validateResetPasswordToken(token)
    }
    return validateInvitationToken(token, params)
  }, [
    location,
    validateInvitationToken,
    validateResetPasswordToken,
  ])

  /**
   * 登出函数
   */
  const logout = useCallback(
    () => actions.auth.logout.logout(),
    [actions]
  )

  /**
   * 跳转函数
   *
   * @param {string} to - 目标路径
   * @param {Object=} state - 状态对象，默认为空对象
   */
  const redirect = useCallback((to, state = {}) => {
    history.push(path(to), state)
  }, [history])

  /**
   * 重定向到成功路径。
   *
   * @param {Object} state - 状态对象。
   */
  const redirectToSuccessPath = useCallback(state => {
    redirect(successRedirectPath, state);
  }, [redirect, successRedirectPath]);

  /**
   * 重定向到失败路径。
   *
   * @param {Object} state - 状态对象。
   */
  const redirectToFailurePath = useCallback(state => {
    trackingService.catchMessage("Redirect to " + failureRedirectPath);
    redirect(failureRedirectPath, state);
  }, [redirect, failureRedirectPath]);


  /**
   * 捕获并重定向到失败路径。
   */
  const captureAndRedirectToFailurePath = useCallback(() => {
    const { search, pathname } = location

    /**
     * 重定向到失败路径，并传递 next 路径参数。
     *
     * @param {string} nextPath - 下一个路径。
     */
    redirectToFailurePath({
      nextPath: (
        pathname === path('/') ? path('/clients') : pathname
      ) + search
    })
  }, [location, redirectToFailurePath])

  /**
   * 重定向到主要组织。
   */
  const redirectToPrimaryOrganization = useCallback(() => {
    if (primaryOrganization) window.location.replace(primaryOrganization.url);
  }, [primaryOrganization]);

  /**
   * 停止会话监控。
   */
  const stopSessionMonitor = useCallback(() => {
    if (config.environment === 'production') {
      clearInterval(sessionMonitorIdRef.current);
      sessionMonitorIdRef.current = 0;
    }
  }, []);

  /**
   * 登出操作。
   *
   * @param {boolean} isSessionExpired - 是否会话过期。
   */
  const onLogout = useCallback((isSessionExpired = false) => {
    trackingService.catchMessage("Auto Logout");
    stopSessionMonitor();

    logout().then(() => {
      trackingService.catchMessage("Auto Logout Success");
      toggleReminder(false);

      if (primaryOrganization) {
        redirectToPrimaryOrganization();
      } else {
        redirectToFailurePath({
          isSessionExpired,
          isLoginPopupOpen: true
        });
      }
    });
  }, [
    logout,
    stopSessionMonitor,
    primaryOrganization,
    redirectToFailurePath,
    redirectToPrimaryOrganization
  ]);


  /**
   * 检查会话生命周期。
   */

  const onCheckSessionLifeTime = () => {

    const jwtHeaderAndPayload = cookies.get('jwtHeaderAndPayload');

    if (!jwtHeaderAndPayload) {
      onLogout(true)
      return
    }

    const { exp } = JSON.parse(
      atob(jwtHeaderAndPayload.split('.')[1])
    )

    const delta = exp * 1000 - Date.now()

    if (delta < 0) onLogout(true)

    const shouldRemind = (
      delta > 0 && delta < REMIND_TIME_MOMENT
    )

    if (shouldRemind && !isReminderOpenRef.current) {
      toggleReminder(true)
    }

    const isAboutToExpire = (
      delta > 0 && delta < SESSION_ABOUT_TO_EXPIRE_TIME_MOMENT
    )

    if (isAboutToExpire && !isSessionAboutToExpire) {
      trackingService.catchMessage(`Session is about to expire. Current time: ${DU.format(Date.now(), DU.formats.longDateTime)}`)
      trackingService.catchMessage(`exp = ${exp}, or ${DU.format(exp * 1000, DU.formats.longDateTime)}`)

      isSessionAboutToExpire = true
      actions.auth.session.setAboutToExpire(isAboutToExpire)
    }

    if (!isAboutToExpire && isSessionAboutToExpire) {
      trackingService.catchMessage(`Session is continued. Current time: ${DU.format(Date.now(), DU.formats.longDateTime)}`)
      trackingService.catchMessage(`exp = ${exp}, or ${DU.format(exp * 1000, DU.formats.longDateTime)}`)

      isSessionAboutToExpire = false
      actions.auth.session.setAboutToExpire(isAboutToExpire)
    }
  }

  /**
   * 开始会话监视器。
   */
  const startSessionMonitor = useCallback(() => {
    if (config.environment === 'production') {
      clearInterval(sessionMonitorIdRef.current)
      sessionMonitorIdRef.current = setInterval(
        onCheckSessionLifeTime, INTERVAL
      )
    }
  }, [onCheckSessionLifeTime])

  const onContinueSession = useCallback(() => {
    stopSessionMonitor()
    toggleReminder(false)
    setSessionMonitorEnabled(false)

    validateSession()
      .then(Response(() => {
        startSessionMonitor()
        setSessionMonitorEnabled(true)
      }, () => onLogout(true)))
      .catch(() => onLogout(true))
  }, [
    onLogout,
    validateSession,
    stopSessionMonitor,
    startSessionMonitor
  ])

  /**
   * 继续会话
   *
   * @type {(function(): void)|*}
   */
  const onCloseErrorViewer = useCallback(() => {
    clearSessionError()
    removeUser()
  }, [removeUser, clearSessionError])

  /**
   *
   * @type {
   * {redirect: function(*, {}=): void,
   * captureAndRedirectToFailurePath: function(): void,
   * redirectToSuccessPath: function(*): void,
   * onFailure: function(),
   * shouldRedirectBySuccess: boolean,
   * shouldRedirectByFailure: boolean,
   * logout: function(): *,
   * restoreUser: function(): void,
   * redirectToFailurePath: function(*): void,
   * location: Location<H.LocationState>,
   * startSessionMonitor: function(): void,
   * validateSession: function(): *,
   * stopSessionMonitor: function(): void,
   * onSuccess: function(),
   * validateToken: function(*, *): (void)}
   * }
   */
  const context = useMemo(() => ({
    logout,
    redirect,
    location,
    onSuccess,
    onFailure,
    restoreUser,
    validateToken,
    validateSession,
    stopSessionMonitor,
    startSessionMonitor,
    redirectToSuccessPath,
    redirectToFailurePath,
    shouldRedirectBySuccess,
    shouldRedirectByFailure,
    captureAndRedirectToFailurePath
  }), [
    logout,
    redirect,
    location,
    onSuccess,
    onFailure,
    restoreUser,
    validateToken,
    validateSession,
    stopSessionMonitor,
    startSessionMonitor,
    redirectToSuccessPath,
    redirectToFailurePath,
    shouldRedirectBySuccess,
    shouldRedirectByFailure,
    captureAndRedirectToFailurePath
  ])

  useEffect(() => {
    if (isSessionValidationNeed) {
      ValidateSession(context)
      setSessionValidationNeed(false)
    }
  }, [context, isSessionValidationNeed])

  useEffect(() => {
    if (
      user
      && isSessionMonitorEnabled
      && !sessionMonitorIdRef.current
      && cookies.get('jwtHeaderAndPayload')
    ) {
      startSessionMonitor()
    }
  }, [
    user,
    cookies,
    startSessionMonitor,
    isSessionMonitorEnabled
  ])

  useMemoEffect(memo => {
    const prevUser = memo()

    if (!user && prevUser) {
      stopSessionMonitor()

      if (shouldRedirectByFailure
        && AUTHENTICATION_EXCLUDED_PATHS.every(t => !matches(t, location.pathname))) {
        if (primaryOrganization?.shouldRedirect) {
          redirectToPrimaryOrganization()
        } else redirectToFailurePath()
      }
    }

    memo(user)
  }, [
    user,
    location,
    stopSessionMonitor,
    primaryOrganization,
    redirectToFailurePath,
    shouldRedirectByFailure,
    redirectToPrimaryOrganization
  ])

  return (
    <>
      {isReminderOpen && (
        <Dialog
          isOpen
          text='Session is about to expire due to long inactivity. Please click  "Continue" to stay in the session'
          title='Session expiring'
          icon={Warning}
          buttons={[
            {
              color: 'success',
              outline: true,
              text: 'Logout',
              onClick: () => onLogout(true)
            },
            {
              color: 'success',
              text: 'Continue',
              onClick: onContinueSession
            }
          ]}
          className='WarningDialog'
        />
      )}
      {session.error && (
        <ErrorViewer
          isOpen
          error={session.error}
          onClose={onCloseErrorViewer}
        />
      )}
    </>
  );
}

Authentication.propTypes = {
  successRedirectPath: PropTypes.string,
  failureRedirectPath: PropTypes.string,

  shouldRedirectBySuccess: PropTypes.bool,
  shouldRedirectByFailure: PropTypes.bool,

  onSuccess: PropTypes.func,
  onFailure: PropTypes.func
}

Authentication.defaultProps = {
  onSuccess: noop,
  onFailure: noop
}

export default connect(mapStateToProps, mapDispatchToProps)(
  withExternalProviderUrlCheck(withCookies(memo(Authentication)))
)
