import { useMemo, useState, useCallback } from 'react'

import MicRecorder from 'mic-recorder-to-mp3'

function useAudioRecorder({ onFailRecording }) {
    const [isRecording, setIsRecording] = useState(false)

    const recorder = useMemo(() => new MicRecorder({
        bitRate: 128
    }), [])

    const startRecording = useCallback(async () => {
        try {
            await recorder.start()

            setIsRecording(true)
        } catch (error) {
            onFailRecording(error)
            setIsRecording(false)
        }
    }, [onFailRecording, recorder])

    const stopRecording = useCallback(async () => {
        try {
            let [buffer, blob] = await recorder.stop().getMp3()

            const file = new File(buffer, 'voice-message.mp3', {
                type: blob.type,
                lastModified: Date.now()
            })

            return file
        } catch (error) {
            onFailRecording(error)
        } finally {
            setIsRecording(false)
        }
    }, [onFailRecording, recorder])

    return {
        isRecording,
        startRecording,
        stopRecording,
    }
}

export default useAudioRecorder
