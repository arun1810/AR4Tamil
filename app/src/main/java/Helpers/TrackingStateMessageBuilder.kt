package Helpers

import com.google.ar.core.Camera
import com.google.ar.core.TrackingFailureReason

object TrackingStateMessageBuilder {
    private const val NoFeatures = "Cant find Surface. Aim camera at surface with more color "
    private const val TO_Much_Motion = "Moving to fast. Slow down"
    private const val Low_Light = "Too dark. Aim camera with more light"
    private const val Bad_State = "Someting went wrong. Try restarting the app"
    private const  val Camera_Unavailable =
        "Another app is using the camera. Tap on this app or try closing the other one."

    fun getMessage(camera: Camera): String {
        val trackingFailureReason = camera.trackingFailureReason
        return when (trackingFailureReason) {
            TrackingFailureReason.NONE -> ""
            TrackingFailureReason.BAD_STATE -> Bad_State
            TrackingFailureReason.EXCESSIVE_MOTION -> TO_Much_Motion
            TrackingFailureReason.CAMERA_UNAVAILABLE -> Camera_Unavailable
            TrackingFailureReason.INSUFFICIENT_LIGHT -> Low_Light
            TrackingFailureReason.INSUFFICIENT_FEATURES -> NoFeatures
           else ->  "Unknown error occured $trackingFailureReason"
        }

    }
}