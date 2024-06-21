package com.example.memortymaster

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.memortymaster.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private  var scoreId :Long=0
    private lateinit var dataBase: DataBase
    private var isRunning =false
    private var score : Long =0
    private val intent = Intent()
    private var clicked : Boolean=false
    private val imageViews :ArrayList<ImageView> = arrayListOf()
    private var opendCard=0
    private  val  opendCardList: ArrayList<ImageView> = arrayListOf()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        dataBase = DataBase(this)
        if (dataBase.isScorePresent()) {
            // Mevcut bir skor varsa, bu skoru kullanın
            scoreId = dataBase.getFirstRecordId() ?: 0
        } else {
            // Mevcut bir skor yoksa yeni bir skor ekleyin
            scoreId = dataBase.insertLongValue(0)
        }

        printMaxScore()
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
            imageViews.add(imageView)
            imageView.setOnClickListener {
                if(clicked){
                    if(imageView.alpha==1.0f){

                        Toast.makeText(this@MainActivity,"Açılmış Görsele Neden Tıklıyon",Toast.LENGTH_SHORT).show()
                    }
                    else {
                        imageView.isClickable=false

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



                            if(image1?.constantState == image2?.constantState){

                                opendCardList.clear()
                                opendCard=0
                                //oyun bitti ise activiteyi oyunu tekrar başlatıyor
                                if(gameFinish(imageViews)){

                                    runAfterDelay(700){

                                    }

                                }
                            }else{

                                for(image in opendCardList){
                                    image.isClickable=true
                                }

                                animateAlpha(opendCardList[0],1.0f)
                                animateAlpha(opendCardList[1],1.0f)

                                opendCardList.clear()
                                opendCard=0
                            }
                        }
                    }
                }else{
                    Toast.makeText(this@MainActivity,"Başlamadan önce süreyi başlatınız!!!",Toast.LENGTH_SHORT).show()

                }





            }
            gridLayout.addView(imageView)
        }



        //Timer Created
        binding.buttonStart.setOnClickListener {
            if(!isRunning){
                isRunning=true
                clicked=true
                binding.chronomater.base=SystemClock.elapsedRealtime()
                binding.chronomater.start()
                dontShowImg(imageViews)

                for (image in imageViews){
                    image.isClickable=true
                }

            }

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
        alphaAnimator.duration = 500 // 0.5 saniye süre

        // AnimatorSet ile animasyonu çalıştır
        val animatorSet = AnimatorSet()
        animatorSet.play(alphaAnimator)
        animatorSet.start()
    }

    //at beginning of app we change the visibility of the image
    private fun dontShowImg(images: List<ImageView>){
        for (image in images){
            runAfterDelay(200){
                animateAlpha(image,0.0f)
            }

        }
        runAfterDelay(200){

        }
        for (image in images){
            runAfterDelay(200){
                animateAlpha(image,1.0f)
            }

        }
    }


    private fun runAfterDelay(delayMillis: Long, action: () -> Unit) {
        Handler(Looper.getMainLooper()).postDelayed({
            action()
        }, delayMillis)
    }
    private fun gameFinish(images :List<ImageView>) : Boolean{
        for (image in images){
            if(image.alpha==0.0f){
                return false
            }
        }
        isRunning=false
        binding.chronomater.stop()

        score= (SystemClock.elapsedRealtime() - binding.chronomater.base)
        if(score< dataBase.getFirstRecordId()!!){
            dataBase.updateLongValue(scoreId,score)
        }


        printMaxScore()





        return true
    }
    private fun printMaxScore(){
        val score = dataBase.getLongValue(scoreId) ?: 0
        val seconds = (score / 1000) % 60
        val minutes = (score / (1000 * 60)) % 60
        val strScore = String.format("Max Score: %02d.%02d", minutes, seconds)
        binding.textViewScore.text = strScore
    }




}