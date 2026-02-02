import { useQuery } from 'hooks/common/redux'

import actions from 'redux/directory/referral/category/list/referralCategoryListActions'

export default function useReferralCategoriesQuery(options) {
    useQuery(actions, null, options)
}
