import { lazy } from 'react';

const AssociateLoginSuccess = lazy(() => import('containers/AfterCodeScanning/associateLogin/AssociateLoginSuccess'));

export default {
  component: AssociateLoginSuccess,
  path: '/associate/:id/:type/login/success',
  exact: true,
}
