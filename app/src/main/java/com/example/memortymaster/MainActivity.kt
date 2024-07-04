package com.example.memortymaster

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.view.View
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

    private lateinit var combinedListEmoji: List<Int>
    private lateinit var combinedListFruits: List<Int>
    private  var scoreId :Long=0
    private lateinit var dataBase: DataBase
    private var isRunning =false
    private var score : Long =0
    private var health :Int = 0
    private var clicked : Boolean=false
    private val imageViews :ArrayList<ImageView> = arrayListOf()
    private var opendCard=0
    private  val  opendCardList: ArrayList<ImageView> = arrayListOf()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        AlertDialog.Builder(this)
            .setTitle("Hint")
            .setMessage("Match the cards in the shortest time possible.!!!(Watch your  heart)!!!")
            .setPositiveButton("Okay") { dialog, _ ->
                dialog.dismiss()
            }
            .show()

        val field=intent.getStringExtra("field")
        val field2=intent.getStringExtra("field2")

        if (field2=="4x4"){
            health=6
            binding.healthGridLayout.columnCount=6
            binding.gridLayout.columnCount=4
            binding.gridLayout.rowCount=4
        }else if(field2=="6x6"){
            health=10
            binding.healthGridLayout.columnCount=10
            binding.healthGridLayout.rowCount=1
            binding.gridLayout.columnCount=6
            binding.gridLayout.rowCount=6

        }

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

        var images_fruits = listOf(
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


        var images_emoji = listOf(
            R.drawable.e1,
            R.drawable.e1,
            R.drawable.e2,
            R.drawable.e2,
            R.drawable.e3,
            R.drawable.e3,
            R.drawable.e4,
            R.drawable.e4,
            R.drawable.e5,
            R.drawable.e5,
            R.drawable.e6,
            R.drawable.e6,
            R.drawable.e7,
            R.drawable.e7,
            R.drawable.e8,
            R.drawable.e8,

            ).shuffled()
        if(field2=="6x6"){
            increaseCards(images_emoji,images_fruits)
            images_emoji=combinedListEmoji
            images_fruits=combinedListFruits
        }
        else{

        }

        var images_chosen_subject= listOf<Int>()
        if(field=="Emojies"){
            images_chosen_subject=images_emoji
        }else if(field=="Fruits"){
            images_chosen_subject=images_fruits
        }
        showHealth(health)
        var uniqId=1
        for (image in images_chosen_subject) {
            val imageView = ImageView(this).apply {
                layoutParams = GridLayout.LayoutParams().apply {
                   if(field2=="6x6"){
                       width = 160 // Görüntü genişliği
                       height = 160 // Görüntü yüksekliği
                   }else{
                       width = 250 // Görüntü genişliği
                       height = 250 // Görüntü yüksekliği
                   }

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
                                health++
                                if(health>9){
                                    binding.healthGridLayout.columnCount=9
                                    binding.healthGridLayout.rowCount=2
                                }
                                showHealth(health)

                                opendCardList.clear()
                                opendCard=0
                                //oyun bitti ise activiteyi oyunu tekrar başlatıyor
                                if(gameFinish(imageViews)){

                                    runAfterDelay(700){

                                    }

                                }
                            }else{

                                health--
                                if(health<9){
                                    binding.healthGridLayout.columnCount=9
                                    binding.healthGridLayout.rowCount=1
                                }
                                showHealth(health)
                                if(health==0){
                                    Toast.makeText(this@MainActivity,"Game Over",Toast.LENGTH_LONG).show()
                                    binding.chronomater.stop()
                                    runAfterDelay(1000){

                                        val intent3= Intent(this,EntryActivity::class.java)
                                        startActivity(intent3)
                                    }
                                }


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
                    Toast.makeText(this@MainActivity,"Start the timer before playing!!!",Toast.LENGTH_SHORT).show()

                }





            }
            gridLayout.addView(imageView)
        }


        if(isRunning){
            binding.buttonStart.visibility= View.INVISIBLE
        }
        //Timer Created
        binding.buttonStart.setOnClickListener {
            if(!isRunning) {
                isRunning = true
                if(binding.buttonStart.text == "Home"){
                    val intent2= Intent(this,EntryActivity::class.java)
                    startActivity(intent2)
                }

                binding.buttonStart.visibility= View.VISIBLE
                binding.buttonStart.text = "Home"

                clicked = true
                binding.chronomater.base = SystemClock.elapsedRealtime()
                binding.chronomater.start()
                dontShowImg(imageViews)
                binding.chronomater.setTextColor(Color.BLACK)
                for (image in imageViews) {
                    image.isClickable = true
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
    private fun showHealth(health : Int){
        binding.healthGridLayout.removeAllViews()
        for (Id in 1..health){
            val imageView=ImageView(this).apply {
                layoutParams=GridLayout.LayoutParams().apply {
                    width=100
                    height=100

                    setMargins(4,4,4,4)
                }

                scaleType=ImageView.ScaleType.CENTER_CROP
                setImageDrawable(ContextCompat.getDrawable(context,R.drawable.health))
                id=Id

            }
            binding.healthGridLayout.addView(imageView)

        }
    }
    private fun increaseCards(images_emoji: List<Int>,images_fruits: List<Int>) {

        val images_emoji2= listOf(
            R.drawable.e9,
            R.drawable.e9,
            R.drawable.e10,
            R.drawable.e10,
            R.drawable.e11,
            R.drawable.e11,
            R.drawable.e12,
            R.drawable.e12,
            R.drawable.e13,
            R.drawable.e13,
            R.drawable.e14,
            R.drawable.e14,
            R.drawable.e15,
            R.drawable.e15,
            R.drawable.e16,
            R.drawable.e16,
            R.drawable.e17,
            R.drawable.e17,
            R.drawable.e18,
            R.drawable.e18,
        ).shuffled().shuffled().shuffled()
        val images_fruits2=listOf(
            R.drawable.tropik,
            R.drawable.tropik,
            R.drawable.kiraz,
            R.drawable.kiraz,
            R.drawable.karpuz,
            R.drawable.karpuz,
            R.drawable.ayva,
            R.drawable.ayva,
            R.drawable.armut,
            R.drawable.armut,
            R.drawable.misir,
            R.drawable.misir,
            R.drawable.mandalina,
            R.drawable.mandalina,
            R.drawable.hinceviz,
            R.drawable.hinceviz,
            R.drawable.kayisi,
            R.drawable.kayisi,
            R.drawable.domates,
            R.drawable.domates,
        ).shuffled().shuffled().shuffled()

        combinedListEmoji=images_emoji+images_emoji2
        combinedListFruits=images_fruits+images_fruits2
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
        if(score<dataBase.getLongValue(scoreId)!!){
            dataBase.updateLongValue(scoreId,score)
        }


        binding.chronomater.setTextColor(Color.RED)

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