package com.mcdar.a3dassests

import android.app.ActivityManager
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Point
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.ar.core.*
import kotlinx.android.synthetic.main.mcd_ar_activity.*
import com.mcdar.a3dassests.helper.ARHelper
import java.text.DecimalFormat


class MainActivity : AppCompatActivity(), McdARListener, ObjectClickListener {

    private lateinit var session: Session
    private var isTracking: Boolean = false
    private var isHitting: Boolean = false
    private var isAdded = false
    private val TIMER_FORMAT = "%s : %s"
    private var countDownTimer: CountDownTimer? = null
    private val FILE_NAME = "mcdonalds_easter_demo_bigmac.sfb"
    private val REQ_CODE = 450065;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.mcd_ar_activity)
        checkAndLoadARScreen()
    }

    override fun onResume() {
        super.onResume()

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            REQ_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    loadARView()
                } else {
                    startNonARScreen()
                }
            }
            else -> {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            }
        }
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
                    ARHelper.add3dModelWithTexture(
                        arFragment,
                        hit.createAnchor(),
                        model,
                        R.drawable.golden_texture,
                        this,
                        false,
                        this
                    )
                    break
                }
            }
        }
    }

    private fun startARScreen() {
        if (ARHelper.checkCameraPermission(this)) {
            loadARView()
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(arrayOf(android.Manifest.permission.CAMERA), REQ_CODE)
            }
        }
    }

    private fun loadARView() {
        val fragment: McdARFragment = McdARFragment.newInstance()
        fragment.listener = this
        supportFragmentManager.beginTransaction().replace(getContainerId(), fragment)
            .commitAllowingStateLoss()
        showTimer(30)
    }

    private fun startNonARScreen() {
    }

    fun getContainerId(): Int {
        return R.id.containerId;
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onArReady(fragment: McdARFragment) {

//         Adds a listener to the ARSceneView
//         Called before processing each frame
        fragment.planeDiscoveryController.hide()
        fragment.planeDiscoveryController.setInstructionView(null)
        fragment.arSceneView.planeRenderer.isVisible = false
        fragment.arSceneView.scene.addOnUpdateListener { frameTime ->
            fragment.onUpdate(frameTime)
            onUpdate(fragment)
        }
    }

    private fun checkAndLoadARScreen() {

        val openGlVersionString: String =
            (getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager)
                .deviceConfigurationInfo
                .glEsVersion

        if (openGlVersionString.toDouble() < 3.0 || android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            startNonARScreen()
            return
        }
        val availability = ArCoreApk.getInstance().checkAvailability(this)
        when {
            availability.isTransient -> {
                Handler().postDelayed(Runnable { checkAndLoadARScreen() }, 200)
            }
            availability.isSupported -> {
                startARScreen()
            }
            else -> {
                //start no ar screen
                startNonARScreen()
            }
        }
    }

    private fun showTimer(seconds: Int) {
        if (seconds == 0) {
            return
        }

        countDownTimer = object : CountDownTimer((seconds * 1000).toLong(), 1000) {

            override fun onFinish() {

                //TODO start non ar flow screen
            }

            override fun onTick(millisUntilFinished: Long) {
                val secondsLeft = millisUntilFinished / 1000
                var decimalFormat = DecimalFormat("00")

                timer.text =
                    String.format(
                        TIMER_FORMAT,
                        decimalFormat.format(secondsLeft / 60),
                        decimalFormat.format(secondsLeft % 60).toString()
                    )
            }
        }
        countDownTimer?.start()
    }

    override fun onClick() {
        print("clicked...")
        Toast.makeText(this, "clicked...", Toast.LENGTH_SHORT)
    }

    fun arObjectFound() {
//        viewModel.foundARObject()
    }

    override fun onDestroy() {
        super.onDestroy()
        countDownTimer?.cancel()
    }
}
