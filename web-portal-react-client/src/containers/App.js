import React, { Suspense, useEffect } from "react";

import cn from "classnames";
import PropTypes from "prop-types";

import { QueryClient, QueryClientProvider } from "@tanstack/react-query";

import { withRouter } from "react-router-dom";

import { useSelector } from "react-redux";

import { ConnectedRouter } from "connected-react-router";

import { ToastContainer } from "react-toastify";

import { Loader } from "components";

import Routes from "routes";

import { useIsRelevantHost } from "hooks/common";

import { useExternalProviderRoleCheck, useExternalProviderUrlCheck } from "hooks/business/external";

import ErrorHandler from "containers/ErrorHandler/ErrorHandler";
import NavigationBar from "containers/NavigationBar/NavigationBar";
import Authentication from "containers/Authentication/Authentication";
import ReleaseNotificator from "containers/Help/ReleaseNotificator/ReleaseNotificator";
import HIEConsentPolicy from "containers/Policies/HealthInfoExchange/HIEConsentPolicy/HIEConsentPolicy";

import { AUTHENTICATION_EXCLUDED_PATHS } from "lib/Constants";

import { matches } from "lib/utils/UrlUtils";
import { path } from "lib/utils/ContextUtils";

import { Authentication as ConversationsAuthentication, MessageNotificator } from "containers/Communication/Messenger";

import VideoChat from "containers/Communication/VideoChat/VideoChat";

import "./App.scss";
import { ReactQueryDevtools } from "@tanstack/react-query-devtools";
import { ChatManagerProvider } from "containers/SDKChat/context/ChatManagerContext";
import InitSocket from "containers/SDKChat/ws/initSocket";
import { ChatBadgeProvider } from "containers/SDKChat/context/ChatBadgeContext";
import CallInviteModalWrapper from "containers/SDKChat/chatRight/callInviteModalWrapper";
import CallSessionPortal from "containers/SDKChat/CallSessionPortal";
import SDKTokenInitializer from "containers/SDKChat/components/SDKTokenInitializer";

const NAVIGATION_EXCLUDED_PATHS = [...AUTHENTICATION_EXCLUDED_PATHS, path("/clients/:clientId/documents/*")];

const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      staleTime: 1000 * 60 * 5,
      refetchOnWindowFocus: false,
    },
  },
});

function App({ location, history }) {
  const user = useSelector((state) => state.auth.login.user.data);

  const isRelevantHost = useIsRelevantHost();

  // useConversationsInitialization();

  const isExtProviderUrl = useExternalProviderUrlCheck();
  const isExtProviderRole = useExternalProviderRoleCheck();

  useEffect(() => {
    return function clearCache() {
      if (user) {
        queryClient.clear();
      }
    };
  }, [user]);

  return (
    <ConnectedRouter history={history}>
      <QueryClientProvider client={queryClient}>
        <ChatManagerProvider>
          <ChatBadgeProvider>
            <SDKTokenInitializer />
            <InitSocket />
            <CallInviteModalWrapper />
            <CallSessionPortal />

            <div className={"App"}>
              {isRelevantHost && NAVIGATION_EXCLUDED_PATHS.every((t) => !matches(t, location.pathname)) && (
                <NavigationBar />
              )}
              <div className={cn("App-Content", { "App-Content_loggedIn": !!user })}>
                <Suspense fallback={<Loader isCentered />}>
                  <Routes />
                </Suspense>
              </div>
              <Authentication
                shouldRedirectByFailure
                failureRedirectPath={isExtProviderUrl || isExtProviderRole ? "/external-provider/home" : "/home"}
              />
              <ConversationsAuthentication />
              <ReleaseNotificator />
              <MessageNotificator />
              <HIEConsentPolicy />
              <ToastContainer className="ToastContainer-Download" />
              <ErrorHandler />
              {user && <VideoChat />}
            </div>
          </ChatBadgeProvider>
        </ChatManagerProvider>

        <ReactQueryDevtools initialIsOpen={false} />
      </QueryClientProvider>
    </ConnectedRouter>
  );
}

App.propTypes = {
  history: PropTypes.object,
  location: PropTypes.object,
};

export default withRouter(App);
