package com.ar4tamil

import Adapters.assestAdapter
import CustomNodes.LetterNode
import DataClasses.letterAndasset
import Helpers.SnackBarHelper
import Helpers.TrackingStateMessageBuilder
import Listeners.RecyclerClickListener
import Listeners.RecyclerTouchListener
import android.graphics.Point
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.ar.core.ArCoreApk
import com.google.ar.core.Plane
import com.google.ar.core.TrackingState
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode
import java.util.*
import kotlin.collections.ArrayList

class ArActivity : AppCompatActivity() {

    private lateinit var Speech:TextToSpeech
    private lateinit var arFragment:ArFragment
    private val snackbar: SnackBarHelper = SnackBarHelper.getInstance()
    private lateinit var recyclerview:RecyclerView
    private lateinit var adapter:assestAdapter
    private lateinit var data:ArrayList<letterAndasset>
    private  lateinit  var currentModel:letterAndasset
    private var objectRequried :Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        }else {
            window.setFlags(
                    WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        data = ArrayList()

        data.add(letterAndasset("அ", "அணிலஂ", Uri.parse("A.sfb"), Uri.parse("squirrel.sfb")))
        data.add(letterAndasset("ஆ", "ஆடூ", Uri.parse("Aa.sfb"), Uri.parse("goat.sfb")))
        data.add(letterAndasset("இ", " ", null, null))
        data.add(letterAndasset("ஈ", " ", null, null))
        data.add(letterAndasset("உ", " ", null, null))
        data.add(letterAndasset("ஊ", " ", null, null))

        adapter= assestAdapter(data, R.layout.adapter_layout, this)
        recyclerview = findViewById(R.id.recyclerview)
        recyclerview.bringToFront()
        recyclerview.layoutManager=LinearLayoutManager(applicationContext, LinearLayoutManager.HORIZONTAL, false)
        recyclerview.adapter = adapter
        CheckARAvailability()
        arFragment = supportFragmentManager.findFragmentById(R.id.ar_fragment) as ArFragment
        arFragment.arSceneView.scene.addOnUpdateListener { onUpdateFrame() }



        recyclerview.addOnItemTouchListener(object : RecyclerTouchListener(this, recyclerview, object : RecyclerClickListener {
            override fun onClick(view: View?, position: Int) {
                data[position].asset.let {
                    if (it == null) {
                        Toast.makeText(this@ArActivity, " Model not yet added ", Toast.LENGTH_LONG).show()
                    } else {
                        currentModel = data[position]
                        objectRequried = true;
                    }
                }


            }

            override fun onLongClick(view: View?, position: Int) {

            }

        }) {

        })



         Speech= TextToSpeech(applicationContext) { status: Int ->
            if (status != TextToSpeech.ERROR) {
                val locales = Locale.getAvailableLocales()
                for(local in locales){
                    if(local.displayName=="Tamil (India)"){
                        Speech.language=local
                    }
                }
               // Speech.language= Locale()
               Speech.setPitch(1.2f);
                //Speech.setSpeechRate(0.9f);
            }
        }
    }
    private fun CheckARAvailability(){
       val availability  = ArCoreApk.getInstance().checkAvailability(this)
        if(availability.isTransient){
            Handler(Looper.getMainLooper()).postDelayed({
                CheckARAvailability();
            }, 200)
        }
        if(availability.isSupported){
            Toast.makeText(this, " AR Supported ", Toast.LENGTH_LONG).show()
        }
        else {
            Toast.makeText(this, " AR Not-Supported ", Toast.LENGTH_SHORT).show()
        }
    }

    private fun onUpdateFrame() {
        val frame = arFragment.arSceneView.arFrame ?: return
        val camera = frame.camera
        if (camera.trackingState == TrackingState.PAUSED) {
            val message: String = TrackingStateMessageBuilder.getMessage(camera)
            snackbar.showmessage(this, message)
            return
        }
        try {
            if (HasPlane()) {
                snackbar.hide(this)
                if (objectRequried) {
                    val pnt = getScreenCenter()
                    val hittest = frame.hitTest(pnt!!.x.toFloat(), (pnt.y.toFloat())).iterator()

                    val anchor = hittest.next().createAnchor()
                    val anchorNode = AnchorNode(anchor)
                    anchorNode.setParent(arFragment.arSceneView.scene)
                    placeObject(anchorNode, currentModel)

                }
            } else {
                snackbar.showmessage(this, "Searching for plane")
            }
        }
        catch (e:Exception){
            e.printStackTrace()
        }
    }

    private fun getScreenCenter(): Point? {
        val view = findViewById<View>(android.R.id.content)
        return Point(view.width / 2, view.height / 2)
    }

    private fun HasPlane(): Boolean {
        try {
            for (plane in arFragment.arSceneView.session!!.getAllTrackables(
                    Plane::class.java
            )) {
                if(plane.trackingState==TrackingState.TRACKING && plane.isPoseInPolygon(plane.centerPose)){return true}
            }
        } catch (e: Exception) {
            return false
        }
        return false
    }


    private fun placeObject(anchornode: AnchorNode, currentModel: letterAndasset) {
        objectRequried=false
        ModelRenderable.builder()
                .setSource(arFragment.context, currentModel.asset)
                .build()
                .thenAccept { modelRenderable: ModelRenderable? -> addNode(anchornode, modelRenderable!!, currentModel) }
                .exceptionally { throwable: Throwable? ->

                    null }
    }

    private fun addNode(anchorNode: AnchorNode, modelRenderable: ModelRenderable, currentModel: letterAndasset){
        val animalnode = TransformableNode(arFragment.transformationSystem)
        animalnode.renderable=modelRenderable
        animalnode.setParent(anchorNode)
        animalnode.name=currentModel.name

        animalnode.setOnTapListener { hitTestResult, motionEvent ->
            animalnode.select()
            Speech.speak(" "+animalnode.name+" ",TextToSpeech.QUEUE_FLUSH, null, null)
        }

        val letternode = LetterNode()
        letternode.name=currentModel.letter
        letternode.setParent(animalnode)
        val calculatedpos = Vector3(letternode.parent!!.localPosition.x + 1f, letternode.parent!!.localPosition.y, letternode.parent!!.localPosition.z)

        letternode.localPosition = calculatedpos

        letternode.setOnTapListener { hitTestResult, motionEvent ->

            Speech.speak(" "+letternode.name+" ",TextToSpeech.QUEUE_FLUSH, null, null)
        }

        ModelRenderable.builder()
                .setSource(arFragment.context, currentModel.letterasset)
                .build()
                .thenAccept { modelRenderable: ModelRenderable? -> letternode.renderable = modelRenderable }
                .exceptionally { throwable: Throwable? ->

                    null }





    }

    override fun onStop() {
        super.onStop()
        Speech.shutdown()
    }

}