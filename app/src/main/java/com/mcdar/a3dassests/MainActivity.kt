package com.mcdar.a3dassests

import android.app.ActivityManager
import android.content.Context
import android.graphics.Point
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.ar.core.*
import kotlinx.android.synthetic.main.mcd_ar_activity.*
import com.mcdar.a3dassests.helper.ARHelper


class MainActivity : AppCompatActivity(), McdARListener {

    // private lateinit var arFragment: ArFragment

    private var isTracking: Boolean = false
    private var isHitting: Boolean = false
    private var isAdded = false
    private val FILE_NAME = "mcdonalds_easter_demo_mcfluryy.sfb"
    //    private val URL_MODEL = "https://mcdmobileappdev.blob.core.windows.net/img/arAndroid/cangrejo.fbx"
    private val URL_MODEL =
        "https://mcdmobileappdev.blob.core.windows.net/img/arAndroid/mungiltf/model.gltf"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.mcd_ar_activity)

    }


    override fun onResume() {
        super.onResume()
        isARCoreAvailable()
    }


    // Updates the tracking state
    @RequiresApi(Build.VERSION_CODES.N)
    private fun onUpdate(arFragment: McdARFragment) {
        updateTracking(arFragment)
        // Check if the devices gaze is hitting a plane detected by ARCore
        if (isTracking) {
            val hitTestChanged = updateHitTest(arFragment)
            if (hitTestChanged) {
                // showFab(isHitting)
                if (!isAdded) {
                    addObject(Uri.parse(FILE_NAME), arFragment)
                    isAdded = true;
                }
            }
        }
    }

    // Performs frame.HitTest and returns if a hit is detected
    private fun updateHitTest(arFragment: McdARFragment): Boolean {
        val frame = arFragment.arSceneView.arFrame
        val point = getScreenCenter()
        val hits: List<HitResult>
        val wasHitting = isHitting
        isHitting = false
        if (frame != null) {
            hits = frame.hitTest(point.x.toFloat(), point.y.toFloat())
            for (hit in hits) {
                val trackable = hit.trackable
                if (trackable is Plane && trackable.isPoseInPolygon(hit.hitPose)) {
                    isHitting = true
                    break
                }
            }
        }
        return wasHitting != isHitting
    }

    // Makes use of ARCore's camera state and returns true if the tracking state has changed
    private fun updateTracking(arFragment: McdARFragment): Boolean {
        val frame = arFragment.arSceneView.arFrame
        val wasTracking = isTracking
        isTracking = frame?.camera?.trackingState == TrackingState.TRACKING
        return isTracking != wasTracking
    }

    // Simply returns the center of the screen
    private fun getScreenCenter(): Point {
        val view = findViewById<View>(android.R.id.content)
        return Point(view.width / 2, view.height / 2)
    }

    /**
     * @param model The Uri of our 3D sfb file
     *
     * This method takes in our 3D model and performs a hit test to determine where to place it
     */
    @RequiresApi(Build.VERSION_CODES.N)
    private fun addObject(model: Uri, arFragment: McdARFragment) {
        val frame = arFragment.arSceneView.arFrame
        val point = getScreenCenter()
        if (frame != null) {
            val hits = frame.hitTest(point.x.toFloat(), point.y.toFloat())
            for (hit in hits) {
                val trackable = hit.trackable
                if (trackable is Plane && trackable.isPoseInPolygon(hit.hitPose)) {
                    //placeObject(arFragment, hit.createAnchor(), model)
                    //ARHelper.addTexturedNode(arFragment, hit.createAnchor(), model)
                    ARHelper.add3dModelWithTexture(arFragment, hit.createAnchor(), model, R.drawable.pommes, this, false)
                    break
                }
            }
        }
    }







    private fun startARScreen() {
        val fragment: McdARFragment = McdARFragment.newInstance()
        fragment.listener = this
        supportFragmentManager.beginTransaction().replace(getContainerId(), fragment)
            .commitAllowingStateLoss()
    }

    private fun startNonARScreen() {
        val fragment: McdNonARFragment = McdNonARFragment.newInstance()
        supportFragmentManager.beginTransaction().replace(getContainerId(), fragment)
            .commitAllowingStateLoss()
    }

    public fun getContainerId(): Int {
        return R.id.containerId;
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onArReady(arFragment: McdARFragment) {

//         Adds a listener to the ARSceneView
//         Called before processing each frame
        arFragment.planeDiscoveryController.hide()
        arFragment.planeDiscoveryController.setInstructionView(null)
        arFragment.arSceneView.planeRenderer.isVisible = false
        arFragment.arSceneView.scene.addOnUpdateListener { frameTime ->
            arFragment.onUpdate(frameTime)
            onUpdate(arFragment)
        }



        // Set the onclick lister for our button
        // Change this string to point to the .sfb file of your choice :)
        floatingActionButton.setOnClickListener { addObject(Uri.parse(FILE_NAME), arFragment) }

//        if (!isAdded) {
//            addObject(Uri.parse(FILE_NAME), arFragment)
//            isAdded = true;
//        }

    }

    private fun isARCoreAvailable() {
        val availability = ArCoreApk.getInstance().checkAvailability(this)
        if (availability.isTransient) {
            isARCoreAvailable()
        }

        val openGlVersionString: String = (getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager)
            .deviceConfigurationInfo
            .glEsVersion
        if (openGlVersionString.toDouble() >= 3.0) {

            startNonARScreen()
        }

        if (availability.isSupported) {
            startARScreen()
        } else {
            //start no ar screen
            startNonARScreen()
        }
    }
}
