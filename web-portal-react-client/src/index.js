import { hot } from "react-hot-loader/root";
import { setConfig } from "react-hot-loader";

import React, { Suspense, lazy } from "react";

import { createRoot } from "react-dom/client";
import { Provider } from "react-redux";
import { createBrowserHistory } from "history";
import { ConnectedRouter } from "connected-react-router";

import { CookiesProvider } from "react-cookie";

import { firebaseConfig } from "./config";

import Loader from "components/Loader/Loader";

import configureStore from "redux/configureStore";

import ConversationService from "factories/ConversationService";
import BaseSchemeValidator from "validators/BaseSchemeValidator";

import * as serviceWorker from "./serviceWorker";

import "./index.scss";

/*import * as Sentry from "@sentry/react";

Sentry.init({
  dsn: "https://0f734602e766b30ff732c0c61440f688@sentry-sc.micmd.com:19001/9",
  integrations: [Sentry.browserTracingIntegration(), Sentry.replayIntegration()],
  // Performance Monitoring
  tracesSampleRate: 1.0, //  Capture 100% of the transactions
  // Set 'tracePropagationTargets' to control for which URLs distributed tracing should be enabled
  tracePropagationTargets: ["localhost", /^https:\/\/yourserver\.io\/api/],
  // Session Replay
  replaysSessionSampleRate: 0.1, // This sets the sample rate at 10%. You may want to change it to 100% while in development and then sample at a lower rate in production.
  replaysOnErrorSampleRate: 1.0, // If you're not already sampling the entire session, change the sample rate to 100% when sampling sessions where errors occur.
});*/

setConfig({ showReactDomPatchNotification: false });

const history = createBrowserHistory();

const store = configureStore(history);

const conversationService = ConversationService();

BaseSchemeValidator.setup();

// Import the functions you need from the SDKs you need
import { initializeApp } from "firebase/app";
import { getAnalytics, logEvent } from "firebase/analytics";
//  Add SDKs for Firebase products that you want to use
// https://firebase.google.com/docs/web/setup#available-libraries

// Your web app's Firebase configuration
// For Firebase JS SDK v7.20.0 and later, measurementId is optional
// Initialize Firebase
const app = initializeApp(firebaseConfig);
const analytics = getAnalytics(app);

const render = () => {
  const App = hot(lazy(() => import("containers/App")));

  const container = document.getElementById("root");

  const root = createRoot(container);

  logEvent(analytics, "notification_received");

  return root.render(
    <Provider store={store}>
      <ConnectedRouter history={history}>
        <CookiesProvider>
          <Suspense fallback={<Loader isCentered />}>
            <App history={history} />
          </Suspense>
        </CookiesProvider>
      </ConnectedRouter>
    </Provider>,
  );
};

/*if (process.env.NODE_ENV !== 'production' && module.hot) {
    module.hot.accept('./containers/App', () => {
        setTimeout(render)
        conversationService.shutdown()
    })
}*/

render();

// If you want your app to work offline and load faster, you can change
// unregister() to register() below. Note this comes with some pitfalls.
// Learn more about service workers: http://bit.ly/CRA-PWA
serviceWorker.unregister();
