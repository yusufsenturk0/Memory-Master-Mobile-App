package com.example.memortymaster

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.drawable.Drawable
import android.media.Image
import android.os.Bundle
import android.os.SystemClock
import android.renderscript.ScriptGroup.Binding
import android.view.View
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.accessibility.AccessibilityManagerCompat.TouchExplorationStateChangeListener
import com.example.memortymaster.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var opendCard=0
    private  val  opendCardList: ArrayList<ImageView> = arrayListOf()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        enableEdgeToEdge()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val gridLayout = findViewById<GridLayout>(R.id.gridLayout)

        val images = listOf(
            R.drawable.ananas1,
            R.drawable.ananas1,
            R.drawable.elma1,
            R.drawable.elma1,
            R.drawable.erik1,
            R.drawable.erik1,
            R.drawable.limon1,
            R.drawable.limon1,
            R.drawable.avakado2,
            R.drawable.avakado2,
            R.drawable.mango1,
            R.drawable.mango1,
            R.drawable.muz1,
            R.drawable.muz1,
            R.drawable.uzum1,
            R.drawable.uzum1,

        ).shuffled() // Resimleri karıştırmak için shuffled() kullanılır.
        var uniqId=1
        for (image in images) {
            val imageView = ImageView(this).apply {
                layoutParams = GridLayout.LayoutParams().apply {
                    width = 250 // Görüntü genişliği
                    height = 250 // Görüntü yüksekliği
                    setMargins(8, 8, 8, 8)

                }
                scaleType = ImageView.ScaleType.CENTER_CROP
                setImageDrawable(ContextCompat.getDrawable(context, image))
                id = uniqId
                this.alpha=0.0f
                uniqId++
            }
            imageView.setOnClickListener {
                if(imageView.alpha==1.0f){
                    println("skdjfhksfjkls")
                    Toast.makeText(this@MainActivity,"Açılmış Görsele Neden Tıklıyon",Toast.LENGTH_LONG).show()
                }
                else {
                    opendCard++
                    if(opendCard<2){
                        opendCardList.add(imageView)

                        imageView.alpha = if (imageView.alpha == 1.0f) {
                            animateAlpha(imageView,1.0f)
                            0.0f // Görünmez yap

                        } else {
                            animateAlpha(imageView,0.0f)
                            1.0f // Görünür yap
                        }
                    }else{
                        opendCardList.add(imageView)

                        imageView.alpha = if (imageView.alpha == 1.0f) {
                            animateAlpha(imageView,1.0f)
                            0.0f // Görünmez yap

                        } else {
                            animateAlpha(imageView,0.0f)
                            1.0f // Görünür yap
                        }

                        val image1=opendCardList[0].drawable
                        val image2=opendCardList[1].drawable

                        println(image1)
                        println(image2)

                        if(image1?.constantState == image2?.constantState){

                            opendCardList.clear()
                            opendCard=0
                        }else{
                            animateAlpha(opendCardList[0],1.0f)
                            animateAlpha(opendCardList[1],1.0f)

                            opendCardList.clear()
                            opendCard=0
                        }
                    }
                }




            }
            gridLayout.addView(imageView)
        }



        //Timer Created
        binding.buttonStart.setOnClickListener {
            binding.chronomater.base=SystemClock.elapsedRealtime()
            binding.chronomater.start()
        }

    }
    private fun animateAlpha(imageView: ImageView,alpha : Float) {
        // Alpha değerini 0.0'dan 1.0'a değiştiren ObjectAnimator
         var start : Float
         var finish : Float
         if(alpha==1.0f){
             start=1.0f
             finish=0.0f
         }else{
            start=0.0f
            finish=1.0f
        }


        val alphaAnimator = ObjectAnimator.ofFloat(imageView, "alpha", start, finish)
        alphaAnimator.duration = 1000 // 1 saniye süre

        // AnimatorSet ile animasyonu çalıştır
        val animatorSet = AnimatorSet()
        animatorSet.play(alphaAnimator)
        animatorSet.start()
    }





}