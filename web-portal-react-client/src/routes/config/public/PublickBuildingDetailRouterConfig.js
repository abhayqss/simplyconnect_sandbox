import { lazy } from 'react';

const BuildingPublicDetail = lazy(() => import('../../../containers/publicBuilding/publicBuildingDetail'));


export default {
  component: BuildingPublicDetail,
  path: '/simplyplace/buildingdetail/:id',
  exact: true,
}
