package com.mcdar.a3dassests.helper

import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.view.MotionEvent
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.google.ar.core.Anchor
import com.google.ar.sceneform.*
import com.google.ar.sceneform.animation.ModelAnimator
import com.google.ar.sceneform.assets.RenderableSource
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.Material
import com.google.ar.sceneform.rendering.MaterialFactory
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.rendering.Texture
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode
import com.mcdar.a3dassests.ARTexture
import com.mcdar.a3dassests.McdARFragment
import com.mcdar.a3dassests.ObjectClickListener

class ARHelper {

    companion object {


        fun add3dModelWithTexture(
            arFragment: McdARFragment, anchor: Anchor, model: Uri, res: Int,
            context: Context, applyAnimation: Boolean, listener: ObjectClickListener,
        arTexture: ARTexture) {


          //  Texture.builder().setSource(BitmapFactory.decodeResource(context.resources, res))
            Texture.builder().setSource(context, arTexture.texture)
//                .setSampler(Texture.Sampler.builder()
//                    .setMagFilter(Texture.Sampler.MagFilter.LINEAR)
//                    .setMinFilter(Texture.Sampler.MinFilter.LINEAR_MIPMAP_LINEAR)
//                    .build())
                .build()
                .thenAccept {
                    MaterialFactory.makeOpaqueWithTexture(context, it)
                        .thenAccept {
                            if (applyAnimation) {
//                                addTexturedNodeWithAimation(
//                                    arFragment,
//                                    anchor,
//                                    model,
//                                    it,
//                                    context,
//                                    listener
//                                )
                            } else {
                                addTexturedNode(arFragment, anchor, model, it, context, listener, arTexture)
                                //addTransferableNode(arFragment,anchor,model, context,it)
                            }
                        }

                }
                .exceptionally {
                    //TODO handle later this use case
                    Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show()
                    return@exceptionally null
                }

        }

        private fun addTexturedNode(
            fragment: ArFragment,
            anchor: Anchor,
            model: Uri,
            texture: Material,
            context: Context,
            listener: ObjectClickListener,
            arTexture: ARTexture
        ) {

            ModelRenderable.builder()
                .setSource(fragment.context, model)
//                .setSource( fragment.context, RenderableSource.builder().
//                        setSource(
//
//                        fragment.context, Uri.parse(URL_MODEL), RenderableSource.SourceType.GLTF2)
//                        .setRecenterMode(RenderableSource.RecenterMode.CENTER)
//                        .build()
//                ).setRegistryId(URL_MODEL)
                .build()
                .thenAccept {
                    println("$texture + rrrrrrr")
                    val anchorNode = AnchorNode(anchor)
                    val skeletonNode = SkeletonNode()
                    skeletonNode.localScale = Vector3(arTexture.size, arTexture.size, arTexture.size)
                    skeletonNode.setParent(anchorNode)
                    skeletonNode.renderable = it
                    skeletonNode.setOnTapListener { hitTestResult: HitTestResult, motionEvent: MotionEvent ->
                        listener.onClick()
                    }
                    anchorNode.setOnTapListener { hitTestResult: HitTestResult, motionEvent: MotionEvent ->
                        listener.onClick()
                    }
                    texture.setFloat("metallic", arTexture.metallicity)
                    texture.setFloat("roughness", arTexture.roughness)
                    texture.setFloat("interpolatedColor", arTexture.interpolar)

                    //texture.setFloat("metallicMap", 0.9f)
                    it.material = texture
                    //anchorNode.localRotation = Quaternion.multiply(anchorNode.localRotation, Quaternion(Vector3.up(),180f))
                    // skeletonNode.setLocalRotation(Quaternion.axisAngle(Vector3(1f, 0f, 0f), 90f))
                    fragment.arSceneView.scene.addChild(anchorNode)
                    anchorNode.addLifecycleListener(object: Node.LifecycleListener{
                        override fun onDeactivated(p0: Node?) {
                            println("onDeactivated11111${p0.toString()}")
                        }

                        override fun onActivated(p0: Node?) {
                            println("onActivated11111${p0.toString()}")
                        }

                        override fun onUpdated(p0: Node?, p1: FrameTime?) {
                            //println("onUpdated11111${p0.toString()}")



                        }

                    })
                    listener.onObjectAdded()
                    RenderableSource.RecenterMode.CENTER
//                val count: Int = it.animationDataCount
//                print("lookAtAnim1" + count)
//                if (count > 0 && (modelAnimator == null || !modelAnimator!!.isRunning)) {
//                    print("lookAtAnim2" + true)
//                    val modelData = it.getAnimationData(0)
//                    modelAnimator = ModelAnimator(modelData, it)
//                    print("lookAtAnim3" + modelAnimator)
//                    modelAnimator?.start()
//                }

                }
                .exceptionally {
                    //TODO handle later this use case
                    Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show()
                    return@exceptionally null
                }
        }

        //required bfx model
        private fun addTexturedNodeWithAimation(
            fragment: ArFragment,
            anchor: Anchor,
            model: Uri,
            texture: Texture,
            context: Context,
            listener: ObjectClickListener
        ) {

            ModelRenderable.builder()
                .setSource(fragment.context, model)
                .build()
                .thenAccept {
                    println("$texture + rrrrrrr")
                    val anchorNode = AnchorNode(anchor)
                    val skeletonNode = SkeletonNode()
                    skeletonNode.localScale = Vector3(0.2f, 0.2f, 0.2f)
                    skeletonNode.setParent(anchorNode)
                    skeletonNode.renderable = it
                    skeletonNode.setOnTapListener { hitTestResult: HitTestResult, motionEvent: MotionEvent ->
                        listener.onClick()
                    }
                    anchorNode.setOnTapListener { hitTestResult: HitTestResult, motionEvent: MotionEvent ->
                        listener.onClick()
                    }


                    it.material.setTexture("baseColor", texture)


                    //anchorNode.localRotation = Quaternion.multiply(anchorNode.localRotation, Quaternion(Vector3.up(),180f))
                    // skeletonNode.setLocalRotation(Quaternion.axisAngle(Vector3(1f, 0f, 0f), 90f))
                    fragment.arSceneView.scene.addChild(anchorNode)
                    val count: Int = it.animationDataCount
                    print("lookAtAnim1" + count)
                    if (count > 0) {
                        print("lookAtAnim2" + true)
                        val modelData = it.getAnimationData(0)
                        val modelAnimator = ModelAnimator(modelData, it)
                        print("lookAtAnim3" + modelAnimator)
                        modelAnimator?.start()
                    }

                }
                .exceptionally {
                    //TODO handle later this use case
                    Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show()
                    return@exceptionally null
                }
        }


        /**
         * @param fragment our fragment
         * @param anchor ARCore anchor from the hit test
         * @param model our 3D model of choice
         *
         * Uses the ARCore anchor from the hitTest result and builds the Sceneform nodes.
         * It starts the asynchronous loading of the 3D model using the ModelRenderable builder.
         */
        @RequiresApi(Build.VERSION_CODES.N)
        private fun addTransferableNode(
            fragment: ArFragment,
            anchor: Anchor,
            model: Uri,
            context: Context,
            material: Material
        ) {
            ModelRenderable.builder()
                .setSource(fragment.context, model)
                .build()
                .thenAccept {
                    addNodeToScene(fragment, anchor, it, material)
                }
                .exceptionally {
                    //TODO handle later this use case
                    Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show()
                    return@exceptionally null
                }
        }


        /**
         * @param fragment our fragment
         * @param anchor ARCore anchor
         * @param renderable our model created as a Sceneform Renderable
         *
         * This method builds two nodes and attaches them to our scene
         * The Anchor nodes is positioned based on the pose of an ARCore Anchor. They stay positioned in the sample place relative to the real world.
         * The Transformable node is our Model
         * Once the nodes are connected we select the TransformableNode so it is available for interactions
         */
        private fun addNodeToScene(
            fragment: ArFragment,
            anchor: Anchor,
            renderable: ModelRenderable,
            material: Material
        ) {
            val anchorNode = AnchorNode(anchor)
            // TransformableNode means the user to move, scale and rotate the model
            val transformableNode = TransformableNode(fragment.transformationSystem)
            transformableNode.scaleController.minScale = 0.1999f;
            transformableNode.scaleController.maxScale = 0.2000f
            transformableNode.scaleController.isEnabled = true
            renderable.material = material
            transformableNode.renderable = renderable
            transformableNode.setParent(anchorNode)

//        anchorNode.setOnTapListener(Node.OnTapListener() { hitTestResult: HitTestResult, motionEvent: MotionEvent ->
//            println("tappedArrrrr")
//        })
            transformableNode.setOnTapListener(Node.OnTapListener() { hitTestResult: HitTestResult, motionEvent: MotionEvent ->
                println("tappedArrrrr")
            })
            fragment.arSceneView.scene.addChild(anchorNode)
            transformableNode.select()

        }

        fun checkCameraPermission(context: Context) : Boolean{
            return ContextCompat.checkSelfPermission(
                context, android.Manifest.permission.CAMERA) ==
                    PackageManager.PERMISSION_GRANTED
        }



    }


}
fun com.google.ar.sceneform.Camera.isWorldPositionVisible(worldPosition: Vector3): Boolean {
    val var2 = com.google.ar.sceneform.math.Matrix()
    com.google.ar.sceneform.math.Matrix.multiply(projectionMatrix, viewMatrix, var2)
    val var5: Float = worldPosition.x
    val var6: Float = worldPosition.y
    val var7: Float = worldPosition.z
    val var8 = var5 * var2.data[3] + var6 * var2.data[7] + var7 * var2.data[11] + 1.0f * var2.data[15]
    if (var8 < 0f) {
        return false
    }
    val var9 = Vector3()
    var9.x = var5 * var2.data[0] + var6 * var2.data[4] + var7 * var2.data[8] + 1.0f * var2.data[12]
    var9.x = var9.x / var8
    if (var9.x !in -1f..1f) {
        return false
    }

    var9.y = var5 * var2.data[1] + var6 * var2.data[5] + var7 * var2.data[9] + 1.0f * var2.data[13]
    var9.y = var9.y / var8
    return var9.y in -1f..1f
}