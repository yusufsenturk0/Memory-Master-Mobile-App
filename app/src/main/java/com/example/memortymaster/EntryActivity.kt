package com.example.memortymaster

import android.content.Intent
import android.os.Bundle
import android.widget.RadioButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.memortymaster.databinding.ActivityEntryBinding

class EntryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEntryBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityEntryBinding.inflate(layoutInflater)
        val view = binding.root

        setContentView(view)
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val intent= Intent(this , MainActivity::class.java)

        binding.radioButton1.isChecked=true
        binding.radioButton21.isChecked=true

        binding.buttonStart.setOnClickListener {
            val selectedRadioButtonId = binding.radioGroup.checkedRadioButtonId
            val selectedRadioButtonId2=binding.radioGroup2.checkedRadioButtonId

            val selectedRadioButton = findViewById<RadioButton>(selectedRadioButtonId)
            val selectedRadioButton2 = findViewById<RadioButton>(selectedRadioButtonId2)

            val selectedText = selectedRadioButton.text
            val selectedText2 = selectedRadioButton2.text

            intent.putExtra("field",selectedText)
            intent.putExtra("field2",selectedText2)

            startActivity(intent)
        }

    }
}