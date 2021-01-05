package com.mcdar.a3dassests

import android.content.Context
import android.hardware.Camera
import android.util.AttributeSet
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import java.io.IOException


class CameraView(context: Context) : SurfaceView(context), SurfaceHolder.Callback {
    private var mHolder: SurfaceHolder? = null
    private var mCamera: Camera? = null


init {
        mCamera = getCameraInstance()
    mHolder = getHolder()
    mHolder?.addCallback(this)
    mHolder?.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS)
}
    constructor( context: Context, attrs: AttributeSet): this(context){

            mCamera = getCameraInstance()

        mHolder = getHolder()
        mHolder?.addCallback(this)
        mHolder?.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS)
    }



    fun getCameraInstance(): Camera? {
        var c: Camera? = null
        try {
            c = Camera.open()
        } catch (e: Exception) {
            print(e.printStackTrace())
        }
        return c
    }


    override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
        if (mHolder!!.surface == null) {
            return
        }

        try {
            mCamera!!.stopPreview()
        } catch (e: java.lang.Exception) {
        }
        try {
            mCamera!!.setPreviewDisplay(mHolder)
            mCamera?.startPreview()
        } catch (e: java.lang.Exception) {
            Log.d("CameraView", "Error starting camera preview: " + e.message)
        }
    }

    override fun surfaceDestroyed(holder: SurfaceHolder?) {

    }

    override fun surfaceCreated(surfaceHolder: SurfaceHolder?) {
        try {
            mCamera?.setPreviewDisplay(surfaceHolder)
            mCamera?.startPreview()
        } catch (e: IOException) {
            Log.d("CameraView", "Error setting camera preview: " + e.message)
        }
    }
}