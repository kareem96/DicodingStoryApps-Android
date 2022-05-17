package com.kareemdev.dicodingstory.presentation.add

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Location
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.FileProvider
import androidx.exifinterface.media.ExifInterface
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import com.bumptech.glide.load.resource.bitmap.TransformationUtils
import com.kareemdev.dicodingstory.R
import com.kareemdev.dicodingstory.databinding.ActivityAddStoryBinding
import com.kareemdev.dicodingstory.presentation.home.HomeActivity
import com.kareemdev.dicodingstory.presentation.home.HomeActivity.Companion.EXTRA_TOKEN
import com.kareemdev.dicodingstory.utils.MediaUtils
import com.kareemdev.dicodingstory.utils.animateVisibility
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.lang.Exception

@AndroidEntryPoint
@ExperimentalPagingApi
class AddStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddStoryBinding
    private lateinit var currentPhotoPath: String
    private var getFile: File? = null
    private var location: Location? = null
    private var lat: RequestBody? = null
    private var lon: RequestBody? = null
    private var token: String = ""
    private val viewModel: AddStoryViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        viewModel.viewModelScope.launch {
            viewModel.getAuthToken().collect {
                token = it!!
            }
        }
        binding.btnCamera.setOnClickListener {
            openCamera()
        }
        binding.btnGallery.setOnClickListener {
            openGallery()
        }
        binding.btnPost.setOnClickListener {
            startUpload()
        }
    }

    private fun openGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, " Choose a picture")
        launcherGallery.launch(chooser)
    }

    private val launcherGallery =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val selectedImg: Uri = result.data?.data as Uri
                MediaUtils.uriToFile(selectedImg, this).also { getFile = it }

                binding.imageView.setImageURI(selectedImg)
            }
        }

    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(packageManager)

        MediaUtils.createTempFile(application).also {
            val photoUri = FileProvider.getUriForFile(
                this,
                "com.kareemdev.dicodingstory",
                it
            )
            currentPhotoPath = it.absolutePath
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
            launcherCamera.launch(intent)
        }
    }

    private val launcherCamera = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val file = File(currentPhotoPath).also { getFile = it }
                val os: OutputStream
                val bitmap = BitmapFactory.decodeFile(getFile?.path)
                val exif = ExifInterface(currentPhotoPath)
                val orientation: Int = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED
                )

                val rotatedBitmap: Bitmap = when (orientation) {
                    ExifInterface.ORIENTATION_ROTATE_90 -> TransformationUtils.rotateImage(
                        bitmap,
                        90
                    )
                    ExifInterface.ORIENTATION_ROTATE_180 -> TransformationUtils.rotateImage(
                        bitmap,
                        180
                    )
                    ExifInterface.ORIENTATION_ROTATE_270 -> TransformationUtils.rotateImage(
                        bitmap,
                        270
                    )
                    ExifInterface.ORIENTATION_NORMAL -> bitmap
                    else -> bitmap
                }

                // Convert rotated image to file
                try {
                    os = FileOutputStream(file)
                    rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, os)
                    os.flush()
                    os.close()

                    getFile = file
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                binding.imageView.setImageBitmap(rotatedBitmap)
            }
        }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }


    private fun startUpload() {
        if (getFile != null) {
            stateLoading(true)
            val desc = binding.etDescription
            val file = MediaUtils.reduceFileImage(getFile as File)
            val description =
                binding.etDescription.text.toString().toRequestBody("text/plain".toMediaType())
            val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                "photo", file.name, requestImageFile
            )

            if (location != null) {
                lat = location?.latitude.toString().toRequestBody("text/plain".toMediaTypeOrNull())
                lon = location?.longitude.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            }

            viewModel.viewModelScope.launch {
                viewModel.uploadImage(token, imageMultipart, description, lat, lon)
                    .collect { response ->
                        response.onSuccess {
                            Intent(this@AddStoryActivity, HomeActivity::class.java).also {
                                it.apply {
                                    putExtra(EXTRA_TOKEN, token)
                                    flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                                }
                                startActivity(it)
                                finish()
                            }
                        }
                        response.onFailure {
                            if (desc.text.toString().isEmpty()) {
                                Toast.makeText(
                                    this@AddStoryActivity,
                                    "Success upload",
                                    Toast.LENGTH_SHORT
                                ).show()
                                stateLoading(false)
                            } else {
                                Toast.makeText(
                                    this@AddStoryActivity,
                                    "Failed upload",
                                    Toast.LENGTH_SHORT
                                ).show()
                                stateLoading(false)
                            }
                        }
                    }
            }

        } else {
            stateLoading(false)
            Toast.makeText(
                this,
                getString(R.string.upload_warning),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun stateLoading(b: Boolean) {
        binding.apply {
            btnCamera.isEnabled = !b
            btnGallery.isEnabled = !b
            etDescription.isEnabled = !b

            viewLoading.animateVisibility(b)
        }
    }
}