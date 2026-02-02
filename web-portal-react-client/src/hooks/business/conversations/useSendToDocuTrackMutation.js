import { useMutation } from '@tanstack/react-query'

import { ConversationService } from 'factories'

const service = ConversationService()

export default function useSendToDocuTrackMutation(options) {
    return useMutation(data => service.sendToDocuTrack(data), options)
}