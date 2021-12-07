package com.mcdar.a3dassests

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.SeekBar
import kotlinx.android.synthetic.main.activity_home_screen.*

class HomeScreen : AppCompatActivity() {

    private val array = arrayListOf<Int>(
        R.drawable.floral_easter_egg,
        R.drawable.happymealtexture,
        R.drawable.goldentexture,
        R.drawable.mcflurrytexture,
        R.drawable.nuggetstexture,
        R.drawable.pommestexture
        )


    private val arrayStr = arrayListOf(
        "floral_easter_egg"
    )

    private var selectedTexture = R.drawable.bigmactexture
    private var factoreRoughness = 0f
    private var factoreMettalicity = 0f
    private var factoreInterpolar = 0.3f


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_screen)
        loadScree()
    }

    private fun loadScree() {
        img.setImageResource(selectedTexture)
        mettalicity.max = 10
        mettalicity.incrementProgressBy(1)
        roughness.max = 10
        roughness.incrementProgressBy(1)

        interpolar.max = 10
        interpolar.progress = 3
        textInterpolar.text = "Egg Size ${.3}"
        interpolar.incrementProgressBy(1)
        letsGo.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            val bundle = Bundle()
            val arTexture = ARTexture(factoreMettalicity, factoreRoughness, selectedTexture,
                0.0f, factoreInterpolar)
            bundle.putSerializable("arTexture", arTexture)
            intent.putExtras(bundle)
            startActivity(intent)
        }

        val adapter = ArrayAdapter<String>(
            this, android.R.layout.simple_spinner_dropdown_item,
            arrayStr
        )
        spinner.adapter = adapter
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (position < 6) {
                    selectedTexture = array[position]
                    img.setImageResource(selectedTexture)
                }
            }

        }

        mettalicity.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                factoreMettalicity = progress * 0.1f
                textMetal.text = "Metallicty ${factoreMettalicity}"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
               //not in use
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }


        })

        roughness.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                factoreRoughness = progress * 0.1f
                textRough.text = "Roughness ${factoreRoughness}"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }


        })

        interpolar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if(progress<3){
                   return
                }
                factoreInterpolar = progress * 0.1f
                textInterpolar.text = "Egg size ${factoreInterpolar}"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }


        })

    }
}
