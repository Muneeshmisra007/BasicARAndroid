package com.mcdar.a3dassests
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Camera
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.SurfaceHolder
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.mcdar.a3dassests.R
import kotlinx.android.synthetic.main.fragment_mcd_non_a_r.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [McdNonARFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class McdNonARFragment : Fragment() {
    private val handler : Handler = Handler()
    private val runnable : Runnable= Runnable {
        val cameraView = activity?.let { CameraView(it) }
        cameraView?.let {
            val holder : SurfaceHolder  = surface_view.holder
            holder.addCallback(it)

        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_mcd_non_a_r, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        handler.postAtTime(runnable, 100)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         *
         * @return A new instance of fragment McdNonARFragment.
         */
        @JvmStatic
        fun newInstance() : McdNonARFragment {
              return  McdNonARFragment()
    }
    }

    /** Check if this device has a camera */
    private fun checkCameraHardware(context: Context): Boolean {
       return context.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA)
    }

    /** A safe way to get an instance of the Camera object. */
    fun getCameraInstance(): Camera? {
        return try {
            Camera.open() // attempt to get a Camera instance
        } catch (e: Exception) {
            // Camera is not available (in use or does not exist)
            null // returns null if camera is unavailable
        }
    }
}