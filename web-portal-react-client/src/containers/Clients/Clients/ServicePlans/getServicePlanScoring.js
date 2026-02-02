import {
    any,
    map,
    keys,
    filter,
    sortBy,
    groupBy,
    findWhere
} from 'underscore'

import { SERVICE_PLAN_NEED_DOMAINS } from 'lib/Constants'

const { EDUCATION_TASK } = SERVICE_PLAN_NEED_DOMAINS

export default function(data, directory) {
    const {
        programTypes,
        programSubTypes,
        serviceStatuses
    } = directory

    const needs = map(data.needs, n => {
        const programType = findWhere(
            programTypes, { id: n.programTypeId }
        ) || {}

        const programSubType = findWhere(
            programSubTypes, { id: n.programSubTypeId }
        ) || {}

        const goals = map(n.goals, g => {
            const status = findWhere(
                serviceStatuses, { id: g.serviceStatusId }
            ) || {}

            return {
                ...g,
                serviceStatusName: status?.name,
                serviceStatusTitle: status?.title
            }
        })

        return {
            ...n,
            goals,
            programTypeName: programType.name,
            programTypeTitle: programType.title,
            programSubTypeName: programSubType.name,
            programSubTypeTitle: programSubType.title,
            programSubTypeZCode: programSubType.zcode,
            programSubTypeZCodeDesc: programSubType.zcodeDesc
        }
    })

    const grouped = groupBy(
        needs, need => need.domainId
    )

    let domains = filter(
        directory.domains,
        o => any(keys(grouped), k => +k === o.id)
    )

    return sortBy(
        map(domains, o => ({
            domainId: o.id,
            domainName: o.name,
            domainTitle: o.title,
            score: (
                findWhere(
                    data.scoring, { domainId: o.id }
                ) || { score: -1 }
            ).score,
            needs: sortBy(grouped[o.id], n => -n.priorityId)
        })),
        o => o.domainName === EDUCATION_TASK ? -1 : o.domainTitle
    )
}