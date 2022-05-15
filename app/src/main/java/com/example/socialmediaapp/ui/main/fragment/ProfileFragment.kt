package com.example.socialmediaapp.ui.main.fragment

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.RequestManager
import com.example.socialmediaapp.R
import com.example.socialmediaapp.ui.main.ViewModelMain
import com.example.socialmediaapp.adapter.AdapterPost
import com.example.socialmediaapp.models.Post
import com.example.socialmediaapp.ui.main.MainActivity
import com.example.socialmediaapp.ui.sign.LoginAndSignUpActivity
import com.example.socialmediaapp.utils.Status
import com.google.firebase.auth.FirebaseAuth
import com.ismaeldivita.chipnavigation.ChipNavigationBar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_profile.prof_bio
import kotlinx.android.synthetic.main.fragment_profile.prof_image_cover
import kotlinx.android.synthetic.main.fragment_profile.prof_image_profile
import kotlinx.android.synthetic.main.fragment_profile.prof_name
import kotlinx.android.synthetic.main.fragment_profile.prof_rec
import javax.inject.Inject


@AndroidEntryPoint
class ProfileFragment  : Fragment(R.layout.fragment_profile) {

    @Inject
    lateinit var auth: FirebaseAuth
    @Inject
    lateinit var glide: RequestManager
    @Inject
    lateinit var adapterPosts: AdapterPost


    var uriImage: Uri? = null
    var uriImageCover: Uri? = null

    lateinit var prog: ProgressDialog
    lateinit var postList : ArrayList<Post>


    private val IMAGE_REQUEST=0
    private val Cover_REQUEST=1

    private val viewModel by viewModels<ViewModelMain>()


    var name: String? = null
    var bio: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        postList= ArrayList()
        prog= ProgressDialog(activity)
        prog.setMessage("Wait a minute...")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        recyclerViewSetUp()


        prof_btn_change_profile.setOnClickListener {
            showAlertDialogForChangePhotos("profile")
        }
        prof_btn_change_cover.setOnClickListener {
            showAlertDialogForChangePhotos("cover")
        }
        prof_btn_edit_pen.setOnClickListener {
            var popupMenu= PopupMenu(activity, prof_btn_edit_pen)
            popupMenu.menuInflater.inflate(R.menu.pop_menu, popupMenu.menu)
            popupMenu.menu.removeItem(R.id.logout)
            popupMenu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.change_name ->
                        showUpdateNameBioDialog("name")
                    R.id.change_bio ->
                        showUpdateNameBioDialog("bio")
                }
                true
            }
            popupMenu.show()
        }
        prof_btn_setting.setOnClickListener {
            var popupMenu= PopupMenu(activity, prof_btn_setting)
            popupMenu.menuInflater.inflate(R.menu.pop_menu, popupMenu.menu)

            popupMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.change_name ->
                        showUpdateNameBioDialog("name")
                    R.id.change_bio ->
                        showUpdateNameBioDialog("bio")
                    R.id.logout -> {
                        auth.signOut()
                        startActivity(Intent(activity, LoginAndSignUpActivity::class.java))
                    }
                }
                true
            })
            popupMenu.show()
        }



        viewModel.getPostsForSpecificUser(auth.currentUser?.uid!!)
        viewModel.getSpecificUserData(auth.currentUser?.uid!!)

        //observe Posts from viewModel
        viewModel.postsForSpecificUserLiveData.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.LOADING -> {
                    prof_ProgressBar?.visibility = View.VISIBLE
                }
                Status.SUCCESS -> {
                    prof_ProgressBar.visibility = View.GONE
                    postList = it.data as ArrayList<Post>
                    adapterPosts.setList(postList)
                }
                Status.ERROR -> {
                    prof_ProgressBar?.visibility = View.GONE
                    Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                }
            }

        }
        adapterPosts.setonItemClickListenerForLike {
            viewModel.setLike(it)
        }
        //observe data for this user from viewModel
        viewModel.specificUserLiveData.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    val user = it.data
                    prof_name.text = user?.name
                    prof_bio.text = user?.bio

                    name = user?.name
                    bio = user?.bio
                    glide.load(user?.image).error(R.drawable.ic_profile).into(prof_image_profile)


                    glide.load(user?.cover).error(R.drawable.ic_image_default)
                        .into(prof_image_cover)
                    prof_image_cover.scaleType = ImageView.ScaleType.CENTER_CROP

                    prog.dismiss()

                }

                Status.ERROR -> {

                }

            }



        }


        viewModel.changePhotoOrCoverLiveData.observe(viewLifecycleOwner){
            when(it.status){
                Status.SUCCESS->{
                    prog.dismiss()
                    Toast.makeText(activity, "Photo changed", Toast.LENGTH_SHORT).show()
                }
                Status.ERROR->{
                    prog.dismiss()
                    Toast.makeText(activity, "${it.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }

        }

    fun recyclerViewSetUp(){
        //put linearLayout in recycle
        val linearLayout = LinearLayoutManager(activity)
        linearLayout.stackFromEnd = true
        linearLayout.reverseLayout = true
        prof_rec.layoutManager=linearLayout
        adapterPosts.setList(postList)
        prof_rec.adapter=adapterPosts
    }

    private fun showUpdateNameBioDialog(key: String){
        val builder = AlertDialog.Builder(activity)
        builder.setTitle("Update $key")
        builder.setCancelable(false)
        val linearLayout = LinearLayout(activity)
        linearLayout.orientation = LinearLayout.VERTICAL
        linearLayout.setPadding(10, 10, 10, 10)


        val editText = EditText(activity)
        if (key == "name"){
            editText.hint = ""+name
        }else if(key == "bio"){
            editText.hint=""+bio
        }
        editText.setHintTextColor(resources.getColor(R.color.colorGray))
        linearLayout.addView(editText)
        builder.setView(linearLayout)


        builder.setPositiveButton("Update") { _, _ ->
            val value=editText.text.toString()
            if (value==null){
                Toast.makeText(activity, "Where's Your new $key", Toast.LENGTH_SHORT).show()
            }else{
                //update key
                viewModel.changeNameOrBio(value,key)
                prog.show()
                viewModel.changeNameOrBioLiveData.observe(viewLifecycleOwner){
                    when(it.status){
                        Status.SUCCESS->{
                            prog.dismiss()
                            Toast.makeText(activity, "$key changed", Toast.LENGTH_SHORT).show()
                        }
                        Status.ERROR->{
                            prog.dismiss()
                            Toast.makeText(activity, "${it.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }


        }

        builder.setNegativeButton("Cancel") { _, _ ->


        }

        val myAlertDialog: AlertDialog = builder.create()
        myAlertDialog.setOnShowListener {
            myAlertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(resources.getColor(R.color.colorGreen));
            myAlertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(resources.getColor(R.color.red));
            myAlertDialog.getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(resources.getColor(R.color.red));
        }
        myAlertDialog.show()

    }
    private fun showAlertDialogForChangePhotos(type: String){
        val alertDialog: AlertDialog.Builder = AlertDialog.Builder(activity)
        // Setting Alert Dialog Title
        alertDialog.setTitle("Are you sure,You want change $type photo?")
        // Icon Of Alert Dialog
        alertDialog.setIcon(R.drawable.ic_warning)
        alertDialog.setCancelable(false)

        if (type == "profile"){
            alertDialog.setPositiveButton("Yes") { dialogInterface, which ->


                if (!hasReadExternalStoragePermission())
                    requestPermission()
                else
                    changeProfilePhoto()


            }
            alertDialog.setNegativeButton("Close") {dialogInterface, which ->  }
        }else{
            alertDialog.setPositiveButton("Yes") { dialogInterface, which ->
                if (!hasReadExternalStoragePermission())
                    requestPermission()
                else
                    changeCoverPhoto()

            }
            alertDialog.setNegativeButton("Close") {dialogInterface, which ->}

        }
        alertDialog.setNeutralButton("Cancel"){ dialogInterface, which -> }


        val myAlertDialog: AlertDialog = alertDialog.create()
        myAlertDialog.setOnShowListener {
            myAlertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(resources.getColor(R.color.colorGreen));
            myAlertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(resources.getColor(R.color.red));
            myAlertDialog.getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(resources.getColor(R.color.red));
        }
        myAlertDialog.show()
    }
    private fun changeProfilePhoto() {
        Intent(Intent.ACTION_GET_CONTENT).also {
            it.type="image/*"
            startActivityForResult(it, IMAGE_REQUEST)
        }
    }
    private fun changeCoverPhoto() {
        Intent(Intent.ACTION_GET_CONTENT).also {
            it.type="image/*"
            this.startActivityForResult(it, Cover_REQUEST)
        }
    }
    private fun hasReadExternalStoragePermission()= activity?.let {
        ActivityCompat.checkSelfPermission(it, Manifest.permission.READ_EXTERNAL_STORAGE)} == PackageManager.PERMISSION_GRANTED


    private fun requestPermission(){
        var permissionsToRequest= mutableListOf<String>()
        if (!hasReadExternalStoragePermission()){
            permissionsToRequest.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        if (permissionsToRequest.isNotEmpty()){
            activity?.let { ActivityCompat.requestPermissions(
                it,
                permissionsToRequest.toTypedArray(),
                0
            ) }
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode==0  && grantResults.isNotEmpty()){
            for (i in grantResults.indices){
                if(grantResults[i] == PackageManager.PERMISSION_GRANTED){
                    Log.d("PermissionRequest", "${permissions[i]} granted.")
                }
            }

        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //profile
        if (resultCode== Activity.RESULT_OK && requestCode == IMAGE_REQUEST){
            uriImage=data?.data

            uriImage?.let {
                viewModel.changePhotoOrCover(it,"images","image")
                prog.show()
                observeChangePhotoOrCover()
            }
        }


        //cover
        if (resultCode== Activity.RESULT_OK && requestCode == Cover_REQUEST){
            uriImageCover=data?.data
            uriImageCover?.let {
                viewModel.changePhotoOrCover(it,"covers","cover")
                prog.show()
                observeChangePhotoOrCover()
            }
        }
    }





    fun observeChangePhotoOrCover(){
        viewModel.changePhotoOrCoverLiveData.observe(viewLifecycleOwner){
            when(it.status){
                Status.SUCCESS->{
                    prog.dismiss()
                    Toast.makeText(activity, "Photo changed", Toast.LENGTH_SHORT).show()
                }
                Status.ERROR->{
                    prog.dismiss()
                    Toast.makeText(activity, "${it.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }




}



