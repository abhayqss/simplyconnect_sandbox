import { useState, useEffect, useCallback } from 'react'

export default function useQueryPermissions() {
    let [audioPermissionState, setAudioPermissionState] = useState(false)

    useEffect(() => {
        let permissionStatus = null

        function onPermissionChange(event) {
            setAudioPermissionState(event.target.state)
        }

        navigator.permissions.query({ name: 'microphone' }).then(ps => {
            ps.onchange = onPermissionChange

            permissionStatus = ps

            setAudioPermissionState(ps.state)
        })

        return function cleanup() {
            permissionStatus.onchange = null
        }
    }, [])

    const requestAudioPermissions = useCallback(async () => {
        let stream = await navigator.mediaDevices.getUserMedia({ audio: true })

        stream.getTracks().forEach(track => track.stop())
    }, [])

    return {
        requestAudioPermissions,
        hasAudioPermissions: audioPermissionState === 'granted',
    }
}