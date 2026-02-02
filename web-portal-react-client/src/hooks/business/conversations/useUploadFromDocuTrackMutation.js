import { useMutation } from '@tanstack/react-query'

import { ConversationService } from 'factories'

const service = ConversationService()

export default function useUploadFromDocuTrackMutation(options) {
    return useMutation(data => service.uploadFromDocuTrack(data), options)
}