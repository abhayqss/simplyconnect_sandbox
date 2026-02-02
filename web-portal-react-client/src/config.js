const uuid = require("uuid");

module.exports = {
  port: process.env.REACT_APP_PORT,

  context: process.env.REACT_APP_HOMEPAGE,

  log: {
    level: "info",
    output: "file",
  },

  tracking: {
    provider: "sentry",
  },

  httpLimit: "10mb",

  demoUser: {
    id: 745,
    isActive: true,
    companyId: "demo",
    username: "ntyler",
    password: "123",
  },

  demoUsers: [
    {
      id: 1,
      isActive: true,
      companyId: "demo",
      username: "ntyler",
      password: "123",
    },
    {
      id: 2,
      isActive: true,
      companyId: "demo",
      username: "cpatnode",
      password: "111",
    },
  ],

  session: {
    // A secret key used as a salt to hash the session and sign the cookies.
    key: uuid.v4(),
    maxAge: 24 * 60 * 60 * 1000,
  },

  location: {
    // Application location and environment configuration
    host: process.env.REACT_APP_LOCATION_HOST,
    domain: process.env.REACT_APP_LOCATION_DOMAIN,
    origin: process.env.REACT_APP_LOCATION_ORIGIN,
    protocol: process.env.REACT_APP_LOCATION_PROTOCOL,
    hostname: process.env.REACT_APP_LOCATION_HOST_NAME,
  },

  environment: process.env.NODE_ENV,
  // Remote server address configuration
  remote: {
    isEnabled: true,
    url: process.env.REACT_APP_REMOTE_SERVER_URL,
  },

  google: {
    maps: {
      apiKey: process.env.REACT_APP_GOOGLE_MAPS_API_KEY,
    },
  },

  sentry: {
    environment: process.env.REACT_APP_SENTRY_ENVIRONMENT,
  },

  firebaseConfig: {
    apiKey: process.env.REACT_APP_FIREBASE_API_KEY,
    authDomain: process.env.REACT_APP_FIREBASE_AUTH_DOMAIN,
    projectId: process.env.REACT_APP_FIREBASE_PROJECTID,
    storageBucket: process.env.REACT_APP_FIREBASE_STORAGE_BUCKET,
    messagingSenderId: process.env.REACT_APP_FIREBASE_MESSAGING_SENDERID,
    appId: process.env.REACT_APP_FIREBASE_APPID,
    measurementId: process.env.REACT_APP_FIREBASE_MEASUREMENTID,
  },

  responseTimeout: 130000,

  conversations: {
    provider: "twilio",
  },
};
