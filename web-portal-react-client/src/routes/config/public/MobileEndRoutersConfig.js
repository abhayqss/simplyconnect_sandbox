import { lazy } from 'react';

const AfterCodeScanning = lazy(() => import('containers/AfterCodeScanning/AfterCodeScanning'));

export default {
  component: AfterCodeScanning,
  path: '/associate/:id/:type',
  exact: true
}
