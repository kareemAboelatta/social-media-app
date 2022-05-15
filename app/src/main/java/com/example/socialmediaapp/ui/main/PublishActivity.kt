package com.example.socialmediaapp.ui.main

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.provider.MediaStore.ACTION_IMAGE_CAPTURE
import android.view.LayoutInflater
import android.view.SoundEffectConstants
import android.view.View
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.RequestManager
import com.example.socialmediaapp.R
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_publish.*
import javax.inject.Inject
import com.example.socialmediaapp.models.Post
import com.example.socialmediaapp.models.User

import androidx.core.content.FileProvider

import android.os.Environment
import com.example.socialmediaapp.utils.Status
import java.io.File


@AndroidEntryPoint
class PublishActivity : AppCompatActivity() {



    //permission constants
    private val CAMERA_REQUEST_CODE = 100
    private val STORAGE_REQUEST_CODE = 101

    //image pick constants
    private val IMAGE_PICK_CAMERA_CODE = 102
    private val IMAGE_PICK_GALLERY_CODE = 103

    //video pick constants
    private val VIDEO_PICK_CAMERA_CODE = 104
    private val VIDEO_PICK_GALLERY_CODE = 105


    var imageUri: Uri? = null
    var videoUri: Uri? = null


    //array of permission
    lateinit var  cameraPermissions : Array<String>
    lateinit var  storagePermissions : Array<String>

    private val viewModel by viewModels<ViewModelMain>()

    lateinit var thisUser : User

    @Inject
    lateinit var glide:RequestManager

    @Inject
    lateinit var myContext: Context


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_publish)

        cameraPermissions =arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        storagePermissions =arrayOf( Manifest.permission.WRITE_EXTERNAL_STORAGE)

        viewModel.getDataForCurrentUser()
        viewModel.currentUserLiveData.observe(this) {
            when (it.status) {
                Status.SUCCESS -> {
                    thisUser = it.data!!
                    glide.load(thisUser.image).into(publish_my_image)
                    publish_my_name.text = thisUser.name
                }
                Status.ERROR -> {
                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        publish_btn_publish.setOnClickListener {
            var caption=publish_caption.text.toString()
            var fans=pubish_text_anyone.text.toString()
            if (caption.isEmpty()){
                Toast.makeText(this, "Enter caption", Toast.LENGTH_SHORT).show()
                return@setOnClickListener;
            }

            var postType: String = "article"
            var postAttachment: String = ""
            if (imageUri == null && videoUri == null) {
                //just article
                postType = "article"
                postAttachment = ""

            } else if (imageUri != null && videoUri == null) {
                //image
                postType = "image"
                postAttachment = "${imageUri.toString()}"

            } else if (imageUri == null && videoUri != null) {
                //video
                postType = "video"
                postAttachment = "${videoUri.toString()}"

            }
            var post = Post(
                thisUser.id, thisUser.name, thisUser.email, thisUser.image, caption,
                "", "" + fans, "$postType", "$postAttachment"
            )

            viewModel.identifyLanguage(caption)
            viewModel.languageIdentifierLiveData.observe(this){
                when(it.status){
                    Status.SUCCESS->{
                        post.languageCode = it.data.toString()
                        viewModel.uploadPost(post)
                        viewModel.postLiveData.observe(this){
                            when(it.status){
                                Status.SUCCESS->{
                                    Toast.makeText(myContext, "Post Published", Toast.LENGTH_SHORT).show()
                                    startActivity(Intent(this, MainActivity::class.java))
                                    finish()
                                }
                                Status.ERROR->{
                                    Toast.makeText(myContext, "${it.message}", Toast.LENGTH_SHORT).show()

                                }
                            }
                        }
                    }
                    Status.ERROR->{
                        Toast.makeText(myContext, "${it.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }


        }


        publish_btn_anyone.setOnClickListener {
            var popupMenu= PopupMenu(this, publish_btn_anyone)
            popupMenu.menuInflater.inflate(R.menu.anyone_menu, popupMenu.menu)
           // popupMenu.menu.removeItem(R.id.logout)
            popupMenu.setOnMenuItemClickListener{ item ->
                when (item.itemId) {
                    R.id.menu_anyone ->
                        pubish_text_anyone.text="Anyone"
                    R.id.menu_friends ->
                        pubish_text_anyone.text="Friends"
                    R.id.menu_only_me ->
                        pubish_text_anyone.text="Only me"
                }
                true
            }
            popupMenu.show()
        }
        publish_btn_bottom.setOnClickListener {
            var bottomSheetDialog=BottomSheetDialog(this,R.style.BottomSheetStyle)

            var sheetView= LayoutInflater.from(applicationContext)
                .inflate(R.layout.bottom_dialog,
                    findViewById(R.id.dialog_container))

            sheetView.findViewById<LinearLayout>(R.id.bottom_image).setOnClickListener {
                imagePickDialog()
                publish_caption.setLines(4)
                bottomSheetDialog.dismiss()
            }
            sheetView.findViewById<LinearLayout>(R.id.bottom_video).setOnClickListener {
                videoPickDialog()
                publish_caption.setLines(4)
                bottomSheetDialog.dismiss()
            }
            sheetView.findViewById<LinearLayout>(R.id.bottom_article).setOnClickListener {
                Toast.makeText(this, "article", Toast.LENGTH_SHORT).show()
                bottomSheetDialog.dismiss()
                publish_caption.setLines(12)
                publish_video.visibility=View.GONE
                publish_image.visibility=View.GONE
                imageUri=null
                videoUri=null
            }
            sheetView.findViewById<LinearLayout>(R.id.bottom_attache).setOnClickListener {
                publish_video.visibility=View.GONE
                publish_image.visibility=View.GONE
                imageUri=null
                videoUri=null

                Toast.makeText(this, "attache", Toast.LENGTH_SHORT).show()
            }

            sheetView.findViewById<ImageView>(R.id.bottom_close).setOnClickListener {
                bottomSheetDialog.dismiss()
            }


            bottomSheetDialog.setContentView(sheetView)
            bottomSheetDialog.show()




        }
    }

    private fun imagePickDialog() {
        val options = arrayOf("Camera", "Gallery")
        //dialog
        val builder = AlertDialog.Builder(this)

        //title
        builder.setTitle("Pick image From ?!")
        builder.setCancelable(false)

            builder.setItems(options) { dialog, which ->
                if (which == 0) {
                    //camera clicked
                    if (!checkCameraPermission()) {
                        requestCameraPermission()
                    } else {
                        imagePickCamera()
                    }
                } else if (which == 1) {
                    // gallery clicked
                    if (!checkStoragePermission()) {
                        requestStoragePermission()
                    }else{
                        imagePickGallery()
                    }
                }

        }

        //create show dialog
        builder.create().show()
    }

    // check internet Connection

    private fun videoPickDialog() {
        val options = arrayOf("Camera", "Gallery")
        //dialog
        val builder = AlertDialog.Builder(this)

        //title
        builder.setTitle("Pick video from ?!")
        builder.setCancelable(false)

        builder.setItems(options) { dialog, which ->
            if (which == 0) {
                //camera clicked
                if (!checkCameraPermission()) {
                    requestCameraPermission()
                } else {
                    videoPickCamera()
                }
            } else if (which == 1) {
                // gallery clicked
                if (!checkStoragePermission()) {
                    requestStoragePermission()
                } else {
                    videoPickGallery()
                }
            }
        }


        //create show dialog
        builder.create().show()
    }

    private fun videoPickGallery() {
        //pick from camera _ intent
        val intent = Intent()
        intent.type = "video/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(
            Intent.createChooser(intent, "Select Videos"),
            VIDEO_PICK_GALLERY_CODE
        )
    }
    private  fun videoPickCamera() {
        //pick from camera _ intent
        val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
        startActivityForResult(intent, VIDEO_PICK_CAMERA_CODE)
    }
    private fun imagePickGallery() {
        //pick from gallery _ intent
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent,IMAGE_PICK_GALLERY_CODE)
    }
    private  fun imagePickCamera() {
        //pick from camera _ intent
        /*val cameraintent = Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(cameraintent, IMAGE_PICK_CAMERA_CODE)*/

        val m_intent = Intent(ACTION_IMAGE_CAPTURE)
        val file = File(Environment.getExternalStorageDirectory(), "MyPhoto.jpg")
        val uri = FileProvider.getUriForFile(this, this.applicationContext.packageName + ".provider", file)
        m_intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
        startActivityForResult(m_intent, IMAGE_PICK_CAMERA_CODE)
    }
    private  fun requestCameraPermission() {
        //check if camera permission is enabled or not
        ActivityCompat.requestPermissions(
            this,
            cameraPermissions,
            CAMERA_REQUEST_CODE
        )
    }
    private  fun requestStoragePermission() {
        //check if camera permission is enabled or not
        ActivityCompat.requestPermissions(
            this,
            storagePermissions,
            STORAGE_REQUEST_CODE
        )
    }
    private fun checkCameraPermission(): Boolean {
        //check if camera permission is enabled or not
        val result = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        val result1 = ContextCompat.checkSelfPermission(this, Manifest.permission.WAKE_LOCK) == PackageManager.PERMISSION_GRANTED
        return result && result1
    }

    private fun checkStoragePermission(): Boolean {
        //check if camera permission is enabled or not
        val result = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        return result
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            CAMERA_REQUEST_CODE -> if (grantResults.isNotEmpty()) {
                //check
                val cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED
                val storageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED
                if (cameraAccepted && storageAccepted) {
                   // videoPickCamera()
                } else {
                    Toast.makeText(
                        this,
                        "Camera & Storage Permission are required",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            STORAGE_REQUEST_CODE -> if (grantResults.isNotEmpty()) {
                //check
                val storageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED
                if ( storageAccepted) {
                    //videoPickCamera()
                } else {
                    Toast.makeText(this, "Storage Permission are required", Toast.LENGTH_SHORT).show()
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK) {
            if (requestCode ==  IMAGE_PICK_GALLERY_CODE || requestCode ==  IMAGE_PICK_CAMERA_CODE ) {
                videoUri=null
                //show picked image
                publish_image.visibility= View.VISIBLE
                publish_video.visibility=View.GONE
                if (requestCode==IMAGE_PICK_GALLERY_CODE){
                    imageUri=data?.data
                }else{

                    //File object of camera image
                    val file = File(Environment.getExternalStorageDirectory(), "MyPhoto.jpg")
                    //Uri of camera image
                    val uri = FileProvider.getUriForFile(this,
                        this.applicationContext.packageName + ".provider", file)
                    imageUri=uri
                    Toast.makeText(this, ""+imageUri, Toast.LENGTH_SHORT).show()

                }
                publish_image.setImageURI(imageUri)

            }else if (requestCode ==  VIDEO_PICK_GALLERY_CODE || requestCode ==  VIDEO_PICK_CAMERA_CODE){
                videoUri = data?.data
                imageUri=null
                //show picked video
                publish_image.visibility= View.GONE
                publish_video.visibility=View.VISIBLE
                setVideoToVideoView()
            }

        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun setVideoToVideoView() {
        val mediaController = MediaController(this)
        mediaController.setAnchorView(publish_video)
        // set media controller to Video view
        publish_video.setMediaController(mediaController)
        publish_video.setVideoURI(videoUri)

        publish_video.setOnPreparedListener{
            publish_video.start()
            mediaController.show()
            mediaController.playSoundEffect(SoundEffectConstants.NAVIGATION_DOWN)
        }
    }

}