import { shape, number, string, bool } from 'prop-types'

export const TCareTeamMember = shape({
    avatarId: number,
    avatarDataUrl: string,
    canEdit: bool,
    communityName: string,
    contactName: string,
    description: string,
    email: string,
    employeeId: number,
    id: number,
    organizationId: number,
    organizationName: string,
    phone: string,
    roleName: string
})
