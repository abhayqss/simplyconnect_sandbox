import { useMutation } from '@tanstack/react-query'

import service from 'services/SupportTicketsService'

function useSupportTicketSubmit(options) {
    return useMutation(data => service.save(data), options)
}

export default useSupportTicketSubmit
