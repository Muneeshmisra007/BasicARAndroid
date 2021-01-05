package com.mcdar.a3dassests.helper

import android.app.ActivityManager
import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.view.MotionEvent
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.google.ar.core.Anchor
import com.google.ar.core.ArCoreApk
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.HitTestResult
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.SkeletonNode
import com.google.ar.sceneform.animation.ModelAnimator
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.rendering.Texture
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode
import com.mcdar.a3dassests.McdARFragment

class ARHelper {

    companion object {


        fun add3dModelWithTexture(
            arFragment: McdARFragment, anchor: Anchor, model: Uri, res: Int,
            context: Context, applyAnimation: Boolean
        ) {


            Texture.builder().setSource(BitmapFactory.decodeResource(context.resources, res))
                .build()
                .thenAccept {
                    if (applyAnimation) {
                        addTexturedNodeWithAimation(
                            arFragment,
                            anchor,
                            model,
                            it,
                            context
                        )
                    } else {
                        addTexturedNode(arFragment, anchor, model, it, context)
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
            texture: Texture,
            context: Context
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
                    skeletonNode.localScale = Vector3(0.2f, 0.2f, 0.2f)
                    skeletonNode.setParent(anchorNode)
                    skeletonNode.renderable = it
                    it.material.setTexture("baseColor", texture)
                    //anchorNode.localRotation = Quaternion.multiply(anchorNode.localRotation, Quaternion(Vector3.up(),180f))
                    // skeletonNode.setLocalRotation(Quaternion.axisAngle(Vector3(1f, 0f, 0f), 90f))
                    fragment.arSceneView.scene.addChild(anchorNode)
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
            context: Context
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
        context: Context
    ) {
        ModelRenderable.builder()
            .setSource(fragment.context, model)
            .build()
            .thenAccept {
                addNodeToScene(fragment, anchor, it)
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
    private fun addNodeToScene(fragment: ArFragment, anchor: Anchor, renderable: ModelRenderable) {
        val anchorNode = AnchorNode(anchor)
        // TransformableNode means the user to move, scale and rotate the model
        val transformableNode = TransformableNode(fragment.transformationSystem)
        transformableNode.scaleController.minScale = 0.1999f;
        transformableNode.scaleController.maxScale = 0.2000f
        transformableNode.scaleController.isEnabled = true
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

}