import { lazy } from 'react';

const BuildingPublic = lazy(() => import('../../../containers/publicBuilding/publicBuilding'));


export default {
  component: BuildingPublic,
  path: '/simplyplace',
  exact: true,
}
