package com.example.pre26

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.example.pre26.databinding.ActivityNewFeaturesBinding

class NewFeaturesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNewFeaturesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityNewFeaturesBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        binding.sendChatNotificationButton.setOnClickListener {
            NotificationHelper(this@NewFeaturesActivity)
                .showNotification()
        }
    }

    override fun onResume() {
        super.onResume()

        binding.replyTextView.text = DIUtils.replyRepository.replyText
    }
}