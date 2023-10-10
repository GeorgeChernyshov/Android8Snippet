package com.example.post26

import android.Manifest
import android.app.PictureInPictureParams
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Rect
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.support.annotation.RequiresApi
import android.support.constraint.ConstraintLayout
import android.support.constraint.ConstraintSet
import android.support.v4.app.ActivityCompat
import android.support.v4.app.LoaderManager
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Rational
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import com.example.post26.databinding.ActivityPipBinding
import java.io.File


class PipActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPipBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPipBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        binding.loadVideoButton.setOnClickListener {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED -> {
                    val i = Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
                    startActivityForResult(i, LOAD_VIDEO)
                }

                else -> {
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                        REQUEST_PERMISSION
                    )
                }
            }
        }

//        binding.videoView.addOnLayoutChangeListener { view, i, i2, i3, i4, i5, i6, i7, i8 ->
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                updatePipParams()
//            }
//        }

        binding.enterPipButton.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                    enterPictureInPictureMode()
                } else {
                    enterPictureInPictureMode(updatePipParams())
                }
            }
        }

        binding.goNextButton.setOnClickListener {
            startActivity(Intent(this, NewFeaturesActivity::class.java))
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                LOAD_VIDEO -> {
                    val video = getPath(data?.data)
                    binding.videoView.setVideoURI(Uri.fromFile(video?.let { File(it) }))
                    binding.videoView.start()
                }
                REQUEST_PERMISSION -> {
                    val i = Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
                    startActivityForResult(i, LOAD_VIDEO)
                }
            }
        }
    }

    private fun getPath(uri: Uri?): String? {
        val projection = arrayOf(MediaStore.Video.Media.DATA)
        val cursor = contentResolver.query(uri, projection, null, null, null)
        return if (cursor != null) {
            // HERE YOU WILL GET A NULLPOINTER IF CURSOR IS NULL
            // THIS CAN BE, IF YOU USED OI FILE MANAGER FOR PICKING THE MEDIA
            val column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Video.Media.DATA)
            cursor.moveToFirst()
            cursor.getString(column_index)
        } else null
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun updatePipParams(): PictureInPictureParams {
        val aspectRatio = Rational(binding.videoView.width, binding.videoView.height)

        val visibleRect = Rect()
        binding.videoView.getGlobalVisibleRect(visibleRect)

        val params = PictureInPictureParams.Builder()
            .setAspectRatio(aspectRatio)
//            .setSourceRectHint(visibleRect)
            .build()

        setPictureInPictureParams(params)

        return params
    }

    override fun onPictureInPictureModeChanged(
        isInPictureInPictureMode: Boolean,
        newConfig: Configuration?
    ) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig)

        with (binding) {
            if (isInPictureInPictureMode) {
                supportActionBar?.hide()
                loadVideoButton.visibility = View.GONE
                enterPipButton.visibility = View.GONE
                rootLayout.setPadding(0, 0, 0, 0)

                videoFrame.layoutParams = ConstraintLayout.LayoutParams(
                    MATCH_PARENT,
                    MATCH_PARENT
                )
            } else {
                supportActionBar?.show()
                loadVideoButton.visibility = View.VISIBLE
                enterPipButton.visibility = View.VISIBLE

                val padding = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    16f,
                    Resources.getSystem().displayMetrics
                ).toInt()

                rootLayout.setPadding(padding, padding, padding, padding)

                videoFrame.layoutParams = ConstraintLayout.LayoutParams(
                    MATCH_PARENT,
                    0
                ).apply { dimensionRatio = "16:9" }

                val constraintSet = ConstraintSet()
                constraintSet.clone(rootLayout)
                constraintSet.connect(
                    R.id.videoFrame,
                    ConstraintSet.TOP,
                    R.id.loadVideoButton,
                    ConstraintSet.BOTTOM
                )

                constraintSet.applyTo(rootLayout)
            }
        }
    }

    companion object {
        private const val LOAD_VIDEO = 0
        private const val REQUEST_PERMISSION = 1
    }
}