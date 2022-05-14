package com.kareemdev.dicodingstory.presentation.add

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.FileProvider
import androidx.exifinterface.media.ExifInterface
import androidx.lifecycle.lifecycleScope
import androidx.paging.ExperimentalPagingApi
import com.bumptech.glide.load.resource.bitmap.TransformationUtils
import com.google.android.material.snackbar.Snackbar
import com.kareemdev.dicodingstory.databinding.ActivityAddStoryBinding
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
    private var token: String = ""
    private val viewModel: AddStoryViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        lifecycleScope.launchWhenCreated {
            launch {
                viewModel.getAuthToken().collect { auth ->
                    if (!auth.isNullOrEmpty()) token = auth
                }
            }
        }
        binding.btnCamera.setOnClickListener {
            openCamera()
        }
        binding.btnGallery.setOnClickListener {
            openGallery()
        }
        binding.btnPost.setOnClickListener {
            uploadStory()
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

    private val launcherCamera =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
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


    private fun uploadStory() {
        stateLoading(true)
        val edtDescription = binding.etDescription
        var isValid = true
        if (edtDescription.text.toString().isBlank()) {
            edtDescription.error = "Please this fill not be empty"
            isValid = false
        }
        if (getFile == null) {
            showSnackBar("Please select image")
            isValid = false
        }
        if (isValid) {
            lifecycleScope.launchWhenCreated {
                launch {
                    val description =
                        edtDescription.text.toString().toRequestBody("text/plain".toMediaType())
                    val file = MediaUtils.reduceFileImage(getFile as File)
                    val requesImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                    val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                        "photo",
                        file.name,
                        requesImageFile
                    )
                    val lat: RequestBody? = null
                    val lon: RequestBody? = null

                    viewModel.uploadImage(token, imageMultipart, description, lat, lon)
                        .collect { response ->
                            response.onSuccess {
                                Toast.makeText(
                                    this@AddStoryActivity,
                                    "Success upload",
                                    Toast.LENGTH_SHORT
                                ).show()
                                finish()
                            }
                            response.onFailure {
                                stateLoading(false)
                                showSnackBar("Upload failed")
                            }
                        }
                }
            }

        } else stateLoading(false)
    }

    private fun showSnackBar(s: String) {
        Snackbar.make(
            binding.root,
            s,
            Snackbar.LENGTH_SHORT
        ).show()
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