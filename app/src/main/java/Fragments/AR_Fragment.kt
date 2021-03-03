package Fragments

import Helpers.SnackBarHelper
import android.app.ActivityManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ar4tamil.R
import com.google.ar.core.Config
import com.google.ar.core.Session
import com.google.ar.core.exceptions.CameraNotAvailableException
import com.google.ar.sceneform.ux.ArFragment

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AR_Fragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AR_Fragment  : ArFragment() {
    val min_opengl_version : Double = 2.0


    override fun onAttach(context: Context) {
        super.onAttach(context)
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.N){
            SnackBarHelper.getInstance().showError(
                activity!!,
                "Sceneform requires Android N or later"
            )
        }

        val openGlVersionString :String = (context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager).deviceConfigurationInfo.glEsVersion

        if(openGlVersionString.toDouble() < min_opengl_version){
            SnackBarHelper.getInstance().showError(
                activity!!,
                "Sceneform requires OpenGL ES 2.0 or later"
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
     val view =  super.onCreateView(inflater, container, savedInstanceState)
        // Inflate the layout for this fragment
        planeDiscoveryController.show()
       // arSceneView.planeRenderer.isEnabled = true;
        return view
    }

    override fun getSessionConfiguration(session: Session): Config? {
        val config = super.getSessionConfiguration(session)
        config.planeFindingMode = Config.PlaneFindingMode.HORIZONTAL
        config.focusMode = Config.FocusMode.AUTO
        config.lightEstimationMode = Config.LightEstimationMode.ENVIRONMENTAL_HDR;
        session.configure(config)
        return config
    }
    override fun onPause() {
        super.onPause()
        arSceneView.pause()
    }

    override fun onResume() {
        super.onResume()
        try {
            arSceneView.resume()
        } catch (e: CameraNotAvailableException) {
            e.printStackTrace()
        }
    }
}
