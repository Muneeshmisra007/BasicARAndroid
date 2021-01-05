package com.mcdar.a3dassests

import android.graphics.Point
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.ar.core.Anchor
import com.google.ar.core.HitResult
import com.google.ar.core.Plane
import com.google.ar.core.TrackingState
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.HitTestResult
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode

class McdARFragment: ArFragment() {

    private var isTracking: Boolean = false
    private var isHitting: Boolean = false
    private var isObjectAdded = false
    public var listener: McdARListener? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return super.onCreateView(inflater, container, savedInstanceState)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listener?.onArReady(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        listener = null
    }

    companion object {
        @JvmStatic
        public fun newInstance(): McdARFragment {
            return McdARFragment()
        }
    }

}



interface McdARListener{
    fun onArReady( fragment: McdARFragment)
}