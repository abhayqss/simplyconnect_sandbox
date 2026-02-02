import React, { memo, useEffect, useState } from 'react'

import PTypes from 'prop-types'

import cn from 'classnames'

import { useHistory, useParams } from 'react-router-dom'

import { Popover } from 'reactstrap'

import { ErrorViewer, Picture } from 'components'

import { useSubDomain } from 'hooks/common'

import { useOrganizationQuery } from 'hooks/business/Marketplace/Public'

import config from 'config'

import { ACTION_TYPES, SERVER_ERROR_CODES } from 'lib/Constants'

import { ReactComponent as ChevronTop } from 'images/chevron-top.svg'
import { ReactComponent as ChevronBottom } from 'images/chevron-bottom.svg'

import './ProviderHeader.scss'

const {
  SIGN_IN_SUCCESS,
  SIGN_IN_FAILURE
} = ACTION_TYPES

const { EXPIRED_CREDENTIALS } = SERVER_ERROR_CODES

function ProviderHeader({ className }) {
  const [error, setError] = useState(null)
  const [isSignInPopupOpen, toggleSignInPopupOpen] = useState(false)

  const params = useParams()
  const history = useHistory()
  const organizationCode = useSubDomain()

  const providerId = params.communityId
  const providerName = params.communityName

  const {
    data: organization
  } = useOrganizationQuery({ organizationCode })

  useEffect(() => {
    function onMessage(e) {
      const { context, location: { origin } } = config

      if (e.origin !== origin) return

      try {
        const {
          type, data
        } = JSON.parse(e.data)

        switch (type) {
          case SIGN_IN_SUCCESS: {
            let path = `${origin}${context}/marketplace`

            if (providerId) {
              path += `/communities/${providerName}--@id=${providerId}`
            }

            window.location.href = path
            break
          }
          case SIGN_IN_FAILURE: {
            /*     if (data.code === EXPIRED_CREDENTIALS) {
                   history.push(`/old-password`, {
                     username: data.data?.username,
                     companyId: organization?.loginCompanyId
                   })
                 } else setError(data)*/

            setError(data);
            break
          }

          case 'FORGOT_PASSWORD': {
            history.push(`/reset-password-request?companyId=${organization?.loginCompanyId}`)
          }
        }
      } catch (e) {
        console.log('Unsupported format of a message')
      }
    }

    window.addEventListener('message', onMessage)

    return () => {
      window.removeEventListener('message', onMessage)
    }
  }, [history, organization])

  return (
    <div className={cn("MarketplaceProviderHeader", className)}>
      <div className="MarketplaceProviderHeader-Item">
        <Picture
          path={`/open-marketplace/organizations/${organizationCode}/logo`}
          className="MarketplaceProviderHeader-OrganizationLogo"
        />
      </div>

      <div className="MarketplaceProviderHeader-Item padding-left-50 padding-right-50">
        <div className="MarketplaceProviderHeader-OrganizationDescription">
          {organization?.shortDescription.split('\n').map((text, i) => (
            <div key={i}>{text}</div>
          ))}
        </div>
      </div>

      <div className="MarketplaceProviderHeader-Item">
        <div
          id="signInButton"
          className="MarketplaceProviderHeader-SignInButton"
          onClick={() => toggleSignInPopupOpen(!isSignInPopupOpen)}
        >
          Sign In &nbsp; {isSignInPopupOpen ? (
          <ChevronTop className="MarketplaceProviderHeader-SignInButtonIcon"/>
        ) : (
          <ChevronBottom className="MarketplaceProviderHeader-SignInButtonIcon"/>
        )}
        </div>

        <Popover
          hideArrow
          trigger="legacy"
          target="signInButton"
          placement="bottom-end"
          isOpen={isSignInPopupOpen}
          boundariesElement={document.body}
          popperClassName="PublicMarketplaceSignInPopup"
        >
          <iframe
            className="SignInFrame"
            name="simplyconnact-sign-in"
            title="Simply Connect Sign In"
            src={`${config.location.origin}${config.context}/sign-in?organizationCode=${organizationCode}&companyId=${organization?.loginCompanyId}&shouldRedirect=true`}
          ></iframe>
        </Popover>
      </div>

      {error && (
        <ErrorViewer
          isOpen
          error={error}
          onClose={() => setError(null)}
        />
      )}
    </div>
  )
}

ProviderHeader.propTypes = {
  className: PTypes.string
}

ProviderHeader.defaultProps = {}

export default memo(ProviderHeader)
