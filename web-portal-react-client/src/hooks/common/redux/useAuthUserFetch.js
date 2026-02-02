import { useSelector } from 'react-redux'

import * as userActions from 'redux/auth/user/userActions'

import {
    useBoundActions
} from './'

function useUpdateAuthUserFetch() {
  const fetch = useBoundActions(userActions).updateAuthUser

  const { isFetching, error } = useSelector(state => state.auth.user)

  const data = useSelector(state => state.auth.login.user.data)

  return {
    data,
    fetch,
    error,
    isFetching
  }
}

export default useUpdateAuthUserFetch