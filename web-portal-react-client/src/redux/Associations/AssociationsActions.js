import actionTypes from './actionTypes';
import service from 'services/AssociationsService';


export function featAssociationsList (params) {
  return dispatch =>{
    service.AssociationsList(params).then(res=>{
      dispatch({ type: actionTypes.LOAD_ASSOCIATIONS_LIST, payload: res });
    })
  }
}

export function addAssociation(params) {
  return dispatch =>{
    service.AddAssociation(params).then(res=>{
      dispatch({ type: actionTypes.ADD_ASSOCIATIONS, payload: res });
    })
  }
}

export function getAssociationDetail(id) {
  return dispatch =>{
    service.FeatAssociationDetail(id).then(res=>{
      dispatch({ type: actionTypes.LOAD_ASSOCIATIONS_DETAIL, payload: res });
    })
  }
}

export function clearAssociationDetail() {
  return dispatch =>{
      dispatch({ type: actionTypes.CLEAR_ASSOCIATIONS_DETAIL, payload: [] });
  }
}

